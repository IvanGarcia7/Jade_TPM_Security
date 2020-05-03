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

public class SenderACLMigration extends SimpleAchieveREInitiator {

    private ACLMessage myMessage;
    private BaseService myService;
    private KeyPairCloudPlatform myPack;


    public SenderACLMigration(ACLMessage message, Agent amsMainPlatform, SecureAgentTPMService secureAgentTPMService,
                              KeyPairCloudPlatform newPack) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        myService = secureAgentTPMService;
        myPack = newPack;

    }


    public ACLMessage prepareRequest(ACLMessage acl){
        try {
            KeyPairCloudPlatform newPacket = new KeyPairCloudPlatform(myPack.getMyLocation(),myPack.getLocationDestiny());
            byte [] encryptedLocation = Agencia.encrypt(myPack.getPublicPassword(),Agencia.serialize(newPacket));
            myMessage.setContentObject(encryptedLocation);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+myPack.getLocationPlatform().getName(),AID.ISGUID);
        receiver.addAddresses(myPack.getLocationPlatform().getAddress());
        System.out.println(receiver+" "+myPack.getLocationPlatform().getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_MIGRATE_PLATFORM);
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
