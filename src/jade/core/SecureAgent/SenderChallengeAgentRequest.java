package jade.core.SecureAgent;


import jade.core.*;
import jade.core.SecureCloud.SecureChallengerPacket;
import jade.core.SecureTPM.Agencia;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;


public class SenderChallengeAgentRequest extends SimpleAchieveREInitiator {

    private ACLMessage myMessage;
    private BaseService myService;
    private AttestationSerialized myAttestation;
    private PublicKey pubCA;
    private PlatformID CAlocation;
    private SecureChallengerPacket packetResponser;


    public SenderChallengeAgentRequest(ACLMessage message, Agent amsMainPlatform, AttestationSerialized PCR_Signed,
                                        SecureAgentTPMService secureAgentTPMService, PublicKey pubSEC, PlatformID CAloc,
                                        SecureChallengerPacket pSender) {
        super(amsMainPlatform,message);
        myMessage = message;
        myAgent = amsMainPlatform;
        myService = secureAgentTPMService;
        myAttestation = PCR_Signed;
        pubCA = pubSEC;
        CAlocation = CAloc;
        packetResponser = pSender;
    }

    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO
     * I NEED TO COMPLETE ALL THE INFORMATION REQUIRED IN BASE OD THE
     * REQUEST ATTESTATION THAT I HAVE RECEIVED PREVIOUSLY.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){
        System.out.println("ENTRO");
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey secKey = generator.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);

            byte[] byteCipherObject = aesCipher.doFinal(Agencia.serialize(myAttestation));
            byte [] encryptedKey = Agencia.encrypt(pubCA,secKey.getEncoded());

            packetResponser.setOTPPub(encryptedKey);
            packetResponser.setPartPublic(byteCipherObject);

            myMessage.setContentObject(packetResponser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Agencia.printLog("PROCEEDING TO SEND THE ATTESTATION RESPONSE IN THE PREPARE REQUEST METHOD",
                Level.INFO, SecureAgentTPMHelper.DEBUG, this.getClass().getName());

        myMessage.addReceiver(CAlocation.getAmsAID());
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);

        Agencia.printLog("MESSAGE CREATE CORRECTLY INTO THE SENDER ZONE 1 ATT REQUEST", Level.INFO,
                SecureAgentTPMHelper.DEBUG, this.getClass().getName());
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
