package jade.core.SecureInterTPM;

import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.core.SecureIntraTPM.SecureIntraTPMHelper;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.RequestAttestation;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

import java.util.Calendar;
import java.util.Date;

public class SenderACL extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private RequestAttestation packet = null;
    private Agent myAgent = null;
    private BaseService myService;

    /**
     * THIS FUNCTION INITIALIZE THE VARIABLES THAT I HAVE DEFINED FOR THIS CLASS
     * @param message
     * @param packnew
     * @param amsMain
     * @param secureInterTPMService
     */
    public SenderACL(ACLMessage message, RequestAttestation packnew, Agent amsMain, SecureInterTPMService secureInterTPMService) {
        super(amsMain,message);
        myMessage=message;
        packet = packnew;
        myAgent = amsMain;
        myService = secureInterTPMService;
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
            myMessage.setContentObject(packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+packet.getDestinyLocation().getName(),AID.ISGUID);
        receiver.addAddresses(packet.getDestinyLocation().getAddress());
        System.out.println(receiver+" "+packet.getDestinyLocation().getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(SecureIntraTPMHelper.REQUEST_ATTESTATION);
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
        try {
            long endTime = System.nanoTime();
            if((inform.getPostTimeStamp()-endTime)/1000000 <= Agencia.getTimeout()){
                GenericCommand cmd = new GenericCommand(
                        SecureInterTPMHelper.REQUEST_CONFIRMATION,
                        SecureInterTPMHelper.NAME,
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
                    SecureInterTPMHelper.REQUEST_CONFIRMATION,
                    SecureInterTPMHelper.NAME,
                    null);
            myService.submit(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


}
