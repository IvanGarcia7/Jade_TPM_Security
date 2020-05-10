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
import java.util.logging.Level;

public class SenderCAConfirmation extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private BaseService myService;
    private String Ontology;
    private PlatformID origin;
    private PlatformID destiny;
    private PublicKey originKey;
    private PublicKey destinyKey;
    private PrivateKey CAKey;


    public SenderCAConfirmation(ACLMessage message, Agent amsMainPlatform, PlatformID or, PlatformID dr,
                                String requestMigrateZone2Platform, PublicKey destinypub, PublicKey destinypubremote,
                                PrivateKey CA) {

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
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO IT NEEDS TO COMPLETE ALL THE INFORMATION
     * REQUIRED IN BASE OD THE REQUEST ACCEPT.
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

        Agencia.printLog("PROCEEDING TO SEND THE CONFIRMATION MESSAGE IN THE PREPARE REQUEST METHOD",
                Level.INFO, SecureCloudTPMHelper.DEBUG, this.getClass().getName());

        myMessage.addReceiver(destiny.getAmsAID());
        myMessage.setOntology(Ontology);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);

        Agencia.printLog("CONFIRMATION MESSAGE CREATE CORRECTLY INTO THE SENDER CONFIRMATION", Level.INFO,
                        SecureCloudTPMHelper.DEBUG, this.getClass().getName());
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
