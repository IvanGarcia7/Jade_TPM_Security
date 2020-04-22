package jade.core.SecureOnionTPM;

import jade.core.*;
import jade.core.SecureInterTPM.ResponserACL;
import jade.core.SecureInterTPM.SecureInterTPMHelper;
import jade.core.SecureInterTPM.SecureInterTPMService;
import jade.core.SecureInterTPM.SecureInterTPMSlice;
import jade.core.SecureTPM.RequestClone;
import jade.core.SecureTPM.RequestMove;
import jade.core.SecureTPM.SecureAgent;
import jade.core.behaviours.Behaviour;
import jade.core.mobility.Movable;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

public class SecureOnionTPMService extends BaseService {


    //INTERESTING VARIABLES.
    public static final String NAME = "jade.core.SecureOnionTPM.SecureOnionTPM";
    public static final String VERBOSE = "jade_core_SecureOnionTPMS_SecureOnionTPMService_verbose";

    //TIME VAR THAT THE AGENCY USE TO SEE THE TIME THAT A REQUEST IT'S TAKEN.
    long startTime = System.nanoTime();

    //DEFINE THE SLICER AND THE HELPER TO EXECUTE CORRECTLY THE SERVICE.
    private Slice actualSlicer = new SecureOnionTPMService.ServiceComponent();
    private ServiceHelper thisHelper = new SecureOnionTPMService.SecureOnionTPMServiceHelperImpl();

    //DEFINE INPUT AND OUTPUT FILTERS.
    private Filter OutFilter = null;
    private Filter InFilter = null;

    //DEFINE INPUT AND OUTPUT SINKS.
    private Sink OutputSink = new SecureOnionTPMService.CommandSourceSink();
    private Sink InputSink = new SecureOnionTPMService.CommandTargetSink();

    //REQUEST THE COMMANDS THAT I HAVE IMPLEMENTED AND SAVE INTO A LIST.
    private String[] actualCommands = new String[]{
            SecureOnionTPMHelper.REQUEST_ADDRESS,
            SecureOnionTPMHelper.CONFIRM_ADDRESS
    };

    //PERFORMATIVE PRINTER.
    private boolean verbose = false;
    private AgentContainer actualcontainer;


    /**
     * THIS FUNCTION GET THE NAME OF THE ACTUAL SERVICE.
     *
     * @return
     */
    @Override
    public String getName() {
        return SecureOnionTPMHelper.NAME;
    }


    /**
     * THIS FUNCTION INIT THE SERVICE, CALLING THE SUPER METHODS THAT CONTAINS THE BASE SERVICE.
     *
     * @param agentcontainer
     * @param prof
     * @throws ProfileException
     */
    public void init(AgentContainer agentcontainer, Profile prof) throws ProfileException {
        super.init(agentcontainer, prof);
        actualcontainer = agentcontainer;
    }

    /**
     * THIS FUNCTION EXECUTE THE START-UPS CONFIGURATIONS FOR THIS METHOD
     *
     * @param prof
     * @throws ServiceException
     */
    public void boot(Profile prof) throws ServiceException {
        super.boot(prof);
        verbose = prof.getBooleanProperty(VERBOSE, false);
        System.out.println("SECUEONIONTPM SERVICE STARTED CORRECTLY ON CONTAINER CALLED: " + actualcontainer.getID());

    }

    /**
     * THIS FUNCTION RETRIEVE THE SERVICE HELPER.
     *
     * @param agent
     * @return
     * @throws ServiceException
     */
    public ServiceHelper getHelper(Agent agent) throws ServiceException {
        if (agent instanceof SecureAgent) {
            return thisHelper;
        } else {
            throw new ServiceException("THIS SERVICE IS NOT ALLOWED TO RUN IN THIS AGENT");
        }
    }

    /**
     * FUNCTION TO GET THE SLICE TO EXECUTE HORIZONTAL COMMANDS.
     *
     * @return
     */
    public Class getHorizontalInterface() {
        return SecureOnionTPMSlice.class;
    }

    /**
     * FUNCTION TO GET THE ACTUAL SLICER.
     *
     * @return
     */
    public Slice getLocalSlice() {
        return actualSlicer;
    }

    /**
     * FUNCTION TO GET THE COMMANDS THAT I IMPLEMENTED FOR THIS SERVICE IN THE HELPER.
     *
     * @return
     */
    public String[] getOwnedCommands() {
        return actualCommands;
    }

    /**
     * RETRIEVE THE FILTERS IF IT WAS NECESSARY
     *
     * @param direction
     * @return
     */
    public Filter getCommandFilter(boolean direction) {
        if (direction == Filter.OUTGOING) {
            return OutFilter;
        } else {
            return InFilter;
        }
    }

    /**
     * RETRIEVE THE SINKS IF IT WAS NECESSARY
     *
     * @param side
     * @return
     */
    public Sink getCommandSink(boolean side) {
        if (side == Sink.COMMAND_SOURCE) {
            return OutputSink;
        } else {
            return InputSink;
        }
    }

    /**
     * DEFINE THE BEHAVIOUR TO SEND AND RECEIVE MESSAGES
     */
    public Behaviour getAMSBehaviour() {
        System.out.println("THE ONION AMSBEHAVIOUR IS WORKING CORRECTLY");
        AID amsAID = new AID("ams", false);
        Agent ams = actualcontainer.acquireLocalAgent(amsAID);
        MessageTemplate mt =
                MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchOntology(SecureOnionTPMHelper.REQUEST_ADDRESS),
                                MessageTemplate.MatchOntology(SecureOnionTPMHelper.CONFIRM_ADDRESS)));
        ResponserACL resp = new ResponserACL(ams, mt, SecureOnionTPMService.this);
        actualcontainer.releaseLocalAgent(amsAID);
        return resp;
    }

    public class SecureOnionTPMServiceHelperImpl implements SecureOnionTPMHelper {

        //REFERENCE MY SECURE AGENT
        private Agent mySecureAgent;
        private Movable myMovable;

        @Override
        public void init(Agent agent) {
            mySecureAgent = agent;
        }

        public void registerMovable(Movable m) {
            myMovable = m;
        }

        public synchronized void sendBroadcastACL(SecureAgent agent, Location destiny){
            //TO-DEFINE
        }

    }

    /**
     * THE COMMAND SOURCE SINK CONSUME THE VERTICAL COMMAND THAT I WANTED TO SEND.
     */
    private class CommandSourceSink implements Sink {
        @Override
        public void consume(VerticalCommand command) {

        }
    }

    /**
     * THE COMMAND TARGET SINK PROCESS THE VERTICAL COMMAND THAT  I WANTED TO RECEIVE.
     */
    private class CommandTargetSink implements Sink {
        @Override
        public void consume(VerticalCommand command) {

        }
    }

    /**
     * THIS CLASS CONSUME THE HORIZONTAL COMMAND THAT I RECEIVE.
     */
    private class ServiceComponent implements Slice {
        @Override
        public Service getService() {
            return SecureOnionTPMService.this;
        }

        @Override
        public Node getNode() throws ServiceException {
            try {
                return SecureOnionTPMService.this.getLocalNode();
            } catch (Exception e) {
                throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE ONION SERVICE COMPONENT");
            }
        }

        @Override
        public VerticalCommand serve(HorizontalCommand command) {
            GenericCommand commandResponse = null;
            return commandResponse;
        }
    }












}
