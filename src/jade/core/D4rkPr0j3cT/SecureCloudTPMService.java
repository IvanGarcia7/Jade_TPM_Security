package jade.core.D4rkPr0j3cT;

import jade.core.*;
import jade.core.CloudAgents.*;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.core.SecureTPM.SecureAgent;
import jade.core.behaviours.Behaviour;
import jade.core.mobility.Movable;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.*;
import java.util.logging.Level;

public class SecureCloudTPMService extends BaseService {

    //INTERESTING VARIABLES.
    public static final String NAME = "jade.core.D4rkPr0j3cT.SecureCloudTPM";
    public static final String VERBOSE = "jade_core_D4rkPr0j3cT_SecureCloudTPMService_verbose";

    //TIME VAR THAT THE AGENCY USE TO SEE THE TIME THAT A REQUEST IT'S TAKEN.
    long startTime = System.nanoTime();

    //DEFINE THE SLICER AND THE HELPER TO EXECUTE CORRECTLY THE SERVICE.
    private Slice actualSlicer = new SecureCloudTPMService.ServiceComponent();
    private ServiceHelper thisHelper = new SecureCloudTPMService.SecureCloudTPMServiceHelperImpl();

    //DEFINE INPUT AND OUTPUT FILTERS.
    private Filter OutFilter = null;
    private Filter InFilter = null;

    //DEFINE INPUT AND OUTPUT SINKS.
    private Sink OutputSink = new SecureCloudTPMService.CommandSourceSink();
    private Sink InputSink = new SecureCloudTPMService.CommandTargetSink();

    //REQUEST THE HELPERIMPL
    private SecureCloudTPMServiceHelperImpl myServiceHelperImpl = new SecureCloudTPMServiceHelperImpl();

    //REQUEST THE COMMANDS THAT I HAVE IMPLEMENTED AND SAVE INTO A LIST.
    private String[] actualCommands = new String[]{
            SecureCloudTPMHelper.REQUEST_START,
            SecureCloudTPMHelper.REQUEST_LIST,
            SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM,
            SecureCloudTPMHelper.REQUEST_ACCEPT_PLATFORM,
            SecureCloudTPMHelper.REQUEST_PACK_PLATFORM,
            SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM,
            SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
            SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM,
            SecureCloudTPMHelper.REQUEST_ERROR
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
    private PrivateKey privateKeyCA;
    private PublicKey publicKeyCA;

    //DICT OF THE HOSTPOTS
    Map<String,SecureInformationCloud> HostpotsRegister = new HashMap<String,SecureInformationCloud>();

    //DICT TO REGISTER THE PLATFORMS TO CONFIRM
    Map<String, SecureInformationCloud> pendingRedirects = new HashMap<String, SecureInformationCloud>();

    /**
     * THIS FUNCTION GET THE NAME OF THE ACTUAL SERVICE.
     *
     * @return
     */
    @Override
    public String getName() {
        return SecureCloudTPMHelper.NAME;
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
        if (agent instanceof SecureCAPlatform) {
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
        return SecureCloudTPMSlice.class;
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
     * Retrieve the HelperImpl
     */
    public SecureCloudTPMServiceHelperImpl getHelperImpl(){
        return myServiceHelperImpl;
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
                        MessageTemplate.MatchAll());
        ResponserCloudACL resp = new ResponserCloudACL(ams, mt, SecureCloudTPMService.this);
        actualcontainer.releaseLocalAgent(amsAID);
        return resp;
    }

    public class SecureCloudTPMServiceHelperImpl implements SecureCloudTPMHelper {

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
         * @param secureCAPlatform
         */
        public synchronized void doStartCloud(SecureCAPlatform secureCAPlatform,PrivateKey priv, PublicKey pub) {
            StringBuilder sb = new StringBuilder();
            sb.append("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED NAME AGENT:")
                     .append(secureCAPlatform.getAID());
            System.out.println(sb.toString());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                              Level.INFO, true, this.getClass().getName());
            System.out.println("CREATE A NEW VERTICAL COMMAND TO PERFORM THE OPERATION THAT " +
                    "THE SERVICE NEED ");
            GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_START,
                                                        SecureCloudTPMHelper.NAME, null);
            Pair<PrivateKey,PublicKey> keypairgenerate = new Pair<PrivateKey,PublicKey>(priv,pub);
            command.addParam(keypairgenerate);
            try{
                Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS",
                        Level.INFO, true, this.getClass().getName());
                try {
                    System.out.println("-> THE VERTICAL COMMAND TO COMMUNICATE IS CORRECTLY SUBMITED");
                    SecureCloudTPMService.this.submit(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch(Exception e){
                System.out.println("THERE ARE AN ERROR STARTING THE CA");
                e.printStackTrace();
            }

        }

        @Override
        public synchronized void listPlatforms(SecureCAPlatform secureCAPlatform) {
            StringBuilder sb = new StringBuilder();
            sb.append("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED NAME AGENT:")
                    .append(secureCAPlatform.getAID());
            System.out.println(sb.toString());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                    Level.INFO, true, this.getClass().getName());
            System.out.println("CREATE A NEW VERTICAL COMMAND TO PERFORM THE OPERATION THAT " +
                    "THE SERVICE NEED ");
            GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_LIST,
                    SecureCloudTPMHelper.NAME, null);
            Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS",
                    Level.INFO, true, this.getClass().getName());
            try {
                System.out.println("-> THE VERTICAL COMMAND TO COMMUNICATE IS CORRECTLY SUBMITED");
                SecureCloudTPMService.this.submit(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void doAcceptCloud(SecureCAPlatform secureCAPlatform, byte[] index) {
            System.out.println("HELLO MY FRIEND");
        }
    }

    private class CommandSourceSink implements Sink {

        @Override
        public void consume(VerticalCommand command) {
            try{
                String commandName = command.getName();
                if(commandName.equals(SecureCloudTPMHelper.REQUEST_START)){
                    System.out.println("PROCEED THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM TO " +
                            "START THE HOTSPOTS");
                    SecureCloudTPMSlice obj = (SecureCloudTPMSlice) getSlice(MAIN_SLICE);
                    try{
                        obj.doCommunicateAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST ADDRESS IN THE COMMAND SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_LIST)){
                    System.out.println("PROCEED THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM TO " +
                            "PRINT THE LIST OF THE HOTSPOTS");
                    SecureCloudTPMSlice obj = (SecureCloudTPMSlice) getSlice(MAIN_SLICE);
                    try{
                        obj.doRequestListAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST LIST ADDRESS IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM)){
                    System.out.println("PROCEED THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM TO " +
                            "REGISTER ONE HOSTPOT");
                    SecureCloudTPMSlice obj = (SecureCloudTPMSlice) getSlice(MAIN_SLICE);
                    try{
                        obj.doInsertHostpotAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST LIST ADDRESS IN THE COMMAND " +
                                "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM)){
                    System.out.println("PROCEED THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM TO " +
                            "INITIALIZE THE ATTESTATION PROCESS");
                    SecureCloudTPMSlice obj = (SecureCloudTPMSlice) getSlice(MAIN_SLICE);
                    try{
                        obj.doStartAttestationHostpotAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST LIST ADDRESS IN THE COMMAND " +
                                "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    System.out.println("PROCEED THE COMMAND TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM TO " +
                            "CHECK THE ATT FILES ORIGIN PLATFORM THE ATTESTATION PROCESS");
                    SecureCloudTPMSlice obj = (SecureCloudTPMSlice) getSlice(MAIN_SLICE);
                    try{
                        obj.doCheckAttestationHostpotoRIGIN(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST LIST ADDRESS IN THE COMMAND " +
                                "SOURCE SINK");
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
                if(CommandName.equals(SecureCloudTPMHelper.REQUEST_START)){
                    System.out.println("PROCESSING THE VERTICAL COMMAND START CLOUD REQUEST INTO THE " +
                                       "AMS DESTINATION CONTAINER");
                    Pair<PrivateKey,PublicKey> keyPairReceive = (Pair<PrivateKey, PublicKey>)command.getParams()[0];
                    System.out.println("INITIALIZING THE KEY PAIR RECEIVE");
                    privateKeyCA = keyPairReceive.getKey();
                    publicKeyCA  = keyPairReceive.getValue();
                    System.out.println("I'M IN THE AMS MAIN CONTAINER, AND THE KEY PAIR IS THE FOLLOWING: ");
                    System.out.println("NAME OF THE CONAINER: "+actualcontainer.getID().getName());
                    System.out.println("*********************SECRET*****************************");
                    System.out.println("PUBLIC KEY: "+publicKeyCA);
                    System.out.println("PRIVATE KEY: "+privateKeyCA);
                    System.out.println("*********************SECRET*****************************");
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_LIST)){
                    System.out.println("PROCESSING THE VERTICAL COMMAND LIST CLOUD REQUEST INTO THE " +
                            "AMS DESTINATION CONTAINER");
                    System.out.println("NAME OF THE REGISTER HOSTPOTS IN: "+actualcontainer.getID().getName());
                    System.out.println("*********************HOSTPOTS*****************************");
                    Iterator it = HostpotsRegister.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry pair = (Map.Entry)it.next();
                        System.out.println(pair.getKey() + " = " + pair.getValue());
                        it.remove();
                    }
                    System.out.println("*********************HOSTPOTS*****************************");
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM)){
                    RequestSecure packSecure = (RequestSecure) command.getParams()[0];
                    KeyPairCloudPlatform pack = packSecure.getPacketAgent();
                    System.out.println("*********************NEW REQUEST*****************************");
                    System.out.println("LOCATION -> "+pack.getLocationPlatform());
                    System.out.println("PUBLIC PASSWORD -> "+pack.getPublicPassword());
                    System.out.println("*********************NEW REQUEST*****************************");
                    if(HostpotsRegister.get(pack.getLocationPlatform())!=null){
                        System.out.println("THE PLATFORM IS ALREADY INCLUDED WITHIN THE CONFIRMED LIST");
                    }else if(HostpotsRegister.get(pack.getLocationPlatform())!=null){
                        System.out.println("THE PLATFORM IS ALREADY INCLUDED WITHIN THE PENDING LIST");
                    }else{
                        System.out.println("PCRS LIST:");
                        String temPath = "./temp";
                        new File(temPath).mkdir();
                        System.out.println("DESERIALIZE THEIR INFORMATION");
                        AttestationSerialized packetReceive = packSecure.getPacket_signed();
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/akpub.pem")) {
                            stream.write(packetReceive.getAIKPub());
                        }
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/sign.out")) {
                            stream.write(packetReceive.getSign());
                        }
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/pcr.out")) {
                            stream.write(packetReceive.getMessage());
                        }
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/quote.out")) {
                            stream.write(packetReceive.getQuoted());
                        }
                        int result = Agencia.check_attestation_files(temPath,"",true);
                        if(result==0){
                            System.out.println("DO YOU WANT TO VALIDATE IT NOW Y/N?");
                            Scanner sc = new Scanner(System.in);
                            String response = sc.nextLine();
                            pack = packSecure.getPacketAgent();
                            Iterator it = null;
                            //CHECK IF THE PLATFORM IS NOT IN THE REQUEST OR VALIDATE HOSTPOTS
                            System.out.println("COMPUTING THE HASH");
                            String hash = Agencia.computeSHA256(temPath+"/pcr.out");
                            SecureInformationCloud saveRequest = new SecureInformationCloud(pack.getPublicPassword(),hash,packetReceive.getAIKPub(),pack.getMyPlatform());
                            Agencia.deleteFolder(new File(temPath));
                            Pair accepted = new Pair(pack.getPublicPassword(),hash);
                            if(response.toUpperCase().equals("Y")){
                                System.out.println("ADDING THE REQUEST IN THE CONFIRM LIST");
                                HostpotsRegister.put(pack.getLocationPlatform().getID(),saveRequest);
                                System.out.println("PLATFORM INSERTED IN THE CORRECTLY ACCEPTED LIST "+HostpotsRegister.size());
                                it = HostpotsRegister.entrySet().iterator();
                            }else {
                                System.out.println("ADDING THE REQUEST IN THE PREVIOUS LIST");
                                pendingRedirects.put(pack.getLocationPlatform().getID(),saveRequest);
                                it = pendingRedirects.entrySet().iterator();
                                System.out.println("PLATFORM INSERTED IN THE CORRECTLY PENDING LIST");
                            }
                            System.out.println("*********************HOSTPOTS*****************************");
                            while(it.hasNext()){
                                Map.Entry pair = (Map.Entry)it.next();
                                SecureInformationCloud iteration = (SecureInformationCloud)pair.getValue();
                                System.out.println(pair.getKey() + " = " + iteration.getSha256());
                            }
                            System.out.println("*********************HOSTPOTS*****************************");
                        }else{
                            System.out.println("ERROR READING THE ATTESTATION DATA.");
                        }

                    }
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM)){
                    /**
                     * AT THIS POINT, I AM IN THE AMS OF THE SECURE PLATFORM, AND IN THE PACKET, I
                     * HAVE ONE DESTINY, SO FIRST I NEED TO VIEW IF THE PLATFORM ARE SAVE IN THE REPO,
                     * THEM, CREATE A CHALLENGUE AND SEND IT, THEN VERIFIE THE CHALLENGUE, CREATE A NEW CHALLENGUE
                     * TO THE SECODN PLATFORM, ATTESTATE THE INFORMATION, COMPARE, AND START OR DENIED THE PROCESS
                     *
                     * IF ONE INFORMATION IS NOT VALID, MOVE THE ACEPTED TO PENDIENTS AND CREARE A COUNTER
                     * TO EVIT REPETITION ATTACKS
                     */

                    KeyPairCloudPlatform packetReceived = (KeyPairCloudPlatform)command.getParams()[0];
                    Location originPlatform = packetReceived.getMyLocation();
                    Location destiny = packetReceived.getLocationDestiny();

                    if(HostpotsRegister.containsKey(originPlatform.getID()) && HostpotsRegister.containsKey(packetReceived.getLocationDestiny().getID())){
                        System.out.println("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGUE");
                        Location newDestiny = HostpotsRegister.get(packetReceived.getMyLocation().getID()).getPlatformLocation();
                        System.out.println(newDestiny);
                        String challengue = Agencia.getRandomChallengue();
                        System.out.println("THE CHALLENGUE IS THE FOLLOWING "+challengue);
                        //Send the challengue to the origin platform to attestate
                        AID amsMain = new AID("ams", false);
                        PublicKey destinypub = HostpotsRegister.get(originPlatform.getID()).getKeyPub();
                        PlatformID destinyPT = HostpotsRegister.get(packetReceived.getMyLocation().getID()).getPlatformLocation();
                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                        Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                        amsMainPlatform.addBehaviour(
                                new SenderACLChallengue(message, amsMainPlatform,
                                        SecureCloudTPMService.this,originPlatform,newDestiny ,challengue,SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,destinypub,publicKeyCA,0,destinyPT)
                        );
                        actualcontainer.releaseLocalAgent(amsMain);
                    }else{
                        System.out.println("REJECTED REQUEST, PLATFORM IS NOT FOUND WITHIN THE ACCEPTED DESTINATIONS DIRECTORY");
                    }
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    if(command.getParams()[0]!=null){
                        //I need to check the data
                        Pair<AttestationSerialized, SecretInformation> sendAtt = (Pair<AttestationSerialized, SecretInformation>)command.getParams()[0];
                        SecretInformation newSecretDecrypt = sendAtt.getValue();

                        Location origin = newSecretDecrypt.getOrigin();
                        Location destiny = newSecretDecrypt.getDestiny();

                        String temPath = "./temp";
                        new File(temPath).mkdir();
                        System.out.println("DESERIALIZE THEIR INFORMATION");
                        AttestationSerialized packetReceive = sendAtt.getKey();
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/akpub.pem")) {
                            stream.write(HostpotsRegister.get(origin).getAIK());
                        }
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/sign.out")) {
                            stream.write(packetReceive.getSign());
                        }
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/pcr.out")) {
                            stream.write(packetReceive.getMessage());
                        }
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/quote.out")) {
                            stream.write(packetReceive.getQuoted());
                        }
                        int result = Agencia.check_attestation_files(temPath,newSecretDecrypt.getChallengue(),false);
                        if(result==0) {
                            System.out.println("COMPUTING THE HASH");
                            String hash = Agencia.computeSHA256(temPath + "/pcr.out");
                            System.out.println("CHEKING TH SAH256");
                            String hashSaved = HostpotsRegister.get(origin).getSha256();
                            if(!hashSaved.equals(hash)){
                                System.out.println("THE PLATFORM IS CORRUPTED BY A MALWARE");
                                AID amsMain = new AID("ams", false);
                                Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                                PublicKey destinypub = HostpotsRegister.get(origin).getKeyPub();
                                String ms = "THE PLATFORM IS CORRUPTED BY A MALWARE";
                                amsMainPlatform.addBehaviour(
                                        new SenderACLChallengueError(message, amsMainPlatform,
                                                SecureCloudTPMHelper.REQUEST_ERROR,destinypub,ms,origin)
                                );
                                actualcontainer.releaseLocalAgent(amsMain);
                                //REMOVE
                                SecureInformationCloud malware = HostpotsRegister.get(origin);
                                pendingRedirects.put(origin.getID(),malware);
                                HostpotsRegister.remove(origin);
                            }else{

                                if(newSecretDecrypt.getValidation()==1){
                                    System.out.println("BOTH PLATFORMS CONFIRMED");
                                    AID amsMain = new AID("ams", false);
                                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                                    PublicKey destinypub = HostpotsRegister.get(origin).getKeyPub();
                                    PublicKey destinyremotepub = HostpotsRegister.get(destiny).getKeyPub();
                                    amsMainPlatform.addBehaviour(
                                            new SenderACLConfirmation(message, amsMainPlatform,
                                                    origin,destiny ,SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM,destinypub,destinyremotepub,privateKeyCA)
                                    );
                                    actualcontainer.releaseLocalAgent(amsMain);
                                }else{
                                    //SEND ATT REQUEST TO THE SECONF PLATFROM
                                    System.out.println("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGUE");
                                    String challengue = Agencia.getRandomChallengue();
                                    //Send the challengue to the origin platform to attestate
                                    AID amsMain = new AID("ams", false);
                                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                                    PublicKey destinypub = HostpotsRegister.get(destiny).getKeyPub();
                                    PlatformID destinyPT = HostpotsRegister.get(destiny).getPlatformLocation();
                                    amsMainPlatform.addBehaviour(
                                            new SenderACLChallengue(message, amsMainPlatform,
                                                    SecureCloudTPMService.this,origin,destiny ,challengue,SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,destinypub,publicKeyCA,1,destinyPT)
                                    );
                                    actualcontainer.releaseLocalAgent(amsMain);
                                }
                            }
                        }else{
                            System.out.println("ERROR READING THE INFORMATION, IGNORE THE MESSAGE");
                            AID amsMain = new AID("ams", false);
                            Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                            PublicKey destinypub = HostpotsRegister.get(origin).getKeyPub();
                            String ms = "ERROR READING THE INFORMATION, IGNORE THE MESSAGE";
                            amsMainPlatform.addBehaviour(
                                    new SenderACLChallengueError(message, amsMainPlatform,
                                            SecureCloudTPMHelper.REQUEST_ERROR,destinypub,ms,origin)
                            );
                            actualcontainer.releaseLocalAgent(amsMain);
                        }
                    }else{
                        System.out.println("ERROR TIMEOUT IGNORIGN ");
                    }
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
            return SecureCloudTPMService.this;
        }

        @Override
        public Node getNode() throws ServiceException {
            try {
                return SecureCloudTPMService.this.getLocalNode();
            } catch (Exception e) {
                throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE CLOUD SERVICE COMPONENT");
            }
        }

        @Override
        public VerticalCommand serve(HorizontalCommand command) {
            GenericCommand commandResponse = null;
            try{
                String commandReceived = command.getName();
                if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_START)) {
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE COMPONENT " +
                                       "TO START THE HOST");
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_START,
                            SecureCloudTPMHelper.NAME, null);
                    commandResponse.addParam(command.getParams()[0]);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_LIST)) {
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE COMPONENT " +
                            "TO REQUEST THE LIST OF THE HOST");
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_LIST,
                            SecureCloudTPMHelper.NAME, null);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_INSERT_PLATFORM)) {
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE COMPONENT " +
                            "TO INSERT A NEW HOSTPOT IN THE SECURE PLATFORM");
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM,
                            SecureCloudTPMHelper.NAME, null);
                    Pair<byte [],byte []> pairsender = (Pair<byte [],byte []>)command.getParams()[0];
                    byte [] key = pairsender.getKey();
                    byte [] object = pairsender.getValue();
                    byte[] decryptedKey = Agencia.decrypt(privateKeyCA,key);
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length, "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(object);
                    RequestSecure pack = (RequestSecure) Agencia.deserialize(byteObject);
                    commandResponse.addParam(pack);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM)){
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE COMPONENT " +
                            "TO MIGRATE A NEW HOSTPOT IN THE SECURE PLATFORM");
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM,
                            SecureCloudTPMHelper.NAME, null);
                    Pair<byte [],byte []> pairsender = (Pair<byte [],byte []>)command.getParams()[0];
                    byte [] key = pairsender.getKey();
                    byte [] object = pairsender.getValue();
                    byte[] decryptedKey = Agencia.decrypt(privateKeyCA,key);
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length, "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(object);
                    KeyPairCloudPlatform packetReceived = (KeyPairCloudPlatform) Agencia.deserialize(byteObject);
                    commandResponse.addParam(packetReceived);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE COMPONENT " +
                            "TO CHECK ORIGIN A NEW HOSTPOT IN THE SECURE PLATFORM");
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                            SecureCloudTPMHelper.NAME, null);
                    Pair<byte [],byte []> pairsender = (Pair<byte [],byte []>)command.getParams()[0];
                    byte [] key = pairsender.getKey();
                    byte [] object = pairsender.getValue();
                    byte[] decryptedKey = Agencia.decrypt(privateKeyCA,key);
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length, "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(object);
                    Pair<AttestationSerialized ,byte []> packetComplete = (Pair<AttestationSerialized ,byte []>)Agencia.deserialize(byteObject);
                    byte [] SecretInformation = packetComplete.getValue();
                    byte [] decriptInformation = Agencia.decrypt(privateKeyCA,SecretInformation);
                    SecretInformation newSecretDecrypt = (SecretInformation) Agencia.deserialize(decriptInformation);
                    if(((Long)command.getParams()[1]-newSecretDecrypt.getTimestamp())<=Agencia.getTimeout()){
                        System.out.println("Checking the values");
                        AttestationSerialized checkAtt = packetComplete.getKey();
                        Pair<AttestationSerialized, SecretInformation> sendAtt = new Pair<AttestationSerialized, SecretInformation>(checkAtt,newSecretDecrypt);
                        commandResponse.addParam(sendAtt);
                    }else{
                        System.out.println("THE AGENCIA IS TIMEOUT");
                        commandResponse.addParam(null);
                    }
                }
            }catch(Exception e){
                System.out.println("AN ERROR HAPPENED WHEN PROCESS THE VERTICAL COMMAND IN THE SERVICECOMPONENT");
                e.printStackTrace();
            }
            return commandResponse;
        }
    }

}
