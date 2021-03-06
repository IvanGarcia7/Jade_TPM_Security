package jade.core.SecureAgent;


import jade.core.*;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

public class SenderMigrationRequest extends SimpleAchieveREInitiator {

    private ACLMessage myMessage;
    private BaseService myService;
    private RequestSecureATT myPackRequest;
    private JTextArea Printer;

    public SenderMigrationRequest(ACLMessage message, Agent amsMainPlatform,
                                  SecureAgentTPMService secureAgentTPMService, RequestSecureATT PackRequest,
                                  JTextArea printer) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        myService = secureAgentTPMService;
        myPackRequest = PackRequest;
        Printer = printer;
    }


    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO
     * THE PLATFORM NEED TO COMPLETE ALL THE INFORMATION REQUIRED
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){

        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey secKey = generator.generateKey();
            RequestSecureATT newPacket = new RequestSecureATT(myPackRequest.getPlatformLocationOrigin(),
                                                              myPackRequest.getPlatformCALocationDestiny(),
                                                              myPackRequest.getAgente());

            byte[] byteCipherObject = Agencia.cipherOwner(Agencia.serialize(newPacket),secKey);
            byte [] encryptedKey = Agencia.encrypt(myPackRequest.getPublicPassword(),secKey.getEncoded());

            myMessage.setContentObject(new Pair<byte [],byte []>(encryptedKey,byteCipherObject));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Agencia.printLog("PROCEEDING TO SEND THE MIGRATION REQUEST TO THE SECURE PLATFORM", Level.INFO,
                SecureAgentTPMHelper.DEBUG, this.getClass().getName());

        myMessage.addReceiver(myPackRequest.getPlatformCALocation().getAmsAID());
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_MIGRATE_PLATFORM);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);

        Agencia.printLog("MESSAGE CREATE CORRECTLY INTO THE SENDER START REQUEST", Level.INFO,
                         SecureAgentTPMHelper.DEBUG, this.getClass().getName());

        return myMessage;
    }


    //REDEFINED ALL THE HANDLERS TO KNOW WHAT HAPPENS WITH THE MESSAGE THAT THE PLATFORM RECEIVE FROM THE CA

    protected void handleInform(ACLMessage inform){
        System.out.println("RESPONSE FROM THE SECURE PLATFORM: "+inform.getContent());
        Printer.append("\nRESPONSE FROM THE SECURE PLATFORM: \n"+inform.getContent()+" \n\n");
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
