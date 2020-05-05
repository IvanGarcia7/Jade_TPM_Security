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

    /**
     * FUNCTION TO INITIALIZE THE SERVICE
     * @param a
     * @param mt
     * @param service
     */
    public ResponserACL(Agent a, MessageTemplate mt, BaseService service) {
        super(a, mt);
        myService = service;

    }

    /**
     * THIS FUNCTION TAKE A ACL REQUEST, AND PROCESS IT ATTEND TO THE TYPE OF THE REQUEST
     * @param request the received message
     * @return
     */
    protected ACLMessage prepareResponse(ACLMessage request) {
        System.out.println("PROCESSING THE REQUEST ATTESTATION IN THE DESTINY");
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
            System.out.println("RESPONSE CREATE SUCCESSFULLY");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        System.out.println("PREPARING THE RESULT NOTIFICATION");
        // Create the agree reply
        ACLMessage reply = request.createReply();
        try {
            reply.setPerformative(ACLMessage.INFORM);
            RequestAttestation ar=(RequestAttestation) request.getContentObject();
            reply.setContentObject(Agencia.serialize(ar));
            System.out.println("SENDING THE SERIALIZE PACKET ATTESTATION "+ar);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reply.setSender(requestPack.getAMSName());
        System.out.println(requestPack.getAMSName());
        System.out.println("THE DESTINY OF THE PACKET IS THE FOLLOWING:"+requestPack.getOriginLocation());
        return reply;
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

}
