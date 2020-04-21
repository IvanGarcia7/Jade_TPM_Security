package jade.core.SecureInterTPM;

import jade.core.*;
import jade.core.SecureIntraTPM.SecureIntraTPMSlice;
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
    private BaseService myService;



    public ResponserACL(Agent a, MessageTemplate mt, BaseService service) {
        super(a, mt);
        myService = service;

    }

    protected ACLMessage prepareResponse(ACLMessage request) {
        System.out.println("HE ENTRADO EN EL RESPONSER PARA RECIBIR MI MENSAJE");
        // Create the agree reply
        ACLMessage reply = request.createReply();
        try {
            if ((request.getOntology().equals(SecureInterTPMHelper.REQUEST_ATTESTATION))){
                reply.setPerformative(ACLMessage.AGREE);
            }
            else {
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            }

            System.out.println("PROCESSING THE VERTICAL COMMAND ATTESTATION INTO THE DESTINATION CONTAINER");
            requestPack = (RequestAttestation) request.getContentObject();
            RequestAttestation pack = requestPack;
            //CREATING THE NEW VERTICAL COMMAND
            RequestConfirmation packnew = new RequestConfirmation(pack.getNameAgent(),pack.getDestinyLocation(),
                    pack.getOriginLocation(),pack.getAMSName());
            //ADD THE PACK
            reply.setContentObject(Agencia.serialize(packnew));
            System.out.println("HASTA AQUI NO HAY PROBLEMA");
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

        long endTime = System.nanoTime();
        long timeElapsed = endTime - requestPack.getTime();

        //if(timeElapsed <= Agencia.getTimeout()){
            System.out.println("THE REQUEST ATTESTATION IS NOT TIME OUT");
            // Create the agree reply
            ACLMessage reply = request.createReply();
            try {
                // We have previously filled the request with the data	so we only have
                // to put the data in the INFORM message
                reply.setPerformative(ACLMessage.INFORM);
                RequestAttestation ar=(RequestAttestation) request.getContentObject();
                reply.setContentObject(Agencia.serialize(ar));
                System.out.println("SIN EMBARGO AKI VALE: "+ar);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            reply.setSender(requestPack.getAMSName());
            System.out.println(requestPack.getAMSName());
            System.out.println("Se lo voy a enviar a "+requestPack.getOriginLocation());

            return reply;
        //}else{
        //    throw new FailureException("ABORTING");
        //}

    }

    protected void handleInform(ACLMessage inform){
        System.out.println("KOWOBUENO");
        try {
            // Generate the ATTEST_RESULT command
            GenericCommand cmd = new GenericCommand(
                    SecureInterTPMHelper.REQUEST_CONFIRMATION,
                    SecureInterTPMHelper.NAME,
                    null);
            // Send the command to the service
            myService.submit(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
