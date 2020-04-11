package jade.core.SecureTPM.INTER;

import java.util.Vector;

import jade.core.SecureTPM.INTER.SecureAgentInterHelper;
import jade.core.SecureTPM.INTER.SecureAgentInterService;
import jade.core.SecureTPM.INTERFAZ_TPM.Agencia;
import jade.core.SecureTPM.PETICIONES.PeticionConfirmacion;
import jade.proto.SimpleAchieveREInitiator;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.GenericCommand;
import jade.core.BaseService;
import java.util.Date;
import java.util.Calendar;
import java.util.logging.Level;


/** The <code> AMSREInitiatorRequest </code> class implements the initiator
 * behaviour that sends the remote attestation request to the remote platform.
 *
 * @author Andr�s Rosado Laitinen
 *
 */
public class AMSREInitiatorRequest extends SimpleAchieveREInitiator {

    private static final long serialVersionUID = -7838365184199564719L;
    private ACLMessage myMessage;
    private BaseService myService;
    private PeticionConfirmacion pc;


    public AMSREInitiatorRequest(Agent a, ACLMessage msg, PeticionConfirmacion request, BaseService service) {
        super(a,msg);
        pc=request;
        myService=service;
        myMessage=msg;
    }


    public ACLMessage prepareRequest(ACLMessage acl){
        //AttestTool.printLog("AMSREInitiator: Preparing request message.",Level.INFO,SecureMigrationServiceHelper.LOG);
        try {
            // Add the request data to the message
            byte [] salida = Agencia.serialize(pc);
            acl.setContentObject(salida);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Add the receiver data to the message
        AID receiver = new AID("ams@"+pc.getLocationdestino().getName(),AID.ISGUID);
        receiver.addAddresses(pc.getLocationdestino().getAddress());
        myMessage.addReceiver(receiver);
        // Set the ontology data.
        myMessage.setOntology(SecureAgentInterService.SecureAgentInterHelper.REQUEST_ATTESTATION);
        //Calendar
        Calendar c = Calendar.getInstance();
        //System.out.println("TIMEOUT PROPERTY: "+myAgent.getProperty(SecureMigrationServiceHelper.TIMEOUT_KEY,null));
        //int timeout = Integer.valueOf(myAgent.getProperty(SecureMigrationServiceHelper.TIMEOUT_KEY,"3000"));
        c.add(Calendar.SECOND, Agencia.getTimeout());
        Date t = new Date(c.getTimeInMillis());
        myMessage.setReplyByDate(t);
        return myMessage;
    }


    /** The <code> handleAllResponses </code> method is invoked when
     * all the responses have been collected or when the timeout is expired.handles the refuse response.
     *
     * @param responses the Vector of ACLMessage objects that have been received
     *
     */
    protected void handleAllResponses(Vector responses) {
        if (responses.size()==0) {//The timeout is expired
            GenericCommand cmd = new GenericCommand(SecureAgentInterHelper.REQUEST_CONFIRMATION,
                                        SecureAgentInterHelper.NAME, null);
            try {
                myService.submit(cmd);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
