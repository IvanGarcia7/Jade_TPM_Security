package jade.core.SecureInterTPM;

import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.core.SecureIntraTPM.SecureIntraTPMHelper;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.RequestConfirmation;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

import java.util.Calendar;
import java.util.Date;

public class SenderACL2 extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private RequestConfirmation packet = null;
    private Agent myAgent = null;
    private BaseService myService;

    public SenderACL2(ACLMessage message, RequestConfirmation packnew, Agent amsMain, SecureInterTPMService secureInterTPMService) {
        super(amsMain,message);
        myMessage=message;
        packnew = packnew;
        myAgent = amsMain;
        myService = secureInterTPMService;
    }

    public ACLMessage prepareRequest(ACLMessage acl){
        try {
            acl.setContentObject(Agencia.serialize(packet));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Add the receiver data to the message
        AID receiver = new AID("ams@"+packet.getDestinyLocation().getName(),AID.ISGUID);
        receiver.addAddresses(packet.getDestinyLocation().getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(SecureIntraTPMHelper.REQUEST_ATTESTATION);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);
        return myMessage;
    }

    protected void handleInform(ACLMessage inform){
        System.out.println("KOWOSENDER2");
        try {
            // Generate the ATTEST_RESULT command
            GenericCommand cmd = new GenericCommand(
                    SecureInterTPMHelper.REQUEST_CONFIRMATION,
                    SecureInterTPMHelper.NAME,
                    null);
            // Add the request data to the command

            // Send the command to the service
            myService.submit(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void handleRefuse(ACLMessage inform){
        System.out.println("KOWOREFUTADO2");
        try {
            // Generate the ATTEST_RESULT command
            GenericCommand cmd = new GenericCommand(
                    SecureInterTPMHelper.REQUEST_CONFIRMATION,
                    SecureInterTPMHelper.NAME,
                    null);
            // Add the request data to the command

            // Send the command to the service
            myService.submit(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
