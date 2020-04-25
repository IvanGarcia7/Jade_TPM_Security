package jade.core.CloudAgents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.BaseService;
import jade.core.GenericCommand;
import jade.core.SecureTPM.Agencia;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

import java.util.Calendar;
import java.util.Date;

public class SenderACLCloud extends SimpleAchieveREInitiator {

    private ACLMessage myMessage = null;
    private KeyPairCloudPlatform packetPlatform = null;
    private KeyPairCloudPlatform packetAgent = null;
    private Agent myAgent = null;
    private BaseService myService;

    public SenderACLCloud(ACLMessage message, KeyPairCloudPlatform requestPairAgent,
                          KeyPairCloudPlatform requestPairPlatform, Agent amsMainPlatform,
                          SecureAgentTPMService secureAgentTPMService) {
        super(amsMainPlatform,message);
        myMessage=message;
        packetPlatform = requestPairPlatform;
        packetAgent = requestPairAgent;
        myAgent = amsMainPlatform;
        myService = secureAgentTPMService;
    }

    /**
     * THIS FUNCTION TAKE AN ACL MESSAGE AND SEND IT TO THE DESTINY, SO
     * I NEED TO COMPLETE ALL THE INFORMATION REQUIRED IN BASE OD THE
     * REQUEST ATTESTATION THAT I HAVE RECEIVED PREVIOUSLY.
     * @param acl
     * @return
     */
    public ACLMessage prepareRequest(ACLMessage acl){
        try {
            myMessage.setContentObject(packetAgent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PROCEEDING TO SEND THE MESSAGE IN THE PREPARE REQUEST METHOD");
        AID receiver = new AID("ams@"+packetPlatform.getLocationPlatform().getName(),AID.ISGUID);

        System.out.println("SALCHICHAS");
        System.out.println(packetPlatform.getLocationPlatform().getName());
        System.out.println(packetPlatform.getLocationPlatform().getAddress());
        System.out.println(packetPlatform.getLocationPlatform().getID());
        System.out.println("SALCHICHAS");

        receiver.addAddresses(packetPlatform.getLocationPlatform().getAddress());
        System.out.println(receiver+" "+packetPlatform.getLocationPlatform().getAddress());
        myMessage.addReceiver(receiver);
        myMessage.setOntology(SecureAgentTPMHelper.REQUEST_INSERT_PLATFORM);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, Agencia.getTimeout());
        //SETTING THE TIMEOUT IN THE ACL MESSAGE
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);
        System.out.println("MESSAGE CREATE SUCCESFULLY");
        return myMessage;
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
                        SecureAgentTPMHelper.REQUEST_ACCEPT_PLATFORM,
                        SecureAgentTPMHelper.NAME,
                        null);
                myService.submit(cmd);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void handleRefuse(ACLMessage inform){
        System.out.println("REFUSING THE MESSAGE IN THE HANDLE REFUSE");
        try {
            GenericCommand cmd = new GenericCommand(
                    SecureAgentTPMHelper.REQUEST_ACCEPT_PLATFORM,
                    SecureAgentTPMHelper.NAME,
                    null);
            myService.submit(cmd);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }










}
