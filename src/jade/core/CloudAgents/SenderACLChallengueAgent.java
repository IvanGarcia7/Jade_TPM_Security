package jade.core.CloudAgents;

        import jade.core.*;
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

public class SenderACLChallengueAgent extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private BaseService myService;
    private AttestationSerialized myAttestation = null;
    private PublicKey pubCA;
    private byte [] secretInformation;
    private Location CAlocation;

    public SenderACLChallengueAgent(ACLMessage message, Agent amsMainPlatform, AttestationSerialized packet_signed, SecureAgentTPMService secureAgentTPMService, PublicKey pubSEC, byte [] secretInfo, Location CAloc) {
        super(amsMainPlatform,message);
        myMessage = message;
        myAgent = amsMainPlatform;
        myService = secureAgentTPMService;
        myAttestation = packet_signed;
        pubCA = pubSEC;
        secretInformation = secretInfo;
        CAlocation = CAloc;
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
            generator.init(256); //INTERESTING TO TEST
            SecretKey secKey = generator.generateKey();
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
            //PACKEST REQUEST TO SECUREPLATFORM
            Pair<AttestationSerialized ,byte []> packetComplete = new Pair<AttestationSerialized, byte []>(myAttestation,secretInformation);
            byte[] byteCipherObject = aesCipher.doFinal(Agencia.serialize(packetComplete));
            byte [] encryptedKey = Agencia.encrypt(pubCA,secKey.getEncoded());
            myMessage.setContentObject(new Pair<byte [],byte []>(encryptedKey,byteCipherObject));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+CAlocation.getName(),AID.ISGUID);
        receiver.addAddresses(CAlocation.getAddress());
        System.out.println(receiver+" "+CAlocation.getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM);
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
                        SecureAgentTPMHelper.REQUEST_ACCEPT_PLATFORM,
                        SecureAgentTPMHelper.NAME,
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
                    SecureAgentTPMHelper.REQUEST_ACCEPT_PLATFORM,
                    SecureAgentTPMHelper.NAME,
                    null);
            myService.submit(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }










}
