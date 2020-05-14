package jade.core.SecureAgent;

import jade.core.Agent;
import jade.core.BaseService;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;

public class SenderStartRequest extends SimpleAchieveREInitiator {


    private ACLMessage myMessage;
    private RequestSecureATT RequestStart;
    private RequestSecureATT CAInformation;
    private BaseService myService;
    private JTextArea Printer;

    public SenderStartRequest(ACLMessage message, RequestSecureATT requestSecureStart,
                              RequestSecureATT SecureCAInformation, Agent amsMainPlatform,
                              SecureAgentTPMService secureAgentTPMService, JTextArea printer) {
        super(amsMainPlatform,message);
        myMessage = message;
        RequestStart = requestSecureStart;
        CAInformation = SecureCAInformation;
        myAgent = amsMainPlatform;
        myService = secureAgentTPMService;
        Printer = printer;
    }

    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO
     * THE PLATFORM NEED TO COMPLETE ALL THE INFORMATION REQUIRED IN BASE OF THE
     * REQUEST ATTESTATION THAT IT HAS RECEIVED PREVIOUSLY.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){

        try {
            PublicKey PublicKeyCA = CAInformation.getPublicPassword();
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);

            //GENERATED OTP KEY
            SecretKey secKey = generator.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);

            byte[] byteCipherObject = aesCipher.doFinal(Agencia.serialize(RequestStart));
            byte [] encryptedKey = Agencia.encrypt(PublicKeyCA,secKey.getEncoded());
            myMessage.setContentObject(new Pair<byte [],byte []>(encryptedKey,byteCipherObject));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //SEND THE INFORMATION TO THE SECURE PLATFORM
        Agencia.printLog("PROCEEDING TO SEND THE REQUEST TO THE SECURE PLATFORM", Level.INFO,
                         SecureAgentTPMHelper.DEBUG, this.getClass().getName());

        myMessage.addReceiver(CAInformation.getPlatformCALocation().getAmsAID());
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_INSERT_PLATFORM);

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
        Printer.append("RESPONSE FROM THE SECURE PLATFORM: "+inform.getContent()+" \n");
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
