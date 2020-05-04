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

public class SenderACLConfirmation extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private BaseService myService;
    private String Ontology;
    private String messagesender;
    private Location origin;
    private Location destiny;
    private PublicKey originKey;
    private PublicKey destinyKey;
    private PrivateKey CAKey;


    public SenderACLConfirmation(ACLMessage message, Agent amsMainPlatform, Location or, Location dr, String requestMigrateZone2Platform, PublicKey destinypub,PublicKey destinypubremote,PrivateKey CA) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        Ontology = requestMigrateZone2Platform;
        origin=or;
        destiny = dr;
        originKey = destinypub;
        destinyKey = destinypubremote;
        CAKey = CA;
    }

    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO
     * I NEED TO COMPLETE ALL THE INFORMATION REQUIRED IN BASE OD THE
     * REQUEST ATTESTATION THAT I HAVE RECEIVED PREVIOUSLY.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){

        //destiny keypubdestiny
        Pair<Location,PublicKey> information = new Pair<Location,PublicKey>(destiny,destinyKey);
        //byte [] signed = Agencia.encrypt(CAKey,information);



        //TO -DO






        try {
            byte [] encryptedKey = Agencia.encrypt(originKey,);
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
