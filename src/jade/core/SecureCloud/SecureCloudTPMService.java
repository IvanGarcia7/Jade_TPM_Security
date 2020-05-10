package jade.core.SecureCloud;

import jade.core.*;
import jade.core.SecureAgent.*;
import jade.core.SecureTPM.Agencia;
import jade.core.SecureTPM.Pair;
import jade.core.behaviours.Behaviour;
import jade.core.mobility.Movable;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.security.*;
import java.util.*;
import java.util.logging.Level;

public class SecureCloudTPMService extends BaseService {

    public static final String NAME = "jade.core.SecureCloud.SecureCloudTPM";
    public static final String VERBOSE = "SecureCloud";

    //TIMESTAMP TO INSERT INTO A CHALLENGE PACKET
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

    //REQUEST THE HELPER IMPL
    private SecureCloudTPMServiceHelperImpl myServiceHelperImpl = new SecureCloudTPMServiceHelperImpl();

    //REQUEST THE COMMANDS THAT I HAVE IMPLEMENTED AND SAVE INTO A LIST.
    private String[] actualCommands = new String[]{
            SecureCloudTPMHelper.REQUEST_START,
            SecureCloudTPMHelper.REQUEST_LIST,
            SecureCloudTPMHelper.REQUEST_ACCEPT,
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

    //SECURE CLOUD KEY PAIR FROM A SECURE PLATFORM;
    private PrivateKey privateKeyCA;
    private PublicKey publicKeyCA;

    //HASHMAP OF ACCEPTED HOTSPOTS
    Map<String,SecureInformationCloud> HotspotsRegister = new HashMap<String,SecureInformationCloud>();

    //HASHMAP TO REGISTER THE PLATFORMS STILL TO BE CONFIRMED
    Map<String, SecureInformationCloud> pendingRedirects = new HashMap<String, SecureInformationCloud>();


    /**
     * THIS FUNCTION GET THE NAME OF THE ACTUAL SERVICE.
     * @return
     */
    @Override
    public String getName() {
        return SecureCloudTPMHelper.NAME;
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
        System.out.println("SECURE CLOUD TPM SERVICE STARTED CORRECTLY ON CONTAINER CALLED: " +
                           actualcontainer.getID());

    }


    /**
     * THIS FUNCTION RETRIEVE THE SERVICE HELPER.
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
     * @return
     */
    public Class getHorizontalInterface() {
        return SecureCloudTPMSlice.class;
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
        System.out.println("THE SECURE CLOUD AMS BEHAVIOUR IS WORKING CORRECTLY");
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
         * THIS FUNCTION TRY TO INITIALIZE THE SECURE PLATFORM, ACCORDING TO A KEY PAIR INTRODUCED AS
         * PARAMETERS IN ORDER TO SIMULATE IT. FIRST OF ALL, I NEED TO CONTACT WITH THE AMS OF THE MAIN
         * PLATFORM IN MY ENVIRONMENT.
         * @param secureCAPlatform
         */
        public synchronized void doStartCloud(SecureCAPlatform secureCAPlatform,PrivateKey priv, PublicKey pub) {
            Agencia.printLog("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED NAME AGENT:" +
                             secureCAPlatform.getAID(), Level.INFO, SecureCloudTPMHelper.DEBUG,
                             this.getClass().getName());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                             Level.INFO, true, this.getClass().getName());
            GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_START, SecureCloudTPMHelper.NAME,
                                                        null);
            Pair<PrivateKey,PublicKey> KeyPairCA = new Pair<PrivateKey,PublicKey>(priv,pub);
            command.addParam(KeyPairCA);
            try{
                Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS", Level.INFO,
                                 true, this.getClass().getName());
                SecureCloudTPMService.this.submit(command);
            }catch(Exception e){
                System.out.println("THERE ARE AN ERROR STARTING THE CA");
                e.printStackTrace();
            }
        }


        /**
         * THIS FUNCTION PRINT THE LIST OF HOTSPOTS REGISTER IN THE SECURE PLATFORM. SON TO RETRIEVE THE LIST,
         * SEND A VERTICAL COMMAND TO CONTACT WITH THE AMS.
         * @param secureCAPlatform
         */
        @Override
        public synchronized void listPlatforms(SecureCAPlatform secureCAPlatform) {
            Agencia.printLog("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED IN ORDER TO RETRIEVE" +
                            "THE LIST OF SECURE PLATFORMS. NAME AGENT:" +
                            secureCAPlatform.getAID(), Level.INFO, SecureCloudTPMHelper.DEBUG,
                            this.getClass().getName());
            Agencia.printLog("START THE LIST SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                             Level.INFO, true, this.getClass().getName());
            GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_LIST, SecureCloudTPMHelper.NAME,
                                                        null);
            Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS TO RETRIEVE THE LIST OF HOTSPOTS " +
                            "REGISTERED", Level.INFO, true, this.getClass().getName());
            try {
                SecureCloudTPMService.this.submit(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * THIS FUNCTION, TAKES AN INDEX, IN ORDER TO ESTABLISHED A DEVICE AS SECURE, BASED ON THE LIST OF HOTSPOTS
         * @param secureCAPlatform
         * @param index
         */
        public void doAcceptCloud(SecureCAPlatform secureCAPlatform, String index) {
            Agencia.printLog("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED IN ORDER TO ACCEPT "+
                            "ONE AGENT. NAME AGENT:" + secureCAPlatform.getAID(), Level.INFO,
                            SecureCloudTPMHelper.DEBUG, this.getClass().getName());
            Agencia.printLog("START THE ACCEPT SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                    Level.INFO, true, this.getClass().getName());
            GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_ACCEPT, SecureCloudTPMHelper.NAME,
                                                        null);
            command.addParam(index);
            Agencia.printLog("AGENT REQUEST COMMUNICATE WITH THE AMS TO ACCEPT ONE PLATFORM IN THE LIST OF " +
                    "HOTSPOTS REGISTERED", Level.INFO, true, this.getClass().getName());
            try {
                SecureCloudTPMService.this.submit(command);
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
                SecureCloudTPMSlice obj = (SecureCloudTPMSlice) getSlice(MAIN_SLICE);
                if(commandName.equals(SecureCloudTPMHelper.REQUEST_START)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS AND START THE " +
                                    "PLATFORM IN THE SECURE CA", Level.INFO, true, this.getClass().getName());
                    try{
                        obj.doCommunicateAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING START COMMAND IN THE COMMAND SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_LIST)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS AND PRINT THE LIST " +
                                    "OF THE HOTSPOTS", Level.INFO, true, this.getClass().getName());
                    try{
                        obj.doRequestListAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST LIST ADDRESS IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_ACCEPT)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS AND ACCEPT ONE OF " +
                                    "THE LIST OF THE HOTSPOTS", Level.INFO, true, this.getClass().getName());
                    try{
                        obj.doRequestAcceptAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST LIST ADDRESS IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                } else if(commandName.equals(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS TO REGISTER ONE " +
                                    "HOTSPOT", Level.INFO, true, this.getClass().getName());
                    try{
                        obj.doInsertHostpotAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST INSERT PLATFORM IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS TO INITIALIZE THE " +
                                    "ATTESTATION PROCESS", Level.INFO, true, this.getClass().getName());
                    try{
                        obj.doStartAttestationHostpotAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST MIGRATE PLATFORM IN THE COMMAND " +
                                           "SOURCE SINK");
                        ie.printStackTrace();
                    }
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS TO CHECK THE " +
                                    "ATT FILES ORIGIN PLATFORM THE ATTESTATION PROCESS", Level.INFO, true,
                                    this.getClass().getName());
                    try{
                        obj.doCheckAttestationHostpotoRIGIN(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST MIGRATE ZONE 1 PLATFORM IN THE " +
                                "COMMAND SOURCE SINK");
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
                    Iterator it = HotspotsRegister.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry pair = (Map.Entry)it.next();
                        System.out.println(pair.getKey() + " = " + pair.getValue());

                    }
                    System.out.println("*********************HOSTPOTS*****************************");
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_ACCEPT)){
                    String index = (String) command.getParams()[0];

                    System.out.println("PROCESSING THE VERTICAL COMMAND LIST CLOUD REQUEST INTO THE " +
                            "AMS DESTINATION CONTAINER");
                    System.out.println("NAME OF THE REGISTER HOSTPOTS IN: "+actualcontainer.getID().getName());
                    System.out.println("*********************HOSTPOTS*****************************");
                    Iterator it = HotspotsRegister.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry pair = (Map.Entry)it.next();
                        System.out.println(pair.getKey() + " = " + pair.getValue());
                    }
                    System.out.println("*********************HOSTPOTS*****************************");

                    try{
                        HotspotsRegister.put(index,pendingRedirects.get(index));
                    }catch(Exception e){
                        System.out.println("THE REQUEST PLATFORM IS NOT REGISTER IN THE PENDING LIST");
                        e.printStackTrace();
                    }


                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM)){



                    RequestSecureATT packSecure = (RequestSecureATT) command.getParams()[0];

                    System.out.println("*********************NEW REQUEST*****************************");
                    System.out.println("LOCATION -> "+packSecure.getLocationPlatform());
                    System.out.println("PUBLIC PASSWORD -> "+packSecure.getPublicPassword());
                    System.out.println("*********************NEW REQUEST*****************************");
                    if(HotspotsRegister.get(packSecure.getLocationPlatform())!=null){
                        System.out.println("THE PLATFORM IS ALREADY INCLUDED WITHIN THE CONFIRMED LIST");
                    }else if(HotspotsRegister.get(packSecure.getLocationPlatform())!=null){
                        System.out.println("THE PLATFORM IS ALREADY INCLUDED WITHIN THE PENDING LIST");
                    }else{
                        System.out.println("PCRS LIST:");
                        String temPath = "./temp";
                        new File(temPath).mkdir();
                        System.out.println("DESERIALIZE THEIR INFORMATION");
                        AttestationSerialized packetReceive = packSecure.getPCR_Signed();
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
                            Iterator it = null;
                            //CHECK IF THE PLATFORM IS NOT IN THE REQUEST OR VALIDATE HOSTPOTS
                            System.out.println("COMPUTING THE HASH");
                            String hash = Agencia.computeSHA256(temPath+"/pcr.out");
                            //SecureInformationCloud saveRequest = new SecureInformationCloud(packSecure.getPublicPassword(),hash,packetReceive.getAIKPub(),packSecure.getMyPlatform());

                            //BAD

                            SecureInformationCloud saveRequest = new SecureInformationCloud(packSecure.getPublicPassword(),hash,packetReceive.getAIKPub(),packSecure.getPlatformCALocation());
                            Agencia.deleteFolder(new File(temPath));
                            Pair accepted = new Pair(packSecure.getPublicPassword(),hash);
                            if(response.toUpperCase().equals("Y")){
                                System.out.println("ADDING THE REQUEST IN THE CONFIRM LIST");
                                HotspotsRegister.put(packSecure.getLocationPlatform().getID(),saveRequest);
                                System.out.println("PLATFORM INSERTED IN THE CORRECTLY ACCEPTED LIST "+HotspotsRegister.size());
                                it = HotspotsRegister.entrySet().iterator();
                            }else {
                                System.out.println("ADDING THE REQUEST IN THE PREVIOUS LIST");
                                pendingRedirects.put(packSecure.getLocationPlatform().getID(),saveRequest);
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

                    RequestSecureATT packetReceived = (RequestSecureATT)command.getParams()[0];
                    Location originPlatform = packetReceived.getMyLocation();
                    Location destiny = packetReceived.getLocationDestiny();

                    if(HotspotsRegister.containsKey(originPlatform.getID()) && HotspotsRegister.containsKey(packetReceived.getLocationDestiny().getID())){
                        System.out.println("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGUE");
                        Location newDestiny = HotspotsRegister.get(packetReceived.getMyLocation().getID()).getPlatformLocation();
                        System.out.println(newDestiny);
                        String challengue = Agencia.getRandomChallengue();
                        System.out.println("THE CHALLENGUE IS THE FOLLOWING "+challengue);
                        //Send the challengue to the origin platform to attestate
                        AID amsMain = new AID("ams", false);
                        PublicKey destinypub = HotspotsRegister.get(originPlatform.getID()).getKeyPub();
                        PlatformID destinyPT = HotspotsRegister.get(packetReceived.getMyLocation().getID()).getPlatformLocation();
                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                        Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                        amsMainPlatform.addBehaviour(
                                new SenderACLChallengue(message, amsMainPlatform,
                                        SecureCloudTPMService.this,originPlatform,destiny ,challengue,SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,destinypub,publicKeyCA,0,destinyPT)
                        );
                        actualcontainer.releaseLocalAgent(amsMain);
                    }else{
                        System.out.println("REJECTED REQUEST, PLATFORM IS NOT FOUND WITHIN THE ACCEPTED DESTINATIONS DIRECTORY");
                    }
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    if(command.getParams()[0]!=null){

                        byte [] rec = (byte [])command.getParams()[0];
                        Pair<byte [],byte []> pairsenderro = (Pair<byte[],byte[]>)Agencia.deserialize(rec);

                        AttestationSerialized packetReceive = (AttestationSerialized) Agencia.deserialize(pairsenderro.getKey());
                        SecretInformation packet_privative = (SecretInformation) Agencia.deserialize(pairsenderro.getValue());

                        //I need to check the data
                        Location origin = packet_privative.getOrigin();
                        Location destiny = packet_privative.getDestiny();

                        String temPath = "./temp";
                        new File(temPath).mkdir();
                        System.out.println("DESERIALIZE THEIR INFORMATION");
                        try (FileOutputStream stream = new FileOutputStream(temPath+"/akpub.pem")) {
                            stream.write(HotspotsRegister.get(origin.getID()).getAIK());
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
                        int result = Agencia.check_attestation_files(temPath,packet_privative.getChallengue(),false);
                        if(result==0) {
                            System.out.println("COMPUTING THE HASH");
                            String hash = Agencia.computeSHA256(temPath + "/pcr.out");
                            System.out.println("CHEKING TH SAH256");
                            String hashSaved = HotspotsRegister.get(origin.getID()).getSha256();
                            if(!hashSaved.equals(hash)){
                                System.out.println("THE PLATFORM IS CORRUPTED BY A MALWARE");
                                AID amsMain = new AID("ams", false);
                                Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                                PublicKey destinypub = HotspotsRegister.get(origin.getID()).getKeyPub();
                                String ms = "THE PLATFORM IS CORRUPTED BY A MALWARE";
                                amsMainPlatform.addBehaviour(
                                        new SenderACLChallengueError(message, amsMainPlatform,
                                                SecureCloudTPMHelper.REQUEST_ERROR,destinypub,ms,origin)
                                );
                                actualcontainer.releaseLocalAgent(amsMain);
                                //REMOVE
                                SecureInformationCloud malware = HotspotsRegister.get(origin);
                                pendingRedirects.put(origin.getID(),malware);
                                HotspotsRegister.remove(origin);
                            }else{
                                if(packet_privative.getValidation()==1){
                                    System.out.println("BOTH PLATFORMS CONFIRMED");

                                    AID amsMain = new AID("ams", false);
                                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);



                                    PublicKey destinypub = HotspotsRegister.get(origin.getID()).getKeyPub();
                                    System.out.println("*************************************");
                                    System.out.println("A    "+origin.getID());
                                    System.out.println("*************************************");
                                    System.out.println("B    "+destiny.getID());

                                    PublicKey destinyremotepub = HotspotsRegister.get(destiny.getID()).getKeyPub();
                                    System.out.println("*************************************");


                                    PlatformID destinyPT = HotspotsRegister.get(origin.getID()).getPlatformLocation();


                                    System.out.println("HE LLEGADO ANTES DEL BEHAVIOUR");
                                    amsMainPlatform.addBehaviour(
                                            new SenderACLConfirmation(message, amsMainPlatform,
                                                    origin,destiny ,SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM,destinypub,destinyremotepub,privateKeyCA,destinyPT)
                                    );
                                    actualcontainer.releaseLocalAgent(amsMain);
                                }else{


                                    //SEND ATT REQUEST TO THE SECONF PLATFROM
                                    System.out.println("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGUE TO THE DESTINY");


                                    //Location newDestiny = HostpotsRegister.get(packet_privative.getDestiny().getID()).getPlatformLocation();
                                    Location newDestiny = packet_privative.getDestiny();
                                    System.out.println(newDestiny);
                                    String challengue = Agencia.getRandomChallengue();
                                    System.out.println("THE CHALLENGUE IS THE FOLLOWING "+challengue);
                                    //Send the challengue to the origin platform to attestate
                                    AID amsMain = new AID("ams", false);
                                    PublicKey destinypub = HotspotsRegister.get(packet_privative.getDestiny().getID()).getKeyPub();
                                    PlatformID destinyPT = HotspotsRegister.get(packet_privative.getDestiny().getID()).getPlatformLocation();
                                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);

                                    Location originPlatform = packet_privative.getOrigin();


                                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                    amsMainPlatform.addBehaviour(
                                            new SenderACLChallengue(message, amsMainPlatform,
                                                    SecureCloudTPMService.this,originPlatform,
                                                    newDestiny ,challengue,
                                                    SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                                                    destinypub,publicKeyCA,1,destinyPT)
                                    );
                                    actualcontainer.releaseLocalAgent(amsMain);
                                }
                            }
                        }else{
                            System.out.println("ERROR READING THE INFORMATION, IGNORE THE MESSAGE");
                            AID amsMain = new AID("ams", false);
                            Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                            ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                            PublicKey destinypub = HotspotsRegister.get(origin).getKeyPub();
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


    /**
     * ServiceComponent PROCESS THE HORIZONTAL COMMANDS THAT THE PLATFORM RECEIVE FROM ANOTHER EXTERNAL PLATFORM AND
     * CONVERTS IT IN VERTICAL COMMANDS.
     */
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
                throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE SECURE CLOUD SERVICE COMPONENT");
            }
        }


        @Override
        public VerticalCommand serve(HorizontalCommand command) {
            GenericCommand commandResponse = null;
            try {
                String commandReceived = command.getName();
                if (commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_START)) {
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                            "COMPONENT TO START THE HOST", Level.INFO, true, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_START, SecureCloudTPMHelper.NAME,
                                                         null);
                    commandResponse.addParam(command.getParams()[0]);
                } else if (commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_LIST)) {
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                                    "COMPONENT TO REQUEST THE LIST OF THE HOST", Level.INFO, true,
                                    this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_LIST, SecureCloudTPMHelper.NAME,
                                                         null);
                }else if (commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_ACCEPT)) {
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                                    "COMPONENT TO ACCEPT ONE OF THE LIST OF THE HOST", Level.INFO, true,
                                    this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_ACCEPT, SecureCloudTPMHelper.NAME,
                                                         null);
                    commandResponse.addParam(command.getParams());
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_INSERT_PLATFORM)) {
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                                    "COMPONENT TO INSERT A NEW HOTSPOT IN THE SECURE PLATFORM", Level.INFO,
                                    true, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM,
                                                        SecureCloudTPMHelper.NAME, null);

                    Pair<byte [],byte []> PairRequest = (Pair<byte [],byte []>)command.getParams()[0];
                    byte [] key = PairRequest.getKey();
                    byte [] object = PairRequest.getValue();

                    byte[] decryptedKey = Agencia.decrypt(privateKeyCA,key);
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length,
                                                              "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(object);

                    RequestSecureATT pack = (RequestSecureATT) Agencia.deserialize(byteObject);
                    commandResponse.addParam(pack);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                                    "COMPONENT TO MIGRATE A NEW HOSTPOT IN THE SECURE PLATFORM", Level.INFO,
                                    true, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM,
                            SecureCloudTPMHelper.NAME, null);

                    Pair<byte [],byte []> PairRequestMigrate = (Pair<byte [],byte []>)command.getParams()[0];
                    byte [] key = PairRequestMigrate.getKey();
                    byte [] object = PairRequestMigrate.getValue();

                    byte[] decryptedKey = Agencia.decrypt(privateKeyCA,key);
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length,
                                                              "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(object);

                    RequestSecureATT packetReceived = (RequestSecureATT) Agencia.deserialize(byteObject);
                    commandResponse.addParam(packetReceived);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                            "COMPONENT TO CHECK THE ORIGIN", Level.INFO, true, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                            SecureCloudTPMHelper.NAME, null);
                    SecureChallenguerPacket secrectInformation = (SecureChallenguerPacket) command.getParams()[0];

                    byte [] OTP_PUB = secrectInformation.getOTPPub();
                    byte [] OTP_PRIV = secrectInformation.getOTPPriv();
                    byte [] public_Part = secrectInformation.getPartPublic();
                    byte [] private_part = secrectInformation.getPartPriv();

                    byte[] decryptedKeyPublic = Agencia.decrypt(privateKeyCA,OTP_PUB);
                    SecretKey originalKey = new SecretKeySpec(decryptedKeyPublic , 0, decryptedKeyPublic .length,
                                                              "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(public_Part);

                    byte[] decryptedKeyPrivate = Agencia.decrypt(privateKeyCA,OTP_PRIV);
                    SecretKey originalKeyPrivate = new SecretKeySpec(decryptedKeyPrivate , 0,
                                                                     decryptedKeyPrivate .length, "AES");
                    Cipher aesCipherPrivate = Cipher.getInstance("AES");
                    aesCipherPrivate.init(Cipher.DECRYPT_MODE, originalKeyPrivate);
                    byte[] byteObjectPrivatesender = aesCipherPrivate.doFinal(private_part);

                    Pair<byte [],byte []> pairProcessed = new Pair<byte[],byte[]>(byteObject,byteObjectPrivatesender);

                    SecretInformation packet_Priv = (SecretInformation) Agencia.deserialize(byteObjectPrivatesender);

                    if(((Long)command.getParams()[1]-packet_Priv.getTimestamp())<=Agencia.getTimeout()){
                        System.out.println("Checking the values");
                        System.out.println("I receive the following time "+packet_Priv.getTimestamp());
                        commandResponse.addParam(Agencia.serialize(pairProcessed));
                    }else{
                        System.out.println("THE AGENCIA IS TIMEOUT, IGNORING THE REQUEST");
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