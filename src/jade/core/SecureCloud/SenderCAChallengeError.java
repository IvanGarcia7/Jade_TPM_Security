package jade.core.SecureCloud;


import jade.core.*;
import jade.core.SecureTPM.Agencia;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;


public class SenderCAChallengeError extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private BaseService myService;
    private String Ontology;
    private PublicKey destinyKey;
    private String messagesender;
    private PlatformID origin;


    public SenderCAChallengeError(ACLMessage message, Agent amsMainPlatform, String requestError, PublicKey destinypub,
                                  String ms, PlatformID or) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        Ontology = requestError;
        destinyKey = destinypub;
        messagesender = ms;
        origin = or;
    }


    /**
     * THIS FUNCTION TAKES AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO IT NEEDS TO COMPLETE ALL THE INFORMATION
     * REQUIRED IN BASE OF THE MESSAGE THAT IT HAS RECEIVED AS A PARAM.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){
        try {
            byte [] encryptedKey = Agencia.encrypt(destinyKey,messagesender.getBytes());
            myMessage.setContentObject(encryptedKey);

            Agencia.printLog("PROCEEDING TO SEND THE ERROR MESSAGE IN THE PREPARE REQUEST METHOD", Level.INFO,
                             SecureCloudTPMHelper.DEBUG, this.getClass().getName());

            myMessage.addReceiver(origin.getAmsAID());
            myMessage.setOntology(Ontology);

            Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, Agencia.getTimeout());
            Date t = new Date(c.getTimeInMillis());
            myMessage.setReplyByDate(t);

            Agencia.printLog("MESSAGE CREATE CORRECTLY INTO THE SENDER CHALLENGE ERROR", Level.INFO,
                            SecureCloudTPMHelper.DEBUG, this.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myMessage;
    }

    //REDEFINED ALL THE HANDLERS TO KNOW WHAT HAPPENS WITH THE MESSAGE THAT THE PLATFORM RECEIVE FROM THE CA

    protected void handleInform(ACLMessage inform){
        System.out.println("RESPONSE FROM THE SECURE PLATFORM: "+inform.getContent());
    }

    protected void handleRefuse(ACLMessage inform){
        System.out.println("REFUSING THE MESSAGE RECEIVED FROM THE SECURE PLATFORM "+inform.getContent());
    }

    protected void handleAgree(ACLMessage agree){
        System.out.println("MESSAGE RECEIVED IN THE AGREE HANDLER "+agree.getContent());
    }

    protected void handleAllResponses(Vector responses) {
        System.out.println("MESSAGE RECEIVED IN THE ALL REPONSE HANDLER ");
    }

    protected void handleNotUnderstood(ACLMessage notUnderstood){
        System.out.println("MESSAGE RECEIVED IN THE NOT UNDERSTOOD HANDLER "+notUnderstood.getContent());
    }

    protected void handleOutOfSequence(ACLMessage outOfSequence) {
        System.out.println("MESSAGE RECEIVED IN THE OUT OF SEQUENCE HANDLER "+outOfSequence.getContent());
    }

    protected void handleFailure(ACLMessage failure){
        System.out.println("MESSAGE RECEIVED IN THE FAILURE HANDLER "+failure.getContent());
    }
}
