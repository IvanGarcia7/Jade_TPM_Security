package jade.core.SecureCloud;

import jade.core.*;
import jade.core.CloudAgents.SecureAgentTPMHelper;
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
import java.util.Vector;

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
    private PlatformID mypt;


    public SenderACLChallengue(ACLMessage message, Agent amsMainPlatform, SecureCloudTPMService secureCloudTPMService,
                               Location origin,Location destiny, String challen, String onto, PublicKey pub, PublicKey publicSec, int val,PlatformID pt) {
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
        mypt = pt;
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

            System.out.println("LA INFORMACION QUE HE GUARDADO ES LA SIGUIENTE");
            System.out.println("***********************************************");
            System.out.println("***********************************************");
            System.out.println(Destiny);
            System.out.println("***********************************************");
            System.out.println(Origin);
            System.out.println("***********************************************");
            System.out.println("***********************************************");
            System.out.println("***********************************************");



            //Cipher with the pubKeySec
            byte[] byteCipherObjectSecret = aesCipher.doFinal(Agencia.serialize(secretInfo));

            byte [] encryptedKeySecret = Agencia.encrypt(pubCA,secKey.getEncoded());
            Pair<byte [],byte []> SecretPack = new Pair<byte [],byte []>(encryptedKeySecret,byteCipherObjectSecret);

            KeyGenerator generator2 = KeyGenerator.getInstance("AES");
            generator2.init(256);
            SecretKey secKey2 = generator.generateKey();
            Cipher aesCipher2 = Cipher.getInstance("AES");
            aesCipher2.init(Cipher.ENCRYPT_MODE, secKey2);
            Pair<String, Pair<byte [],byte []>> publico = new Pair<String,Pair<byte [],byte []>>(Challengue,SecretPack);
            byte[] byteCipherObjectPublic = aesCipher2.doFinal(Agencia.serialize(Challengue));
            byte [] encryptedKeyPublic = Agencia.encrypt(destinyKey,secKey2.getEncoded());

            Pair<byte[],byte[]> packetFinal = new Pair<byte[],byte[]>(encryptedKeyPublic,byteCipherObjectPublic);
            //myMessage.setContentObject(packetFinal);



            System.out.println("MI CLAVE EN EL SEC PLAT PARA LA PARTE PUBLICA ES LA SIGUIENTE:");
            System.out.println(destinyKey);



            SecureChallenguerPacket pSender = new SecureChallenguerPacket(encryptedKeySecret,encryptedKeyPublic,byteCipherObjectPublic,byteCipherObjectSecret);
            myMessage.setContentObject(Agencia.serialize(pSender));






        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");


        AID receiver = new AID("ams@"+mypt.getName(),AID.ISGUID);
        receiver.addAddresses(mypt.getAddress());
        System.out.println(receiver+" "+mypt.getAddress());

        System.out.println(mypt.getAmsAID());

        myMessage.addReceiver(mypt.getAmsAID());
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        //SETTING THE TIMEOUT IN THE ACL MESSAGE
        Date t = new Date(c.getTimeInMillis());

        myMessage.setReplyByDate(t);
        System.out.println("MESSAGE CREATE SUCCESFULLY INTO THE SENDER ACL CHALLENGUE");


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
