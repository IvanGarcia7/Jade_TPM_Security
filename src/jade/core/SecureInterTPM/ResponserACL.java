package jade.core.SecureInterTPM;

import jade.core.*;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.RequestAttestation;
import jade.core.SecureTPM.RequestConfirmation;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;

public class ResponserACL extends SimpleAchieveREResponder {

    private Logger logger = Logger.getMyLogger(getClass().getName());
    private RequestAttestation requestPack;



    public ResponserACL(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    protected ACLMessage prepareResponse(ACLMessage request) {
        System.out.println("HE ENTRADO EN EL RESPONSER PARA RECIBIR MI MENSAJE");
        // Create the agree reply
        ACLMessage reply = request.createReply();
        RequestAttestation ar=null;
        try {
            if ((request.getOntology().equals(SecureInterTPMHelper.REQUEST_ATTESTATION))){
                reply.setPerformative(ACLMessage.AGREE);
            }
            else {
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            }

            System.out.println("PROCESSING THE VERTICAL COMMAND ATTESTATION INTO THE DESTINATION CONTAINER");
            RequestAttestation pack = requestPack;
            //CREATING THE NEW VERTICAL COMMAND
            RequestConfirmation packnew = new RequestConfirmation(pack.getNameAgent(),pack.getDestinyLocation(),
                    pack.getOriginLocation(),pack.getAMSName());
            //ADD THE PACK
            reply.setContentObject(Agencia.serialize(packnew));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        System.out.println("HE ENTRADO EN EL RESPONSER PARA RECIBIR MI notifi");
        if (logger.isLoggable(Logger.FINE)) {
            logger.log(Logger.FINE,	"AMSResponder: Request message received.");
        }

        // Create the agree reply
        ACLMessage reply = request.createReply();
        try {
            // We have previously filled the request with the data	so we only have
            // to put the data in the INFORM message
            reply.setPerformative(ACLMessage.INFORM);
            RequestAttestation ar=(RequestAttestation) response.getContentObject();
            reply.setContentObject(Agencia.serialize(ar));
            System.out.println("SIN EMBARGO AKI VALE: "+ar);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reply.setSender(new AID("test",AID.ISGUID));
        return reply;
    }

}
