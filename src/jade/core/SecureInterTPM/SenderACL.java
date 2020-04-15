package jade.core.SecureInterTPM;

import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
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

    public SenderACL(ACLMessage message, RequestAttestation packnew, Agent amsMain, SecureInterTPMService secureInterTPMService) {
        super(amsMain,message);
        myMessage=message;
        packet = packnew;
        myAgent = amsMain;
        myService = secureInterTPMService;
    }

    public ACLMessage prepareRequest(ACLMessage acl){
        try {
            acl.setContentObject(packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("HE ENTRADO EN EL SENDER PARA ENVIAR MI MENSAJE");
        // Add the receiver data to the message
        AID receiver = new AID("ams@"+packet.getDestinyLocation().getName(),AID.ISGUID);
        receiver.addAddresses(packet.getDestinyLocation().getAddress());
        System.out.println(receiver+" "+packet.getDestinyLocation().getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(SecureIntraTPMHelper.REQUEST_ATTESTATION);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);
        System.out.println("HE DEVUELTO MI MENSAJE EN EL SENDER");
        return myMessage;
    }
}
