package jade.core.SecureCloud;



import jade.core.*;
import jade.core.SecureAgent.SecureAgentPlatform;
import jade.core.SecureAgent.SecureAgentTPMHelper;
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


public class SenderCAChallenge extends SimpleAchieveREInitiator {


    private ACLMessage myMessage = null;
    private BaseService myService;
    private PlatformID Origin;
    private PlatformID Destiny;
    private String Challenge;
    private String Ontology;
    private PublicKey destinyKey;
    private PublicKey pubCA;
    private int validation;
    private AID Agent;
    private JTextArea Printer;


    public SenderCAChallenge(ACLMessage message, Agent amsMainPlatform, SecureCloudTPMService secureCloudTPMService,
                             PlatformID origin, PlatformID destiny, String challen, String onto, PublicKey pub,
                             PublicKey publicSec, int val, AID agent, JTextArea printer) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        myService = secureCloudTPMService;
        Origin = origin;
        Destiny = destiny;
        Challenge = challen;
        Ontology = onto;
        destinyKey = pub;
        pubCA = publicSec;
        validation = val;
        Agent = agent;
        Printer = printer;
    }


    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO IT NEEDS TO COMPLETE ALL THE INFORMATION
     * REQUIRED IN BASE OF THE REQUEST ATTESTATION THAT IT HAS RECEIVED PREVIOUSLY.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey secKey = generator.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);

            Date date = new Date();
            long timeMilli = date.getTime();
            PrivateInformationCA secretInfo = new PrivateInformationCA(Destiny,timeMilli,Challenge,Origin,validation,
                                                                       Agent);

            byte[] byteCipherObjectSecret = aesCipher.doFinal(Agencia.serialize(secretInfo));
            byte [] encryptedKeySecret = Agencia.encrypt(pubCA,secKey.getEncoded());
            //Pair<byte [],byte []> SecretPack = new Pair<byte [],byte []>(encryptedKeySecret,byteCipherObjectSecret);

            KeyGenerator generator2 = KeyGenerator.getInstance("AES");
            generator2.init(256);
            SecretKey secKey2 = generator.generateKey();
            Cipher aesCipher2 = Cipher.getInstance("AES");
            aesCipher2.init(Cipher.ENCRYPT_MODE, secKey2);

            byte[] byteCipherObjectPublic = aesCipher2.doFinal(Agencia.serialize(Challenge));
            byte [] encryptedKeyPublic = Agencia.encrypt(destinyKey,secKey2.getEncoded());

            SecureChallengerPacket pSender = new SecureChallengerPacket(encryptedKeySecret,encryptedKeyPublic,
                                                                        byteCipherObjectPublic,byteCipherObjectSecret);
            myMessage.setContentObject(pSender);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Agencia.printLog("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD", Level.INFO,
                        SecureCloudTPMHelper.DEBUG, this.getClass().getName());

        myMessage.addReceiver(Origin.getAmsAID());
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        Date t = new Date(c.getTimeInMillis());

        myMessage.setReplyByDate(t);

        Agencia.printLog("MESSAGE CREATE SUCCESFULLY INTO THE SENDER ACL CHALLENGE", Level.INFO,
                        SecureCloudTPMHelper.DEBUG, this.getClass().getName());

        return myMessage;
    }

    //REDEFINED ALL THE HANDLERS TO KNOW WHAT HAPPENS WITH THE MESSAGE THAT THE PLATFORM RECEIVE FROM THE CA

    protected void handleInform(ACLMessage inform){
        Printer.append("\nRESPONSE FROM THE SECURE PLATFORM: \n"+inform.getContent()+" \n\n");
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
