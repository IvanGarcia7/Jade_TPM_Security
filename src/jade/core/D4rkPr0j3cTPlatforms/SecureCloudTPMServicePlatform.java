package jade.core.D4rkPr0j3cTPlatforms;

import jade.core.*;
import jade.core.D4rkPr0j3cT.KeyPairCloud;
import jade.core.SecureTPM.Agencia;
import jade.core.behaviours.Behaviour;
import jade.core.mobility.Movable;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.security.*;
import java.util.*;
import java.util.logging.Level;

public class SecureCloudTPMServicePlatform extends BaseService {

    //INTERESTING VARIABLES.
    public static final String NAME = "jade.core.D4rkPr0j3cT.SecureCloudTPM";
    public static final String VERBOSE = "jade_core_D4rkPr0j3cT_SecureCloudTPMService_verbose";

    //TIME VAR THAT THE AGENCY USE TO SEE THE TIME THAT A REQUEST IT'S TAKEN.
    long startTime = System.nanoTime();

    //DEFINE THE SLICER AND THE HELPER TO EXECUTE CORRECTLY THE SERVICE.
    private Slice actualSlicer = new SecureCloudTPMServicePlatform.ServiceComponent();
    private ServiceHelper thisHelper = new SecureCloudTPMServicePlatform.SecureCloudTPMServiceHelperImpl();

    //DEFINE INPUT AND OUTPUT FILTERS.
    private Filter OutFilter = null;
    private Filter InFilter = null;

    //DEFINE INPUT AND OUTPUT SINKS.
    private Sink OutputSink = new SecureCloudTPMServicePlatform.CommandSourceSink();
    private Sink InputSink = new SecureCloudTPMServicePlatform.CommandTargetSink();

    //REQUEST THE COMMANDS THAT I HAVE IMPLEMENTED AND SAVE INTO A LIST.
    private String[] actualCommands = new String[]{
            SecureCloudTPMHelperPlatform.REQUEST_INSERT_PLATFORM,
            SecureCloudTPMHelperPlatform.REQUEST_ACCEPT_PLATFORM,
            SecureCloudTPMHelperPlatform.REQUEST_PACK_PLATFORM,
            SecureCloudTPMHelperPlatform.REQUEST_START,
            SecureCloudTPMHelperPlatform.REQUEST_LIST
    };

    //PERFORMATIVE PRINTER.
    private boolean verbose = false;
    private AgentContainer actualcontainer;

    private byte [] serialized_certificate = null;

    //KEYSTORAGELIST TO SAVE THE LOCATION AND THE CERTIFICATE OF EVERY HOST THAT THE PLATFORM SCAN
    List<PlatformID> device_list_host = new ArrayList<PlatformID>();

    //HASHMAP TO REGISTER THE ID OF THE NONCE, AND THE CONTAINER TO RESEND THE INFORMATION
    //THE FIRST VALUE ES THE NONCE AND THE SECOND THE CONTAINER NAME
    Map<String, String> listRedirects = new HashMap<String, String>();


    //SECURE CLOUD KEY PAIR FROM A PLATFORM;
    private byte [] privateKeyCA;
    private byte [] publicKeyCA;

    //DICT OF THE HOSTPOTS
    Map<Location,byte []> HostpotsRegister = new HashMap<Location,byte []>();

    /**
     * THIS FUNCTION GET THE NAME OF THE ACTUAL SERVICE.
     *
     * @return
     */
    @Override
    public String getName() {
        return SecureCloudTPMHelperPlatform.NAME;
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
        System.out.println("SECUE_CLOUD_TPM SERVICE STARTED CORRECTLY ON CONTAINER CALLED: " + actualcontainer.getID());

    }

    /**
     * THIS FUNCTION RETRIEVE THE SERVICE HELPER.
     *
     * @param agent
     * @return
     * @throws ServiceException
     */
    public ServiceHelper getHelper(Agent agent) throws ServiceException {
        if (agent instanceof SecureCAPlatformAgent) {
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
        return SecureCloudTPMSlicePlatform.class;
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
        System.out.println("THE CLOUD AMSBEHAVIOUR IS WORKING CORRECTLY");
        AID amsAID = new AID("ams", false);
        Agent ams = actualcontainer.acquireLocalAgent(amsAID);
        MessageTemplate mt =
                MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchOntology(SecureCloudTPMHelperPlatform.REQUEST_INSERT_PLATFORM),
                                MessageTemplate.MatchOntology(SecureCloudTPMHelperPlatform.REQUEST_PACK_PLATFORM)));
        ResponserCloudACLPlatform resp = new ResponserCloudACLPlatform(ams, mt, SecureCloudTPMServicePlatform.this);
        actualcontainer.releaseLocalAgent(amsAID);
        return resp;
    }


    public class SecureCloudTPMServiceHelperImpl implements SecureCloudTPMHelperPlatform {

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
         * THIS FUNCTION TRY TO INITIALIZE THE PLATFORMS, ACCORDING TO A KEY PAIR PROVEED
         * BY PARAMS IN ORDER TO SIMULATE IT. FIRST OF ALL, I NEED TO CONTACT WITH THE AMS
         * OF THE MAIN PLATFORM IN MY ENVIRONMENT.
         * @param secureCAPlatformAgent
         */
        @Override
        public synchronized void doStartCloudAgent(SecureCAPlatformAgent secureCAPlatformAgent,
                                                   Location caLocation, byte[] pubKey){

            StringBuilder sb = new StringBuilder();
            sb.append("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED NAME AGENT:")
                    .append(secureCAPlatformAgent.getAID());
            System.out.println(sb.toString());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                    Level.INFO, true, this.getClass().getName());
            System.out.println("CREATE A NEW VERTICAL COMMAND TO PERFORM THE OPERATION THAT " +
                    "THE SERVICE NEED ");
            GenericCommand command = new GenericCommand(SecureCloudTPMHelperPlatform.REQUEST_START,
                    SecureCloudTPMHelperPlatform.NAME, null);

            //AT THIS POINT FETC PUBLIC KEY TPM
            KeyPairCloudPlatform Generated_Pack = new KeyPairCloudPlatform(pubKey,caLocation);
            command.addParam(Generated_Pack);
            Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS",
                    Level.INFO, true, this.getClass().getName());
            try {
                System.out.println("-> THE VERTICAL COMMAND TO COMMUNICATE IS CORRECTLY SUBMITED");
                SecureCloudTPMServicePlatform.this.submit(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CommandSourceSink implements Sink {

        @Override
        public void consume(VerticalCommand command) {
            try{
                String commandName = command.getName();
                if(commandName.equals(SecureCloudTPMHelperPlatform.REQUEST_START)){
                    System.out.println("PROCEED THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM TO " +
                            "START THE AGENT");
                    Object[] params = command.getParams();
                    SecureCloudTPMSlicePlatform obj = (SecureCloudTPMSlicePlatform) getSlice(MAIN_SLICE);
                    try{
                        obj.doCommunicateAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST ADDRESS IN THE COMMAND SOURCE SINK");
                        ie.printStackTrace();
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class CommandTargetSink implements Sink {

        @Override
        public void consume(VerticalCommand command) {
            try{
                String CommandName = command.getName();
                if(CommandName.equals(SecureCloudTPMHelperPlatform.REQUEST_START)){
                    /**
                     * AT THIS POINT, I AM IN THE AMS MAIN PLATFORM, FIRST OF ALL, I COMMUNICATE WITH THE TPM
                     * IN CASE OF THE FIRST AGENT AND FETCH THE PUBLICKEY AND THE LOCATION OF THIS CONTAINER.
                     * THEN, I CIPHER THIS DATA WITH THE PUBLICKEY OF THE CLOUD AND SEND IT WITH AN ACL MESSAGE.
                     */
                    KeyPairCloudPlatform newPair = (KeyPairCloudPlatform)command.getParams()[0];
                    byte [] ekPub = Agencia.getEKPub();
                    Location actualLocation = actualcontainer.here();
                    CloudPackRequest newCloudRequest = new CloudPackRequest(newPair.getPublicPassword(),
                                                                            newPair.getLocationPlatform(),
                                                                            ekPub,
                                                                            actualLocation);
                    //SEND THE INFORMATION
                }
            }catch(Exception ex){
                System.out.println("AN ERROR HAPPENED WHEN RUNNING THE SERVICE IN THE COMMAND TARGET SINK");
                ex.printStackTrace();
            }
        }
    }

    private class ServiceComponent implements Slice {

        @Override
        public Service getService() {
            return SecureCloudTPMServicePlatform.this;
        }

        @Override
        public Node getNode() throws ServiceException {
            try {
                return SecureCloudTPMServicePlatform.this.getLocalNode();
            } catch (Exception e) {
                throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE CLOUD SERVICE COMPONENT");
            }
        }

        @Override
        public VerticalCommand serve(HorizontalCommand command) {
            GenericCommand commandResponse = null;
            try{
                String commandReceived = command.getName();
                if(commandReceived.equals(SecureCloudTPMSlicePlatform.REMOTE_REQUEST_START)) {
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE COMPONENT " +
                                       "TO START THE HOST");
                    commandResponse = new GenericCommand(SecureCloudTPMHelperPlatform.REQUEST_START,
                            SecureCloudTPMHelperPlatform.NAME, null);
                    KeyPairCloudPlatform keyPack = (KeyPairCloudPlatform) command.getParams()[0];
                    commandResponse.addParam(keyPack);
                }
            }catch(Exception e){
                System.out.println("AN ERROR HAPPENED WHEN PROCESS THE VERTICAL COMMAND IN THE SERVICECOMPONENT");
                e.printStackTrace();
            }
            return commandResponse;
        }
    }




}