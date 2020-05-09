package jade.core.CloudAgents;


import jade.core.Agent;
import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.core.SecureTPM.Agencia;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import java.util.Vector;
import java.util.logging.Level;


public class ResponseAgentACL extends SimpleAchieveREResponder {

    private BaseService myService;

    public ResponseAgentACL(Agent ams, MessageTemplate mt, SecureAgentTPMService secureAgentTPMService) {
        super(ams, mt);
        myService = secureAgentTPMService;
    }


    /**
     * THIS FUNCTION TAKE A ACL REQUEST, AND PROCESS IT ATTEND TO THE TYPE OF THE REQUEST
     * @param request the received message
     * @return
     */
    protected ACLMessage prepareResponse(ACLMessage request) {

        Agencia.printLog("PROCESSING THE REQUEST ATTESTATION IN THE DESTINY IN THE RESPONSER AGENT",
                Level.INFO, SecureAgentTPMHelper.DEBUG, this.getClass().getName());

        ACLMessage reply = null;

        if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
            try{
                System.out.println("PLATFORM RECEIVE A ZONE 1 REQUEST TO ATTESTATE THE ORIGIN");
                GenericCommand command = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                                                            SecureAgentTPMHelper.NAME, null);
                command.addParam(request.getContentObject());
                myService.submit(command);
            }catch(Exception e){
                System.out.println("ERROR IN THE PREPARE RESPONSE OF THE ZONE 1");
                e.printStackTrace();
            }
        }else if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM)){
            try{
                System.out.println("PLATFORM RECEIVE A CONFIRMATION SECURE MOVE");
                GenericCommand command = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM,
                        SecureAgentTPMHelper.NAME, null);
                command.addParam(request.getContentObject());
                myService.submit(command);
            }catch(Exception e){
                System.out.println("ERROR IN THE CONFIRMATION RESPONSE OF THE ZONE 2");
                e.printStackTrace();
            }
        }else if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_ERROR)){
            System.out.println("PLATFORM RECEIVE A ERROR REQUEST WHILE ATTESTATE THE ORIGIN");
            System.out.println(request.getContent());
        }else{
            System.out.println("UNKNOWN ERROR RECEIVES FROM THE SECURE PLATFORM");
            //reply = request.createReply();
        }

        //SEND AN ACK TO THE SECURE PLATFORM
        String Response = "200";
        reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(Response);

        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        //System.out.println("PREPARING THE RESULT NOTIFICATION IN THE RESPONSE AGENT");
        return null;
    }

    //REDEFINED ALL THE HANDLERS TO KNOW WHAT HAPPENS WITH THE MESSAGE THAT THE PLATFORM RECEIVE FROM THE CA

    protected void handleInform(ACLMessage inform){
        System.out.println("RESPONSE FROM THE SECURE PLATFORM: "+inform.getContent());
    }

    protected void handleAgree(ACLMessage agree){
        System.out.println("MESSAGE RECEIVED IN THE AGREE HANDLER "+agree.getContent());
    }

    protected void handleRefuse(ACLMessage inform){
        System.out.println("REFUSING THE MESSAGE RECEIVED FROM THE SECURE PLATFORM "+inform.getContent());
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
