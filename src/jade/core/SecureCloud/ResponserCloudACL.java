package jade.core.SecureCloud;


import jade.core.Agent;
import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.core.SecureAgent.SecureAgentTPMHelper;
import jade.core.SecureTPM.Agencia;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import java.util.Vector;
import java.util.logging.Level;


public class ResponserCloudACL extends SimpleAchieveREResponder {

    private BaseService myService;

    public ResponserCloudACL(Agent ams, MessageTemplate mt, SecureCloudTPMService secureCloudTPMService) {
        super(ams, mt);
        myService = secureCloudTPMService;
    }

    /**
     * THIS FUNCTION TAKES AN ACL REQUEST, AND PROCESS IT ATTEND TO THE TYPE OF THE REQUEST
     * @param request the received message
     * @return
     */
    protected ACLMessage prepareResponse(ACLMessage request) {

        Agencia.printLog("PROCESSING THE REQUEST IN THE SECURE DESTINY RESPONSER AGENT", Level.INFO,
                SecureAgentTPMHelper.DEBUG, this.getClass().getName());
        ACLMessage reply = null;

       if(request.getOntology().equals(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM)){
           try{
               System.out.println("SAVING THE PLATFORM IN THE SECURE DIRECTORY");
               GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM,
                                                           SecureCloudTPMHelper.NAME, null);
               command.addParam(request.getContentObject());
               myService.submit(command);
               String Response = "RESPONSE: 200";
               reply = request.createReply();
               reply.setPerformative(ACLMessage.INFORM);
               reply.setContent(Response);
           }catch(Exception se){
               System.out.println("THE PLATFORM COULD NOT REGISTER THE REQUEST, TRY AGAIN LATER");
               String Response = "RESPONSE INSERT REQUEST: 500";
               reply = request.createReply();
               reply.setPerformative(ACLMessage.INFORM);
               reply.setContent(Response);
           }
       }else if(request.getOntology().equals("MIGRATE_REQUEST")){
           try{
               System.out.println("THE SECURE PLATFORM RECEIVES A MIGRATE REQUEST");
               GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM,
                       SecureCloudTPMHelper.NAME, null);

               command.addParam(request.getContentObject());
               myService.submit(command);
               String Response = "RESPONSE MIGRATE REQUEST: 200";
               reply = request.createReply();
               reply.setPerformative(ACLMessage.INFORM);
               reply.setContent(Response);
           }catch(Exception sme){
               System.out.println("THE PLATFORM COULD NOT REGISTER, TRY AGAIN LATER");
               String Response = "RESPONSE MIGRATE REQUEST: 500";
               reply = request.createReply();
               reply.setPerformative(ACLMessage.INFORM);
               reply.setContent(Response);
           }
       }else if(request.getOntology().equals("MIGRATE_ZONE1_REQUEST")){
           try{
               System.out.println("THE SECURE PLATFORM RECEIVES AN ATTESTATE PLATFORM REQUEST");
               GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                                                           SecureCloudTPMHelper.NAME, null);
               command.addParam(request.getContentObject());
               command.addParam(request.getPostTimeStamp());
               myService.submit(command);
               String Response = "RESPONSE ATTESTATION REQUEST: 200";
               reply = request.createReply();
               reply.setPerformative(ACLMessage.INFORM);
               reply.setContent(Response);
           }catch(Exception se){
               System.out.println("THE PLATFORM COULD NOT REGISTER, TRY AGAIN LATER");
               String Response = "RESPONSE ATTESTATION REQUEST: 500";
               reply = request.createReply();
               reply.setPerformative(ACLMessage.INFORM);
               reply.setContent(Response);
           }
       }else{
           System.out.println("THE SECURE PLATFORM RECEIVES AN UNKNOWN MESSAGE, --IGNORING--");
       }
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
