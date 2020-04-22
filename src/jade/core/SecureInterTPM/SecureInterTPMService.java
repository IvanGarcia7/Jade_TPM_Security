package jade.core.SecureInterTPM;

import jade.core.*;
import jade.core.SecureTPM.*;
import jade.core.behaviours.Behaviour;
import jade.core.mobility.Movable;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.FIPAProtocolNames;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SecureInterTPMService extends BaseService {

    //INTERESTING VARIABLES.
    public static final String NAME = "jade.core.SecureInterTPM.SecureInterTPM";
    public static final String VERBOSE = "jade_core_SecureInterTPMS_SecureInterTPMService_verbose";

    //TIME VAR THAT THE AGENCY USE TO SEE THE TIME THAT A REQUEST IT'S TAKEN.
    long startTime = System.nanoTime();

    //LIST OF PETITIONS.
    private List<RequestMove> request_move_list = new ArrayList<RequestMove>();
    private List<RequestClone> request_clone_list = new ArrayList<RequestClone>();

    //DEFINE THE SLICER AND THE HELPER TO EXECUTE CORRECTLY THE SERVICE.
    private Slice actualSlicer = new ServiceComponent();
    private ServiceHelper thisHelper = new SecureInterTPMServiceHelperImpl();

    /**
     * THUS FUNCTION SEE ITH THE TPM IS WORKING ON ANOTHER REQUEST.
     * RETURN TRUE IF THE TPM IS IN USE.
     */
    public boolean isBlocked() {
        return (request_clone_list.size() + request_move_list.size()) > 0;
    }

    //DEFINE INPUT AND OUTPUT FILTERS.
    private Filter OutFilter = null;
    private Filter InFilter = null;

    //DEFINE INPUT AND OUTPUT SINKS.
    private Sink OutputSink = new CommandSourceSink();
    private Sink InputSink = new CommandTargetSink();

    //REQUEST THE COMMANDS THAT I HAVE IMPLEMENTED AND SAVE INTO A LIST.
    private String[] actualCommands = new String[]{
            SecureInterTPMHelper.REQUEST_ATTESTATION,
            SecureInterTPMHelper.REQUEST_CONFIRMATION
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
        return SecureInterTPMHelper.NAME;
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
        System.out.println("SECUREINTERTPM SERVICE STARTED CORRECTLY ON CONTAINER CALLED: " + actualcontainer.getID());

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
        return SecureInterTPMSlice.class;
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

    public Behaviour getAMSBehaviour() {
        System.out.println("THE AMSBEHAVIOUR IS WORKING CORRECTLY");
        AID amsAID = new AID("ams", false);
        Agent ams = actualcontainer.acquireLocalAgent(amsAID);
        MessageTemplate mt =
                MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchOntology(SecureInterTPMHelper.REQUEST_ATTESTATION),
                                MessageTemplate.MatchOntology(SecureInterTPMHelper.REQUEST_CONFIRMATION)));
        ResponserACL resp = new ResponserACL(ams, mt, SecureInterTPMService.this);
        actualcontainer.releaseLocalAgent(amsAID);
        return resp;

    }

    public class SecureInterTPMServiceHelperImpl implements SecureInterTPMHelper {

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

        /**
         THE CLONE AND MOVE HANDLER WILL BE SIMILAR BECAUSE THE REQUEST ATTESTATION IS SIMILAR
         IN BOTH OF THEM
         */

        /**
         * This method take an agent, and the location requested to move.
         * First of all, review if the TPM is free to handler with the opperation needit
         * and then inicialize the process
         *
         * @param agent
         * @param destiny
         */
        public synchronized void doMoveTPM(SecureAgent agent, Location destiny) {
            StringBuilder sb = new StringBuilder();
            if (!isBlocked()) {
                request_move_list.add(new RequestMove(agent, destiny));
                sb.append("-> THE PROCCES TO MOVE THE AGENT HAS JUST STARTED").append(agent.getAID());
                System.out.println(sb.toString());
                Agencia.printLog("START THE SERVICE TO MOVE THE SECUREAGENT",
                        Level.INFO, true, this.getClass().getName());
                System.out.println("CREATE A NEW VERTICAL ATTESTATION COMMAND TO PERFORM THE OPERATION THAT " +
                        "THE SERVICE NEED ");
                GenericCommand command = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION,
                        SecureInterTPMHelper.NAME, null);
                System.out.println("CREATING THE PACKET WITH THE INFORMATION THAT I WANT TO SEND FROM MY NODE");
                RequestAttestation paquet = new RequestAttestation(agent.getAID(), agent.here(), destiny, agent.getAMS());
                command.addParam(paquet);
                Agencia.printLog("AGENT REQUEST MOVE AND SEND THE REQUEST TO THE AGENCY",
                        Level.INFO, true, this.getClass().getName());
                try {
                    System.out.println("-> THE VERTICAL COMMAND TO MOVE IS CORRECTLY SUBMITED");
                    SecureInterTPMService.this.submit(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append("ITS NOT POSSIBLE TO START THE MOVE PROCESS  ").append(agent.getAID())
                        .append(" BUSSY TPM HANDLER OTHER REQUEST");
                Agencia.printLog("ITS NOT POSSIBLE TO START THE MOVE PROCESS " +
                        " BUSSY TPM HANDLER OTHER REQUEST", Level.INFO, true, this.getClass().getName());
                agent.doSecureMoveError(sb.toString());
            }
        }


        /**
         * This method take an agent, and the location requested to move.
         * First of all, review if the TPM is free to handler with the opperation needit
         * and then inicialize the process
         *
         * @param agent
         * @param destiny
         */
        public synchronized void doCloneTPM(SecureAgent agent, Location destiny, String nameAgent) {
            StringBuilder sb = new StringBuilder();
            if (!isBlocked()) {
                request_clone_list.add(new RequestClone(agent, destiny, nameAgent));
                sb.append("THE PROCCES TO CLONE THE AGENT HAS JUST STARTED").append(agent.getAID());
                System.out.println(sb.toString());
                Agencia.printLog("START THE SERVICE TO CLONE THE SECUREAGENT",
                        Level.INFO, true, this.getClass().getName());
                System.out.println("CREATE A NEW VERTICAL ATTESTATION COMMAND TO PERFORM THE OPERATION THAT " +
                        "THE SERVICE NEED TO PERFORM");
                GenericCommand command = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION,
                        SecureInterTPMHelper.NAME, null);
                System.out.println("CREATING THE PACKET WITH THE INFORMATION THAT I WANT TO SEND FROM MY NODE");
                RequestAttestation paquet = new RequestAttestation(agent.getAID(), agent.here(), destiny, agent.getAMS());
                command.addParam(paquet);
                Agencia.printLog("AGENT REQUEST CLONATION AND SEND THE REQUEST TO THE AGENCY",
                        Level.INFO, true, this.getClass().getName());
                try {
                    System.out.println("THE VERTICAL COMMAND TO CLONE IS CORRECTLY SUBMITED");
                    SecureInterTPMService.this.submit(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sb.append("ITS NOT POSSIBLE TO START THE CLONATION PROCESS  ").append(agent.getAID())
                        .append(" BUSSY TPM HANDLER OTHER REQUEST");

                Agencia.printLog("ITS NOT POSSIBLE TO START THE CLONATION PROCESS " +
                        " BUSSY TPM HANDLER OTHER REQUEST", Level.INFO, true, this.getClass().getName());
                agent.doSecureCloneError(sb.toString());
            }
        }
    }

    /**
     * THE COMMAND SOURCE SINK CONSUME THE VERTICAL COMMAND THAT I WANTED TO SEND.
     */
    private class CommandSourceSink implements Sink {

        @Override
        public void consume(VerticalCommand command) {
            try {
                String commandName = command.getName();
                if (commandName.equals(SecureInterTPMHelper.REQUEST_ATTESTATION)) {
                    System.out.println("-> VERTICAL COMMAND CATCH BY CONSUME SOURCE SINK");
                    //Process the command before to send it
                    Object[] params = command.getParams();
                    RequestAttestation pack = (RequestAttestation) params[0];
                    SecureInterTPMSlice obj = (SecureInterTPMSlice) getSlice(MAIN_SLICE);
                    try {
                        obj.doRequestAttestation(command);
                        startTime = System.nanoTime();
                    } catch (Exception ie) {
                        ie.printStackTrace();
                        Agencia.setStatus(false);
                    }
                } else {
                    System.out.println("THE MOVE IS GOING TO START");
                    SecureAgent a = request_move_list.get(0).getSecureAgent();
                    Location b = request_move_list.get(0).getLocation();
                    System.out.println(a);
                    System.out.println(b.getID());
                    System.out.println(b.getName());
                    System.out.println(b.getAddress());
                    a.doMove(b);
                    request_move_list.clear();
                }
            } catch (Exception e) {
                command.setReturnValue(new Exception("THERE ARE AN ERROR IN THE SERVICE, " +
                        "REJECT THE REQUEST OPERATION"));
            }
        }
    }



    /**
     * THE COMMAND TARGET SINK PROCESS THE VERTICAL COMMAND THAT  I WANTED TO RECEIVE.
     */
    private class CommandTargetSink implements Sink {

        @Override
        public void consume(VerticalCommand command) {
            try {
                String CommandName = command.getName();
                if (CommandName.equals(SecureInterTPMHelper.REQUEST_ATTESTATION)) {
                    //At this point process the request attestation that i send in the other container
                    System.out.println("PROCESSING THE VERTICAL COMMAND ATTESTATION INTO THE DESTINATION CONTAINER");
                    RequestAttestation pack = (RequestAttestation) command.getParams()[0];
                    //CREATING THE NEW VERTICAL COMMAND
                    GenericCommand newCommand = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION,
                            SecureInterTPMHelper.NAME, null);

                    //ADD THE PACK
                    newCommand.addParam(pack);
                    AID amsMain = new AID("ams", false);
                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    amsMainPlatform.addBehaviour(
                            new SenderACL(message, pack, amsMainPlatform, SecureInterTPMService.this)
                    );
                    actualcontainer.releaseLocalAgent(amsMain);
                } else if (CommandName.equals(SecureInterTPMHelper.REQUEST_CONFIRMATION)) {
                    if (AgentContainer.MAIN_CONTAINER_NAME.equals(actualcontainer.getID().getName())) {
                        //At this point process the request confirmation that i send in the other container
                        System.out.println("PROCESSING THE VERTICAL COMMAND CONFIRMATION INTO THE DESTINATION CONTAINER");
                        AID amsDestiny = new AID("ams", false);
                        Agent amsActual = actualcontainer.acquireLocalAgent(amsDestiny);
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        RequestConfirmation pack = (RequestConfirmation) command.getParams()[0];
                        amsActual.addBehaviour(
                                new SenderACL2(message, pack, amsActual, SecureInterTPMService.this)
                        );
                        actualcontainer.releaseLocalAgent(amsDestiny);
                    } else {
                        if (request_move_list.size() > 0) {
                            //EXECUTE THE MODIFIED FUNCTION TO MOVE
                            SecureAgent security = request_move_list.get(0).getSecureAgent();
                            security.doMove(request_move_list.get(0).getLocation());
                            request_move_list.clear();
                        } else if (request_clone_list.size() > 0) {
                            //EXECUTE THE MODIFIED FUNCTION TO CLONE
                            SecureAgent security = request_clone_list.get(0).getSecureAgent();
                            security.doClone(request_clone_list.get(0).getLocation(),
                                    request_clone_list.get(0).getNewName());
                            request_clone_list.clear();
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("AN ERROR HAPPENED WHEN RUNNING THE SERVICE");
                ex.printStackTrace();
            }
        }
    }


        /**
         * THIS CLASS CONSUME THE HORIZONTAL COMMAND THAT I RECEIVE.
         */
        private class ServiceComponent implements Slice {

            @Override
            public Service getService() {
                return SecureInterTPMService.this;
            }

            @Override
            public Node getNode() throws ServiceException {
                try {
                    return SecureInterTPMService.this.getLocalNode();
                } catch (Exception e) {
                    throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE SERVICE COMPONENT");
                }
            }

            @Override
            public VerticalCommand serve(HorizontalCommand command) {
                GenericCommand commandResponse = null;
                try {
                    String commandReceive = command.getName();
                    if (commandReceive.equals(SecureInterTPMSlice.REMOTE_REQUEST_ATTESTATION)) {
                        System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND ATTESTATION IN THE SERVICE COMPONENT");
                        //Fetch the packet that i send
                        RequestAttestation pack = (RequestAttestation) command.getParams()[0];
                        //Transform the horizontal command into a vertical command
                        commandResponse = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION, SecureInterTPMHelper.NAME, null);
                        commandResponse.addParam(pack);
                    } else if (commandReceive.equals(SecureInterTPMSlice.REMOTE_REQUEST_CONFIRMATION)) {
                        System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CONFIRMATION IN THE SERVICE COMPONENT");
                        //Fetch the packet that i send
                        RequestConfirmation pack = (RequestConfirmation) command.getParams()[0];
                        //Transform the horizontal command into a vertical command
                        commandResponse = new GenericCommand(SecureInterTPMHelper.REQUEST_CONFIRMATION, SecureInterTPMHelper.NAME, null);
                        commandResponse.addParam(pack);
                    }
                } catch (Exception e) {
                    System.out.println("AN ERROR HAPPENED WHEN PROCESS THE VERTICAL COMMAND IN THE SERVICECOMPONENT");
                    e.printStackTrace();
                }
                return commandResponse;
            }
        }
}



