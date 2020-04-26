package jade.core.CloudAgents;

import jade.core.Agent;
import jade.core.BaseService;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;

public class ResponserAgentACL extends SimpleAchieveREResponder {

    private Logger logger = Logger.getMyLogger(getClass().getName());
    private BaseService myService;


    public ResponserAgentACL(Agent ams, MessageTemplate mt, SecureAgentTPMService secureCloudTPMService) {
        super(ams, mt);
        myService = secureCloudTPMService;
    }


    /**
     * THIS FUNCTION TAKE A ACL REQUEST, AND PROCESS IT ATTEND TO THE TYPE OF THE REQUEST
     * @param request the received message
     * @return
     */
    protected ACLMessage prepareResponse(ACLMessage request) {
        System.out.println("PROCESSING THE REQUEST ATTESTATION IN THE DESTINY IN THE RESPONSER AGENT");
        ACLMessage reply = request.createReply();
        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        System.out.println("PREPARING THE RESULT NOTIFICATION IN THE RESPONSER AGENT");
        ACLMessage reply = request.createReply();
        return reply;
    }

    /**
     * THIS FUNCTION PROCEED WITH THE SECURE MOVE OR CLONATION, IF THE MAXIMUM WAITING TIME IS NOT EXPIRED.
     * @param inform
     */
    protected void handleInform(ACLMessage inform){
        System.out.println("CATCH THE ACL MESSAGE IN THE HANDLE INFORM IN THE RESPONSER AGENT");
    }






}
