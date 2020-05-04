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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

public class SenderACLChallengue extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private BaseService myService;
    private Location Origin;
    private Location Destiny;
    private String Challengue;
    private String Ontology;
    private PublicKey destinyKey;
    private PublicKey pubCA;
    private int validation;


    public SenderACLChallengue(ACLMessage message, Agent amsMainPlatform, SecureCloudTPMService secureCloudTPMService,
                               Location origin,Location destiny, String challen, String onto, PublicKey pub, PublicKey publicSec, int val) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        myService = secureCloudTPMService;
        Origin = origin;
        Destiny = destiny;
        Challengue = challen;
        Ontology = onto;
        destinyKey = pub;
        pubCA = publicSec;
        validation = val;
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
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey secKey = generator.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            Date date = new Date();
            long timeMilli = date.getTime();
            SecretInformation secretInfo = new SecretInformation(Destiny,timeMilli,Challengue,Origin,validation);
            byte [] secretInforPair = Agencia.serialize(secretInfo);
            //Cipher with the pubKeySec
            byte [] encryptedKey = Agencia.encrypt(pubCA,secretInforPair);
            Pair<String,byte []> contentSender = new Pair<String,byte []>(Challengue,encryptedKey);
            byte [] encryptedPacket = Agencia.encrypt(destinyKey,Agencia.serialize(contentSender));
            myMessage.setContentObject(encryptedPacket);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+Destiny.getName(),AID.ISGUID);
        receiver.addAddresses(Destiny.getAddress());
        System.out.println(receiver+" "+Destiny.getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(Ontology);
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
