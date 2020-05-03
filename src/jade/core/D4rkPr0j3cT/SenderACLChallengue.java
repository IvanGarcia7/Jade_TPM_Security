package jade.core.D4rkPr0j3cT;

import jade.core.*;
import jade.core.CloudAgents.SecureAgentTPMService;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

public class SenderACLChallengue extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private BaseService myService;
    private Location Origin;
    private Location Destiny;
    private String challengue;
    private String myId;
    private long timestamp;


    public SenderACLChallengue(ACLMessage message, Agent amsMainPlatform, SecureCloudTPMService secureCloudTPMService,
                               Location origin, Location destiny, String challen,String id, long tim) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        myService = secureCloudTPMService;
        Origin = origin;
        Destiny = destiny;
        challengue = challen;
        myId = id;
        timestamp = tim;
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
            //Packet Agent need to be cipher with the public Key of the platform
            PublicKey kpub = packetPlatform.getPublicPassword();
            //cipher the message
            //GENERATE OTP KEY
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256); //INTERESTING TO TEST
            SecretKey secKey = generator.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            //PACKEST REQUEST TO SECUREPLATFORM
            RequestSecure newRequestSecure = new RequestSecure(packetAgent,myAttestation);
            byte[] byteCipherObject = aesCipher.doFinal(Agencia.serialize(newRequestSecure));
            byte [] encryptedKey = Agencia.encrypt(kpub,secKey.getEncoded());
            myMessage.setContentObject(new Pair<byte [],byte []>(encryptedKey,byteCipherObject));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+packetPlatform.getLocationPlatform().getName(),AID.ISGUID);
        receiver.addAddresses(packetPlatform.getLocationPlatform().getAddress());
        System.out.println(receiver+" "+packetPlatform.getLocationPlatform().getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_INSERT_PLATFORM);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        //SETTING THE TIMEOUT IN THE ACL MESSAGE
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);
        System.out.println("MESSAGE CREATE SUCCESFULLY");
        return myMessage;
    }

    /**
     * THIS FUNCTION PROCEED WITH THE SECURE MOVE OR CLONATION, IF THE MAXIMUM WAITING TIME IS NOT EXPIRED.
     * @param inform
     */
    protected void handleInform(ACLMessage inform){
        System.out.println("CATCH THE ACL MESSAGE IN THE HANDLE INFORM");
        System.out.println(inform.getContent());
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










}
