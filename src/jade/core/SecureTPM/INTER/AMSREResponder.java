package jade.core.SecureTPM.INTER;

import java.util.logging.Level;

import jade.core.Agent;
import jade.core.BaseService;
import jade.core.SecureTPM.INTER.SecureAgentInterHelper;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREResponder;
import jade.util.Logger;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.core.Profile;
import jade.core.AID;

import jade.core.Location;

/** The <code> AMSREResponder </code> class implements the responder
 * behaviour that handles the attestation requests messages.
 *
 * @author Andr�s Rosado Laitinen
 *
 */
public class AMSREResponder extends SimpleAchieveREResponder {

    private static final long serialVersionUID = -5573777298399418189L;
    private Logger logger = Logger.getMyLogger(getClass().getName());
    //private AttestTool_Impl myATools;
    private Profile myProfile;
    private Location myLocation;


    public AMSREResponder(Agent a, MessageTemplate mt, Profile p, Location l) {
        super(a,mt);
        myProfile=p;
        myLocation=l;
    }

    /** The <code> prepareResponse </code> method is used to generate the response.
     *
     * @param request	The request received.
     *
     * @return			The generated response message.
     */
    protected ACLMessage prepareResponse(ACLMessage request) {
        // Create the agree reply
        ACLMessage reply = request.createReply();
        //AttestRequest_Interface ar=null;

		/*System.out.println("RECIBIDO MENSAJE: ");
		System.out.println("Emisor: "+request.getSender());
		System.out.println("Receptores: ");
		java.util.Iterator it = request.getAllReceiver();
		while (it.hasNext()) {
			System.out.println("-"+it.next());
		}
		System.out.println("Performative: "+request.getPerformative());
		System.out.println("Ontology: "+request.getOntology());
		System.out.println("Reply to: ");
		java.util.Iterator it2 = request.getAllReplyTo();
		while (it2.hasNext()) {
			System.out.println("-"+it2.next());
		}
		*/
        try {
            //ar = (AttestRequest_Interface) request.getContentObject();
            // Check the ontology


            /*
            if ((request.getOntology().equals(SecureAgentInterHelper.ATTEST_DATA))) {
                AttestTool_Impl.printLog("Received an attestation data request from a remote platform.",Level.INFO,SecureInterPlatformMobilityHelper.LOG,this.getClass().getName());
                // If it is a request with data we must check the configuration
                int status = myATools.fillResult(ar);
                if (status == AttestTool_Interface.ATT_OK) {
                    // The requester configuration is correct, we agree the request
                    AttestTool_Impl.printLog("Requester configuration OK, sending the requested attestation data.",Level.INFO,SecureInterPlatformMobilityHelper.LOG,this.getClass().getName());
                    reply.setPerformative(ACLMessage.AGREE);
                }
                if (status == AttestTool_Interface.ATT_REFUSE) {
                    // The requester configuration is dangerous, we refuse the request
                    AttestTool_Impl.printLog("Requester configuration error, sending a refuse message.",Level.INFO,SecureInterPlatformMobilityHelper.LOG,this.getClass().getName());
                    reply.setPerformative(ACLMessage.REFUSE);
                    ar.setStatusMessage("REQUESTER CONFIGURATION ERROR");
                }
                else {

                }
            }
            else
            */
            if ((request.getOntology().equals(SecureAgentInterHelper.REQUEST_ATTESTATION))){

                //	If it is a attestation request we agree
                //AttestTool_Impl.printLog("Received a request from a remote platform."+request.getSender(),Level.INFO,SecureInterPlatformMobilityHelper.LOG,this.getClass().getName());
                //AttestTool_Impl.printLog("Sending the nounce data for the attestation to ."+reply.getAllReceiver().next(),Level.INFO,SecureInterPlatformMobilityHelper.LOG,this.getClass().getName());
                reply.setPerformative(ACLMessage.AGREE);
                //myATools=AttestTool_Impl.getInstace(myProfile);
                //myATools.fillAgree(ar);
            }
            else {
                // The request is unknown
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
            }
            //reply.setContentObject(ar);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		/*System.out.println("CONVERSATION ID: "+request.getConversationId());
		reply.removeReceiver(request.getSender());
		reply.removeReplyTo(request.getSender());
		System.out.println("RECEIVERS: ");
		java.util.Iterator it=request.getAllIntendedReceiver();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}*/
        return reply;
    }

    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        if (logger.isLoggable(Logger.FINE)) {
            logger.log(Logger.FINE,	"AMSResponder: Request message received.");
        }

        // Create the agree reply
        ACLMessage reply = request.createReply();
        try {
            // We have previously filled the request with the data	so we only have
            // to put the data in the INFORM message
            reply.setPerformative(ACLMessage.INFORM);
            /*
            AttestRequest_Interface ar=(AttestRequest_Interface) response.getContentObject();
            reply.setContentObject(ar);
             */
            //System.out.println("SIN EMBARGO AKI VALE: "+ar);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reply.setSender(new AID("pepe",AID.ISGUID));
        return reply;


    }

}