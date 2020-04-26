package jade.core.D4rkPr0j3cT;

import jade.core.Agent;
import jade.core.BaseService;
import jade.core.CloudAgents.KeyPairCloudPlatform;
import jade.core.GenericCommand;
import jade.core.SecureTPM.Agencia;
import jade.core.ServiceException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;

public class ResponserCloudACL extends SimpleAchieveREResponder {

    private Logger logger = Logger.getMyLogger(getClass().getName());
    private BaseService myService;


    public ResponserCloudACL(Agent ams, MessageTemplate mt, SecureCloudTPMService secureCloudTPMService) {
        super(ams, mt);
        myService = secureCloudTPMService;
    }

    /**
     * THIS FUNCTION TAKE A ACL REQUEST, AND PROCESS IT ATTEND TO THE TYPE OF THE REQUEST
     * @param request the received message
     * @return
     */
    protected ACLMessage prepareResponse(ACLMessage request) {
        System.out.println("SAVING THE PLATFORM WITHIN THE DIRECTORY");
        GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM,
                SecureCloudTPMHelper.NAME, null);
        ACLMessage reply = null;
        try{
            /**
             * REMEMBER, THE CONTENT OF THE ACL IS CIPHER BY THE PUBLICK KEY OF MY PLATFORM,
             * SO I NEED TO DECRYPT IT FIRST
             */
            KeyPairCloudPlatform pack = Agencia.decypher(request.getContentObject());
            command.addParam(pack);
            myService.submit(command);
            System.out.println("THE PROCESS HAS BEEN COMPLETED SUCCESSFUL");
            String Response = "200";
            reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(Response);
        }catch(Exception se){
            System.out.println("THE PLATFORM COULD NOT REGISTER, TRY AGAIN LATER");
            se.printStackTrace();
            String Response = "500";
            reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(Response);
        }
        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        System.out.println("PREPARING THE RESULT NOTIFICATION IN THE RESPONSER PLATFORM");
        ACLMessage reply = request.createReply();
        return reply;
    }

    /**
     * THIS FUNCTION PROCEED WITH THE SECURE MOVE OR CLONATION, IF THE MAXIMUM WAITING TIME IS NOT EXPIRED.
     * @param inform
     */
    protected void handleInform(ACLMessage inform){
        System.out.println("CATCH THE ACL MESSAGE IN THE HANDLE INFORM IN THE RESPONSER PLATFORM");
    }
}
