package jade.core.CloudAgents;

import jade.core.Agent;
import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;

import java.util.Vector;

public class ResponserAgentACL extends SimpleAchieveREResponder {

    private Logger logger = Logger.getMyLogger(getClass().getName());
    private BaseService myService;


    public ResponserAgentACL(Agent ams, MessageTemplate mt, SecureAgentTPMService secureAgentTPMService) {
        super(ams, mt);
        myService = secureAgentTPMService;
    }


    /**
     * THIS FUNCTION TAKE A ACL REQUEST, AND PROCESS IT ATTEND TO THE TYPE OF THE REQUEST
     * @param request the received message
     * @return
     */
    protected ACLMessage prepareResponse(ACLMessage request) {
        System.out.println("PROCESSING THE REQUEST ATTESTATION IN THE DESTINY IN THE RESPONSER AGENT");
        ACLMessage reply = null;
        if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
            try{
                System.out.println("PLATFORM RECEIVE A ZONE 1 REQUEST TO ATTESTATE THE ORIGIN");
                GenericCommand command = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                        SecureAgentTPMHelper.NAME, null);
                command.addParam(request.getContentObject());
                myService.submit(command);
                System.out.println("ADIUSSSSSSSSS");
            }catch(Exception e){
                System.out.println("ERROR IN THE PREPARE RESPONSE OF THE ZONE 1");
                e.printStackTrace();
            }
        }else if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_ERROR)){
            try{
                System.out.println("PLATFORM RECEIVE A ERROR REQUEST TO ATTESTATE THE ORIGIN");
                System.out.println(request.getContent());
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
                System.out.println("ERROR IN THE CONFIRMATION RESPONSE OF THE ZONE 1");
                e.printStackTrace();
            }
        }else{
            System.out.println("AAAAAhuihnuhnuhuionhuiunA");
            reply = request.createReply();
        }
        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        System.out.println("PREPARING THE RESULT NOTIFICATION IN THE RESPONSER AGENT");
        //ACLMessage reply = request.createReply();
        //return reply;
        return null;
    }

    /**
     * THIS FUNCTION PROCEED WITH THE SECURE MOVE OR CLONATION, IF THE MAXIMUM WAITING TIME IS NOT EXPIRED.
     * @param inform
     */
    protected void handleInform(ACLMessage inform){
        System.out.println("CATCH THE ACL MESSAGE IN THE HANDLE INFORM IN THE RESPONSER AGENT");
        System.out.println("**********************************************************");
    }


    protected void handleAgree(ACLMessage agree){
        System.out.println("hrvewrevwe");
    }

    protected void handleRefuse(ACLMessage refuse){
        System.out.println("hwevew");
    }

    protected void handleAllResponses(Vector responses) {
        System.out.println("hrr");
    }

    protected void handleNotUnderstood(ACLMessage notUnderstood){
        System.out.println("hrr");
    }

    protected void handleOutOfSequence(ACLMessage outOfSequence) {
        System.out.println("hr");
    }

    protected void handleFailure(ACLMessage failure){
        System.out.println("h");
    }




}
