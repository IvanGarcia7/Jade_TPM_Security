package jade.core.SecureOnionTPM;

import jade.content.ContentManager;
import jade.core.*;
import jade.core.SecureInterTPM.ResponserACL;
import jade.core.SecureTPM.*;
import jade.core.behaviours.Behaviour;
import jade.core.mobility.Movable;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

    //SECURE VARIABLES THAT I WILL NEED TO IMPLEMENT THE ONION METHOD IN THE FUTURE
    private byte [] serialized_certificate = null;

    //KEYSTORAGELIST TO SAVE THE LOCATION AND THE CERTIFICATE OF EVERY HOST THAT THE PLATFORM SCAN
    List<PlatformID> device_list_host = new ArrayList<PlatformID>();

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
     * THIS FUNCTION TRY TO SIMULATE A CERTIFICATE MADE BY INFINEON
     * ON EVERY TPM. THIS CERTIFICATE, CONTAINS THE PUBLIC KEY OF
     * THE DEVICE, SIGNED BY INFINEON PRIVATE KEY;
     */
    public void defineCertificate(){
        if(serialized_certificate == null){
            serialized_certificate = Agencia.InfineonCertificate();
        }
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

        /**
         * THIS METHOD, TRY TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM, TO DEPLOY
         * THE BROADCAST MESSAGE.
         * @param agent
         * @param devices_list
         */
        public synchronized void sendAMSHostpots(SecureAgent agent, List<PlatformID> devices_list){
            StringBuilder sb = new StringBuilder();
            sb.append("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED NAME AGENT:").append(agent.getAID());
            System.out.println(sb.toString());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                              Level.INFO, true, this.getClass().getName());
            System.out.println("CREATE A NEW VERTICAL COMMAND TO PERFORM THE OPERATION THAT " +
                    "THE SERVICE NEED ");
            GenericCommand command = new GenericCommand(SecureOnionTPMHelper.REQUEST_ADDRESS,
                    SecureOnionTPMHelper.NAME, null);
            command.addParam(devices_list);
            Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS",
                    Level.INFO, true, this.getClass().getName());
            try {
                System.out.println("-> THE VERTICAL COMMAND TO COMMUNICATE IS CORRECTLY SUBMITED");
                SecureOnionTPMService.this.submit(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * THE COMMAND SOURCE SINK CONSUME THE VERTICAL COMMAND THAT I WANTED TO SEND.
     */
    private class CommandSourceSink implements Sink {
        @Override
        public void consume(VerticalCommand command) {
            try{
                String commandName = command.getName();
                if(commandName.equals(SecureOnionTPMHelper.REQUEST_ADDRESS)){
                    System.out.println("PROCEED THE COMMANDO TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM");
                    Object[] params = command.getParams();
                    SecureOnionTPMSlice obj = (SecureOnionTPMSlice) getSlice(MAIN_SLICE);
                    try{
                        obj.doCommunicateAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING THE COMMAND SOURCE SINK");
                        ie.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * THE COMMAND TARGET SINK PROCESS THE VERTICAL COMMAND THAT  I WANTED TO RECEIVE.
     */
    private class CommandTargetSink implements Sink {
        @Override
        public void consume(VerticalCommand command) {
            try{
                String CommandName = command.getName();
                if(CommandName.equals(SecureOnionTPMHelper.REQUEST_ADDRESS)){
                    /**
                     * AT THIS POINT, I CREATE THE MESSAGE AND SEND TO THE AMS, THEN
                     * REGISTER THE HOSPOTS AND START THE ONION PROTOCOL.
                     */
                    System.out.println("PROCESSING THE VERTICAL COMMAND ONION REQUEST INTO THE " +
                                       "AMS DESTINATION CONTAINER");

                    System.out.println("CHECKING IF THERE ARE PLATFORMS PASS BY PARAMS:");
                    ArrayList<PlatformID> hostpots = null;
                    try{
                        System.out.println("FETCHING THE PLATFORMS PASSED");
                        hostpots = (ArrayList<PlatformID>) command.getParams()[0];
                        device_list_host = hostpots;
                    }catch(NullPointerException ne){
                        System.out.println("NO PLATFORMS HAVE BEEN PASSED BY PARAMETER");
                    }

                    System.out.println("I'M IN THE AMS MAIN CONTAINER, AND THE LIST THAT I RECEIVE IS THE " +
                                       "FOLLOWING");
                    System.out.println("NAME OF THE CONAINER: "+actualcontainer.getID().getName());
                    for(int i=0;i<device_list_host.size();i++){
                        PlatformID platformhost = device_list_host.get(i);
                        System.out.println("*********************************");
                        System.out.println("LLAVERO NUMERO "+i+" CON EL SIGUIENTE CONTENIDO:");
                        System.out.println("ADDRESS: "+platformhost.getAddress());
                        System.out.println("NAME: "+platformhost.getName());
                        System.out.println("ID: "+platformhost.getID());
                        System.out.println("*********************************");
                    }
                    System.out.println("REGISTER ALL THE PLATFORMS SUCCESFULLY");
                }
            }catch(Exception ex){
                System.out.println("AN ERROR HAPPENED WHEN RUNNING THE SERVICE IN THE COMMAND TARGET SINK");
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
            try{
                String commandReceived = command.getName();
                if(commandReceived.equals(SecureOnionTPMSlice.REMOTE_ADDRESS_REQUEST)){
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND ONION MD IN THE SERVICE COMPONENT");
                    List<KeyStorage> device_hostpots = (ArrayList<KeyStorage>) command.getParams()[0];
                    commandResponse = new GenericCommand(SecureOnionTPMHelper.REQUEST_ADDRESS,
                                                         SecureOnionTPMHelper.NAME, null);
                    commandResponse.addParam(device_hostpots);
                }else{
                    System.out.println("SUNEO");
                }
            }catch(Exception e){
                System.out.println("AN ERROR HAPPENED WHEN PROCESS THE VERTICAL COMMAND IN THE SERVICECOMPONENT");
                e.printStackTrace();
            }
            return commandResponse;
        }
    }












}
