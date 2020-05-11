package jade.core.SecureAgent;

import jade.core.*;
import jade.core.SecureCloud.SecureCAConfirmation;
import jade.core.SecureCloud.SecureChallengerPacket;
import jade.core.SecureCloud.SecureCloudTPMHelper;
import jade.core.SecureCloud.SecureInformationCloud;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.core.behaviours.Behaviour;
import jade.core.mobility.Movable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.nio.file.Files;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;


public class SecureAgentTPMService extends BaseService {

    public static final String NAME = "jade.core.SecureAgent.SecureAgentTPM";
    public static final String VERBOSE = "jade_core_SecureAgent_SecureAgentTPMService_verbose";

    //TIME VAR THAT THE AGENCY USE TO SEE THE TIME THAT A REQUEST IT'S TAKEN.
    long startTime = System.nanoTime();

    //DEFINE THE SLICER AND THE HELPER TO EXECUTE CORRECTLY THE SERVICE.
    private Slice actualSlicer = new SecureAgentTPMService.ServiceComponent();
    private ServiceHelper thisHelper = new SecureAgentTPMServiceHelperImpl();

    //DEFINE INPUT AND OUTPUT FILTERS.
    private Filter OutFilter = null;
    private Filter InFilter = null;

    //DEFINE INPUT AND OUTPUT SINKS.
    private Sink OutputSink = new SecureAgentTPMService.CommandSourceSink();
    private Sink InputSink = new SecureAgentTPMService.CommandTargetSink();

    //REQUEST THE COMMANDS THAT I HAVE IMPLEMENTED AND SAVE INTO A LIST.
    private String[] actualCommands = new String[]{
            SecureAgentTPMHelper.REQUEST_START,
            SecureAgentTPMHelper.REQUEST_LIST,
            SecureAgentTPMHelper.REQUEST_INSERT_PLATFORM,
            SecureAgentTPMHelper.REQUEST_ACCEPT_PLATFORM,
            SecureAgentTPMHelper.REQUEST_PACK_PLATFORM,
            SecureAgentTPMHelper.REQUEST_MIGRATE_PLATFORM,
            SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
            SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM,
            SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE3_PLATFORM,
            SecureAgentTPMHelper.REQUEST_ERROR
    };

    //PERFORMATIVE PRINTER.
    private boolean verbose = false;
    private AgentContainer actualcontainer;

    //MY PUBLIC AND PRIVATE KEY OF THE AGENT
    private PrivateKey privKeyAgent;
    private PublicKey pubKeyAgent;

    //INDEX OF THE NVRAM WHERE THE KEYS ARE LOADED
    private String contextEK="";
    private String contextAK = "";

    //MY AIK PUB SIGNED KEY
    private byte[] AIKPub;

    //ADDRESS OF THE SECURE PLATFORM
    public PlatformID CALocation;
    public PublicKey CAKey;

    //PERMITS ASSIGNED BY THE AC
    Map<String, SecureCAConfirmation> CAPermissionList = new HashMap<String,SecureCAConfirmation>();



    /**
     * THIS FUNCTION GET THE NAME OF THE ACTUAL SERVICE.
     * @return
     */
    @Override
    public String getName() {
        return SecureAgentTPMHelper.NAME;
    }


    /**
     * THIS FUNCTION INIT THE SERVICE, CALLING THE SUPER METHODS THAT CONTAINS THE BASE SERVICE.
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
     * @param prof
     * @throws ServiceException
     */
    public void boot(Profile prof) throws ServiceException {
        super.boot(prof);
        verbose = prof.getBooleanProperty(VERBOSE, false);
        System.out.println("SECURE AGENT CLOUD TPM SERVICE STARTED CORRECTLY ON CONTAINER CALLED: " +
                           actualcontainer.getID());
    }


    /**
     * THIS FUNCTION RETRIEVE THE SERVICE HELPER.
     * @param agent
     * @return
     * @throws ServiceException
     */
    public ServiceHelper getHelper(Agent agent) throws ServiceException {
        if (agent instanceof SecureAgentPlatform) {
            return thisHelper;
        } else {
            throw new ServiceException("THIS SERVICE IS NOT ALLOWED TO RUN IN THIS AGENT");
        }
    }


    /**
     * FUNCTION TO GET THE SLICE TO EXECUTE HORIZONTAL COMMANDS.
     * @return
     */
    public Class getHorizontalInterface() {
        return SecureAgentTPMSlice.class;
    }


    /**
     * FUNCTION TO GET THE ACTUAL SLICER.
     * @return
     */
    public Slice getLocalSlice() {
        return actualSlicer;
    }


    /**
     * FUNCTION TO GET THE COMMANDS THAT I IMPLEMENTED FOR THIS SERVICE IN THE HELPER.
     * @return
     */
    public String[] getOwnedCommands() {
        return actualCommands;
    }


    /**
     * RETRIEVE THE FILTERS IF IT WAS NECESSARY
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
     * @return
     */
    public Behaviour getAMSBehaviour() {
        System.out.println("THE AGENT CLOUD AMS BEHAVIOUR IS WORKING CORRECTLY");
        AID amsAID = new AID("ams", false);
        Agent ams = actualcontainer.acquireLocalAgent(amsAID);
        MessageTemplate mt =
                MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.MatchAll());
        ResponseAgentACL resp = new ResponseAgentACL(ams, mt, SecureAgentTPMService.this);
        actualcontainer.releaseLocalAgent(amsAID);
        return resp;
    }


    public class SecureAgentTPMServiceHelperImpl implements SecureAgentTPMHelper {

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
         * THIS FUNCTION TRY TO INITIALIZE THE PLATFORM, SO ITS GENERATE A KEY PAIR, AND A SIGNED KEY.
         * TAKES A PUBLIC KEY AND A CA LOCATION, TO COMMUNICATE WITH THE PLATFORM.
         * BECAUSE WE WANT TO CONNECT WITH REMOTE PLATFORMS, THE REQUEST NEEDS TO BE SENT IN THE AMS OF THE PLATFORM.
         * IN ADDITION, IT TAKES AS A PARAMETER THE VALUE OF THE INDEX WHERE THE KEY IS STORED.
         * @param secureAgentPlatform
         */
        @Override
        public synchronized void doStartCloudAgent(SecureAgentPlatform secureAgentPlatform, PlatformID caLocation,
                                                   PublicKey pubKey, String contextEK, String contextAK){
            Agencia.printLog("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED BY THE AGENT: " +
                              secureAgentPlatform.getAID(), Level.INFO, SecureAgentTPMHelper.DEBUG,
                              this.getClass().getName());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                              Level.INFO, true, this.getClass().getName());
            GenericCommand command = new GenericCommand(SecureAgentTPMHelper.REQUEST_START, SecureAgentTPMHelper.NAME,
                                              null);
            RequestSecureATT Generated_Pack = new RequestSecureATT(pubKey,caLocation,contextEK,contextAK);
            command.addParam(Generated_Pack);
            Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS", Level.INFO, true,
                              this.getClass().getName());
            try {
                SecureAgentTPMService.this.submit(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * THIS FUNCTION TRY TO START A MIGRATION WHEN RECEIVE THE CONFIRMATION BY THE SECURE PLATFORM.
         * TAKES AS A PARAM THE LOCATION OF THE PLATFORM WHERE IT NEEDS TO COMMUNICATE.
         * @param secureAgentPlatform
         * @param destiny
         */
        @Override
        public void doStartMigration(SecureAgentPlatform secureAgentPlatform, PlatformID destiny) {
            Agencia.printLog("-> THE PROCCES TO COMMUNICATE WITH THE AMS TO MOVE AN AGENT HAS JUST STARTED " +
                             "NAME AGENT: " + secureAgentPlatform.getAID(), Level.INFO, SecureAgentTPMHelper.DEBUG,
                             this.getClass().getName());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                              Level.INFO, true, this.getClass().getName());
            GenericCommand command = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_PLATFORM,
                                                        SecureAgentTPMHelper.NAME, null);
            Pair<SecureAgentPlatform,PlatformID> migrationRequest = new Pair<SecureAgentPlatform,PlatformID>
                    (secureAgentPlatform,destiny);
            command.addParam(migrationRequest);
            Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS", Level.INFO, true,
                              this.getClass().getName());
            try {
                SecureAgentTPMService.this.submit(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * THE CommandSourceSink TRY TO PROCESS THE VERTICAL COMMANDS THAT I CREATED IN THIS PLATFORM.
     */
    private class CommandSourceSink implements Sink {


        @Override
        public void consume(VerticalCommand command) {
            try{
                String commandName = command.getName();
                SecureAgentTPMSlice obj = (SecureAgentTPMSlice) getSlice(MAIN_SLICE);
                if(commandName.equals(SecureAgentTPMHelper.REQUEST_START)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN " +
                                    "PLATFORM TO START THE AGENT", Level.INFO, SecureAgentTPMHelper.DEBUG,
                                    this.getClass().getName());
                    try{
                        obj.doCommunicateAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING START REQUEST IN THE COMMAND SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureAgentTPMHelper.REQUEST_MIGRATE_PLATFORM)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN " +
                                    "PLATFORM TO SEND THE REQUEST MIGRATE", Level.INFO, SecureAgentTPMHelper.DEBUG,
                                    this.getClass().getName());
                    try{
                        obj.doMigrateAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST MIGRATION IN THE COMMAND SOURCE " +
                                           "SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN " +
                                    "PLATFORM TO ATTESTATE THE ORIGIN", Level.INFO, SecureAgentTPMHelper.DEBUG,
                                    this.getClass().getName());
                    try{
                        obj.doAttestateOrginAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST ORIGIN ATTESTATION IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM)){
                    Agencia.printLog("PROCESSING THE COMMAND TO MIGRATE THE AGENT WITH THE AMS OF THE MAIN " +
                                      "PLATFORM TO ATTESTATE THE DESTINY", Level.INFO, SecureAgentTPMHelper.DEBUG,
                                      this.getClass().getName());
                    try{
                        obj.doMigrateHostpotAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST DESTINY ADDRESS IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureAgentTPMHelper.REQUEST_MOVE)){
                    Agencia.printLog("PROCESSING THE COMMAND TO MOVE THE AGENT", Level.INFO,
                                     SecureAgentTPMHelper.DEBUG, this.getClass().getName());
                    try{
                        obj.doMigrateHostpotAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST ACCEPTED MOVE IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * THE CommandTargetSink TRY TO PROCESS THE VERTICAL COMMANDS THAT IT RECEIVE FROM THE SERVICE COMPONENT.
     */
    private class CommandTargetSink implements Sink {


        @Override
        public void consume(VerticalCommand command) {
            try{
                String CommandName = command.getName();

                if(CommandName.equals(SecureAgentTPMHelper.REQUEST_START)){

                    //IN THIS PART OF THE CODE, THE AMS OF THE ORIGIN PLATFORM SEND A REQUEST TO THE CA FOR VALIDATION
                    //IN THE SECURE PLATFORM

                    RequestSecureATT PairReceive = (RequestSecureATT)command.getParams()[0];
                    CALocation = PairReceive.getPlatformCALocation();
                    CAKey = PairReceive.getPublicPassword();

                    //CREATE THE NEW PACKET THAT THE PLATFORM SEND TO THE SECURE CA
                    RequestSecureATT SecureCAInformation = new RequestSecureATT(CAKey, CALocation);

                    //CREATE KEY PAIR FROM MY PLATFORM AGENT
                    Pair<PrivateKey,PublicKey> pairAgent = Agencia.genKeyPairAgent();

                    //SAVE INTO THE CONTEXT OF THE PLATFORM
                    privKeyAgent=pairAgent.getKey();
                    pubKeyAgent=pairAgent.getValue();

                    //SAVE THE ACTUAL LOCATION OF THE PLATFORM
                    Location actualLocation = actualcontainer.here();

                    //SAVE THE CONTEXT WHERE THE EK AND THE AK ARE SAVED
                    contextEK = PairReceive.getContextEK();
                    contextAK = PairReceive.getContextAK();

                    //GET THE INFORMATION OF THE PLATFORM
                    AID MYams =  actualcontainer.getAMS();
                    PlatformID myPlatform = new PlatformID(MYams);

                    System.out.println("GENERATING THE TEMPORAL DIRECTORY: ");

                    //GENERATE THE PRIVATE AND PUBLIC AIK TO SIGN THE INFORMATION
                    Agencia.init_platform("./"+actualLocation.getName(),contextEK, contextAK);

                    //GENERATE THE SIGNED FILES, AND SERIALIZE INTO AN OBJECT TO SEND IT AFTER TO THE SECURE PLATFORM
                    Agencia.attestation_files("./"+actualLocation.getName(),contextAK,"",true);
                    File AIKFile = new File("./"+actualLocation.getName()+"/akpub.pem");
                    AIKPub = Files.readAllBytes(AIKFile.toPath());
                    AttestationSerialized PCR_Signed = new AttestationSerialized("./"+
                                                                                    actualLocation.getName());

                    RequestSecureATT requestSecureStart = new RequestSecureATT(pubKeyAgent,myPlatform,PCR_Signed);

                    //SEND THE INFORMATION TO THE SECURE PLATFORM
                    AID amsMain = new AID("ams", false);
                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    amsMainPlatform.addBehaviour(
                            new SenderStartRequest(message, requestSecureStart, SecureCAInformation, amsMainPlatform,
                                              SecureAgentTPMService.this)
                    );
                    actualcontainer.releaseLocalAgent(amsMain);

                }else if(CommandName.equals(SecureAgentTPMHelper.REQUEST_MIGRATE_PLATFORM)){

                    //IN THIS PART OF THE CODE, THE AMS OF THE ORIGIN PLATFORM SEND A REQUEST TO THE SECURE PLATFORM
                    //TO START THE REMOTE ATTESTATION PROCESS

                    Pair<SecureAgentPlatform,PlatformID> migratePair = (Pair<SecureAgentPlatform,PlatformID>)
                            command.getParams()[0];
                    PlatformID DestinyPlatform = (PlatformID) migratePair.getValue();
                    SecureAgentPlatform requestAgentPlatform = (SecureAgentPlatform) migratePair.getKey();
                    AID MYams =  actualcontainer.getAMS();
                    PlatformID myPlatform = new PlatformID(MYams);

                    RequestSecureATT PackRequest = new RequestSecureATT(CAKey,CALocation,DestinyPlatform,
                                                                        myPlatform,requestAgentPlatform);

                    AID amsMain = new AID("ams", false);
                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    amsMainPlatform.addBehaviour(
                            new SenderMigrationRequest(message, amsMainPlatform,
                                    SecureAgentTPMService.this, PackRequest)
                    );
                    actualcontainer.releaseLocalAgent(amsMain);

                }else if(CommandName.equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){

                    //IN THIS PART OF THE CODE, THE AMS OF THE ORIGIN PLATFORM RECEIVES A REQUEST FROM THE SECURE
                    // PLATFORM TO SEND THE SIGNED PCR VALUES

                    SecureChallengerPacket pSenderDone = (SecureChallengerPacket) command.getParams()[0];

                    byte [] OTP_Pub = pSenderDone.getOTPPub();
                    byte [] contentPub = pSenderDone.getPartPublic();

                    byte [] decryptedKey = Agencia.decrypt(privKeyAgent,OTP_Pub);

                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length,
                                                              "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(contentPub);
                    String challenge = (String)Agencia.deserialize(byteObject);

                    System.out.println("*************************************************");
                    System.out.println("THE CHALLENGE IS THE FOLLOWING "+challenge);
                    System.out.println("*************************************************");

                    String temPath = "./temp_agent";
                    new File(temPath).mkdir();
                    try (FileOutputStream fos = new FileOutputStream(temPath+"/akpub.pem")) {
                        fos.write(AIKPub);
                    }
                    Agencia.attestation_files(temPath,contextAK,challenge,false);
                    AttestationSerialized PCR_Signed = new AttestationSerialized(temPath);
                    Agencia.deleteFolder(new File(temPath));

                    //CREATE THE NEW PACKET TO SEND IT TO THE SECURE CA PLATFORM
                    SecureChallengerPacket pSender = new SecureChallengerPacket(pSenderDone.getOTPPriv(),
                                                                                  null,null,
                                                                                   pSenderDone.getPartPriv());

                    AID amsMain = new AID("ams", false);
                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                    amsMainPlatform.addBehaviour(
                            new SenderChallengeAgentRequest(message, amsMainPlatform, PCR_Signed,
                                    SecureAgentTPMService.this, CAKey,CALocation,pSender)
                    );
                    actualcontainer.releaseLocalAgent(amsMain);

                }else if(CommandName.equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM)){

                    Pair<byte [],byte []> confirmationPacket = (Pair<byte [],byte []>)command.getParams()[0];
                    byte[] decryptedKey = Agencia.decrypt(privKeyAgent,confirmationPacket.getKey());
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length,
                                                              "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(confirmationPacket.getValue());
                    SecureCAConfirmation packetReceived = (SecureCAConfirmation) Agencia.deserialize(byteObject);

                    //MIGRATE THE AGENT
                    SecureAgentPlatform requestAgent = packetReceived.getAgent();

                    System.out.println("*****************************************************************");
                    System.out.println("THE AGENT IS GOING TO MIGRATE TO THE PLATFORM SELECTED PREVIOUSLY:"+requestAgent);
                    System.out.println("*****************************************************************");


                    try{
                        requestAgent.doMove(packetReceived.getDestinyPlatform());
                    }catch(Exception e){
                        System.out.println("THE REQUESTED AGENT COULD NOT MAKE THE MIGRATION");
                        e.printStackTrace();
                    }
                }else if(CommandName.equals(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE3_PLATFORM)){

                    Pair<byte [],byte []> confirmationPacket = (Pair<byte [],byte []>)command.getParams()[0];
                    byte[] decryptedKey = Agencia.decrypt(privKeyAgent,confirmationPacket.getKey());
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length,
                            "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(confirmationPacket.getValue());
                    SecureCAConfirmation packetReceived = (SecureCAConfirmation) Agencia.deserialize(byteObject);

                    System.out.println("***********************************************************");
                    System.out.println("SAVING THE CONFIRMATION REQUEST: "+packetReceived.getToken());
                    System.out.println("***********************************************************");

                    CAPermissionList.put(packetReceived.getToken(),packetReceived);
                }
            }catch(Exception ex){
                System.out.println("AN ERROR HAPPENED WHEN RUNNING THE SERVICE IN THE COMMAND TARGET SINK");
                ex.printStackTrace();
            }
        }
    }


    /**
     * ServiceComponent PROCESS THE HORIZONTAL COMMANDS THAT THE PLATFORM RECEIVE FROM ANOTHER EXTERNAL PLATFORM AND
     * CONVERTS IT IN VERTICAL COMMANDS.
     */
    private class ServiceComponent implements Slice {


        @Override
        public Service getService() {
            return SecureAgentTPMService.this;
        }


        @Override
        public Node getNode() throws ServiceException {
            try {
                return SecureAgentTPMService.this.getLocalNode();
            } catch (Exception e) {
                throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE SECURE AGENT SERVICE");
            }
        }


        @Override
        public VerticalCommand serve(HorizontalCommand command) {
            GenericCommand commandResponse = null;
            try{
                String commandReceived = command.getName();
                if(commandReceived.equals(SecureAgentTPMSlice.REMOTE_REQUEST_START)) {
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND TO START THE HOST ",
                                    Level.INFO, SecureAgentTPMHelper.DEBUG, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureAgentTPMHelper.REQUEST_START, SecureAgentTPMHelper.NAME,
                                               null);
                    commandResponse.addParam(command.getParams()[0]);
                }else if(commandReceived.equals(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND TO MIGRATE THE HOST ",
                                    Level.INFO, SecureAgentTPMHelper.DEBUG, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_PLATFORM,
                                                         SecureAgentTPMHelper.NAME, null);
                    commandResponse.addParam(command.getParams()[0]);
                }else if(commandReceived.equals(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND ATTESTATE THE ORIGIN PLATFORM",
                                    Level.INFO, SecureAgentTPMHelper.DEBUG, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                                                         SecureAgentTPMHelper.NAME, null);
                    commandResponse.addParam(command.getParams()[0]);
                }else if(commandReceived.equals(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE2_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL CONFIRMATION COMMAND IN THE ORIGIN" +
                                    " PLATFORM", Level.INFO, SecureAgentTPMHelper.DEBUG, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM,
                                                         SecureAgentTPMHelper.NAME, null);
                    commandResponse.addParam(command.getParams()[0]);
                }else if(commandReceived.equals(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE3_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL CONFIRMATION COMMAND IN THE DESTINY" +
                                    " PLATFORM", Level.INFO, SecureAgentTPMHelper.DEBUG, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureAgentTPMHelper.REQUEST_MIGRATE_ZONE3_PLATFORM,
                            SecureAgentTPMHelper.NAME, null);
                    commandResponse.addParam(command.getParams()[0]);
                }
            }catch(Exception e){
                System.out.println("AN ERROR HAPPENED WHILE THE VERTICAL COMMAND WAS PROCESSED IN THE SERVER SERVICE " +
                                   "COMPONENT");
                e.printStackTrace();
            }
            return commandResponse;
        }
    }
}
