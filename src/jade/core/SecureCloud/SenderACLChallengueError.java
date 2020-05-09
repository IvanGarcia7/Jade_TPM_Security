package jade.core.SecureCloud;

import jade.core.*;
import jade.core.SecureTPM.Agencia;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;


import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class SenderACLChallengueError extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private BaseService myService;
    private String Ontology;
    private PublicKey destinyKey;
    private String messagesender;
    private Location origin;


    public SenderACLChallengueError(ACLMessage message, Agent amsMainPlatform, String requestError, PublicKey destinypub, String ms, Location or) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        Ontology = requestError;
        destinyKey = destinypub;
        messagesender = ms;
        origin = or;
    }

    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO
     * I NEED TO COMPLETE ALL THE INFORMATION REQUIRED IN BASE OD THE
     * REQUEST ATTESTATION THAT I HAVE RECEIVED PREVIOUSLY.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){
        try {
            byte [] encryptedKey = Agencia.encrypt(destinyKey,messagesender.getBytes());
            myMessage.setContentObject(encryptedKey);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+origin.getName(),AID.ISGUID);
        receiver.addAddresses(origin.getAddress());
        System.out.println(receiver+" "+origin.getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(Ontology);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        //SETTING THE TIMEOUT IN THE ACL MESSAGE
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);
        System.out.println("MESSAGE CREATE SUCCESFULLY INTO THE SENDERACLCHALLENGUEERROR");
        return myMessage;
    }

    /**
     * THIS FUNCTION PROCEED WITH THE SECURE MOVE OR CLONATION, IF THE MAXIMUM WAITING TIME IS NOT EXPIRED.
     * @param inform
     */
    protected void handleInform(ACLMessage inform){
        System.out.println("CATCH THE ACL MESSAGE IN THE HANDLE INFORM");
        System.out.println(inform.getContent());
        /*
        try {
            long endTime = System.nanoTime();
            if((inform.getPostTimeStamp()-endTime)/1000000 <= Agencia.getTimeout()){
                GenericCommand cmd = new GenericCommand(
                        SecureCloudTPMHelper.REQUEST_ACCEPT_PLATFORM,
                        SecureCloudTPMHelper.NAME,
                        null);
                myService.submit(cmd);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }


         */
    }

    protected void handleRefuse(ACLMessage inform){
        System.out.println("REFUSING THE MESSAGE IN THE HANDLE REFUSE");
        try {
            GenericCommand cmd = new GenericCommand(
                    SecureCloudTPMHelper.REQUEST_ACCEPT_PLATFORM,
                    SecureCloudTPMHelper.NAME,
                    null);
            myService.submit(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void handleAgree(ACLMessage agree){
        System.out.println("hrvewrevwe");
    }


    protected void handleAllResponses(Vector responses) {
        System.out.println("hrr");
    }

    protected void handleNotUnderstood(ACLMessage notUnderstood){
        System.out.println("hrr");
    }

    protected void handleOutOfSequence(ACLMessage outOfSequence) {
        System.out.println("hr");
    }

    protected void handleFailure(ACLMessage failure){
        System.out.println("h");
    }










}
