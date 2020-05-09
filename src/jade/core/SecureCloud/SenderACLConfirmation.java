package jade.core.SecureCloud;

import jade.core.*;
import jade.core.SecureTPM.Agencia;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

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
    private PlatformID mypt;


    public SenderACLConfirmation(ACLMessage message, Agent amsMainPlatform, Location or, Location dr, String requestMigrateZone2Platform, PublicKey destinypub,PublicKey destinypubremote,PrivateKey CA,PlatformID ppt) {
        super(amsMainPlatform,message);
        myMessage=message;
        myAgent = amsMainPlatform;
        Ontology = requestMigrateZone2Platform;
        origin=or;
        destiny = dr;
        originKey = destinypub;
        destinyKey = destinypubremote;
        CAKey = CA;
        mypt = ppt;
    }

    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO
     * I NEED TO COMPLETE ALL THE INFORMATION REQUIRED IN BASE OD THE
     * REQUEST ATTESTATION THAT I HAVE RECEIVED PREVIOUSLY.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl) {
        System.out.println("HOLA");
        /*
        //destiny keypubdestiny
        Pair<Pair<Location,Location>,PublicKey> information = new Pair<Pair<Location,Location>,PublicKey>(new Pair<Location,Location>(origin,destiny),destinyKey);
        byte [] informationSerial = null;
        byte [] signed = null;

        try{
            informationSerial = Agencia.serialize(information);
            signed = Agencia.Signed(CAKey,Agencia.serialize(informationSerial));
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            byte [] encryptedKey = Agencia.encrypt(originKey,signed);
            myMessage.setContentObject(encryptedKey);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */

        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+mypt.getName(),AID.ISGUID);
        receiver.addAddresses(mypt.getAddress());
        System.out.println(receiver+"BETTER THA ALONE "+origin.getAddress());
        myMessage.addReceiver(mypt.getAmsAID());
        myMessage.setOntology(Ontology);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        //SETTING THE TIMEOUT IN THE ACL MESSAGE
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);
        System.out.println("MESSAGE CREATE SUCCESFULLY INTO THE SENDER ACL CONFIRMATION");
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