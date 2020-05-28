package jade.core.SecureAgent;



import jade.core.Agent;
import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.core.SecureTPM.Agencia;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SimpleAchieveREResponder;

import javax.swing.*;
import java.util.Vector;
import java.util.logging.Level;


public class ResponseAgentACL extends SimpleAchieveREResponder {

    private BaseService myService;
    private JTextArea Printer;

   
   


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

        SecureAgentTPMService myServiceHelper = (SecureAgentTPMService) myService;
        Printer = myServiceHelper.getGUI();
        

        Printer.append("\nPROCESSING THE RESPONSE IN THE AGENT\n");
        Agencia.printLog("PROCESSING THE REQUEST IN THE AGENT DESTINY RESPONSER AGENT", Level.INFO,
                        SecureAgentTPMHelper.DEBUG, this.getClass().getName());
        ACLMessage reply = null;

        if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
            try{
            	Printer.append("PLATFORM RECEIVE A ZONE 1 REQUEST TO ATTESTATE THE ORIGIN \n\n");
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
            	Printer.append("\n\nPLATFORM RECEIVE A CONFIRMATION ORIGIN SECURE MOVE \n");

                System.out.println("PLATFORM RECEIVE A CONFIRMATION ORIGIN SECURE MOVE");
                GenericCommand command = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM,
                        SecureAgentTPMHelper.NAME, null);
                command.addParam(request.getContentObject());
                myService.submit(command);
            }catch(Exception e){
                System.out.println("ERROR IN THE ORIGIN CONFIRMATION RESPONSE OF THE ZONE 2");
                e.printStackTrace();
            }
        }else if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE3_PLATFORM)){
            try{
            	Printer.append("PLATFORM RECEIVE A CONFIRMATION DESTINY SECURE MOVE \n");
                Printer.append("****** CHECKING THE CONFIRM LIST ******");
                System.out.println("PLATFORM RECEIVE A CONFIRMATION DESTINY SECURE MOVE");
                GenericCommand command = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE3_PLATFORM,
                        SecureAgentTPMHelper.NAME, null);
                command.addParam(request.getContentObject());
                myService.submit(command);
            }catch(Exception e){
                System.out.println("ERROR IN THE DESTINY CONFIRMATION RESPONSE OF THE ZONE 2");
                e.printStackTrace();
            }
        } else if(request.getOntology().equals(SecureAgentTPMHelper.REQUEST_ERROR)){
        	Printer.append("PLATFORM RECEIVE A ERROR REQUEST WHILE ATTESTATE THE ORIGIN \n");
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
        Printer.append("\nRESPONSE FROM THE SECURE PLATFORM: \n"+inform.getContent()+" \n\n");
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
