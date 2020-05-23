package jade.core.SecureCloud;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
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
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.security.*;
import java.util.*;
import java.util.logging.Level;
import java.util.Random;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.QueryBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

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
            SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE3_PLATFORM,
            SecureCloudTPMHelper.REQUEST_ERROR,
            SecureCloudTPMHelper.REQUEST_LIST_ACCEPTED
    };

    //PERFORMATIVE PRINTER.
    private boolean verbose = false;
    private AgentContainer actualcontainer;

    private  MongoCollection<Document> collection = null;

    //PRINTER
    private JTextArea Printer;

    //SECURE CLOUD KEY PAIR FROM A SECURE PLATFORM;
    private PrivateKey privateKeyCA;
    private PublicKey publicKeyCA;

    //HASHMAP OF ACCEPTED HOTSPOTS
    //Map<String,SecureInformationCloud> HotspotsRegister = new HashMap<String,SecureInformationCloud>();

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

    public JTextArea getGUI(){
        return Printer;
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
                        MessageTemplate.or(
                                MessageTemplate.MatchOntology(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM),
                                MessageTemplate.or(
                                        MessageTemplate.MatchOntology(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM),
                                        MessageTemplate.MatchOntology(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)
                                )
                        ));
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


        public PrivateKey getPrivKey(){
            return privateKeyCA;
        }


        /**
         * THIS FUNCTION TRY TO INITIALIZE THE SECURE PLATFORM, ACCORDING TO A KEY PAIR INTRODUCED AS
         * PARAMETERS IN ORDER TO SIMULATE IT. FIRST OF ALL, I NEED TO CONTACT WITH THE AMS OF THE MAIN
         * PLATFORM IN MY ENVIRONMENT.
         * @param secureCAPlatform
         */
        public synchronized void doStartCloud(SecureCAPlatform secureCAPlatform,PrivateKey priv, PublicKey pub,
                                              JTextArea printer,String username, String password) {
            Printer = printer;
            Agencia.printLog("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED NAME AGENT:" +
                             secureCAPlatform.getAID(), Level.INFO, SecureCloudTPMHelper.DEBUG,
                             this.getClass().getName());
            Agencia.printLog("START THE SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                             Level.INFO, true, this.getClass().getName());
            GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_START, SecureCloudTPMHelper.NAME,
                                                        null);
            Pair<PrivateKey,PublicKey> KeyPairCA = new Pair<PrivateKey,PublicKey>(priv,pub);
            command.addParam(KeyPairCA);
            command.addParam(username);
            command.addParam(password);
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


        public synchronized void listAcceptedPlatforms(SecureCAPlatform secureCAPlatform) {
            Agencia.printLog("-> THE PROCCES TO COMMUNICATE WITH THE AMS HAS JUST STARTED IN ORDER TO RETRIEVE" +
                            "THE LIST OF SECURE PLATFORMS. NAME AGENT:" +
                            secureCAPlatform.getAID(), Level.INFO, SecureCloudTPMHelper.DEBUG,
                    this.getClass().getName());
            Agencia.printLog("START THE LIST SERVICE TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM",
                    Level.INFO, true, this.getClass().getName());
            GenericCommand command = new GenericCommand(SecureCloudTPMHelper.REQUEST_LIST_ACCEPTED,
                    SecureCloudTPMHelper.NAME, null);
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
                }else if(commandName.equals(SecureCloudTPMHelper.REQUEST_LIST_ACCEPTED)){

                    Agencia.printLog("PROCESSING THE COMMAND TO COMMUNICATE WITH THE AMS AND ACCEPT ONE OF " +
                            "THE LIST OF THE HOTSPOTS", Level.INFO, true, this.getClass().getName());
                    try{
                        obj.doRequestAcceptListAMS(command);
                    }catch(Exception ie){
                        System.out.println("THERE ARE AN ERROR PROCESSING REQUEST LIST ADDRESS IN THE COMMAND " +
                                "SOURCE SINK");
                        ie.printStackTrace();
                    }

                } else if(commandName.equals(SecureCloudTPMHelper.REQUEST_ACCEPT)){
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
                        obj.doCheckAttestationHostpotORIGIN(command);
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
                    Agencia.printLog("PROCESSING THE VERTICAL COMMAND START SECURE CA INTO THE AMS " +
                                    "DESTINATION CONTAINER", Level.INFO, true, this.getClass().getName());

                    Pair<PrivateKey,PublicKey> keyPairReceive = (Pair<PrivateKey, PublicKey>)command.getParams()[0];
                    String username = (String)command.getParams()[1];
                    String password = (String)command.getParams()[2];

                    try{
                        System.out.println("USERNAME: "+username);

                        MongoClientURI mongoCientURI = new MongoClientURI("mongodb+srv://"+username+":"+password+"@jadecluster-lf67e.mongodb.net/test?retryWrites=true&w=majority");
                        MongoClient mongoClient = new MongoClient(mongoCientURI);
                        MongoDatabase database = mongoClient.getDatabase("hotspot");
                        collection = database.getCollection("hotspot");
                        System.out.println("NUMBER OF INSTANCES: "+collection.count());


                        Printer.append("INITIALIZING THE KEY PAIR RECEIVE\n");
                        System.out.println("INITIALIZING THE KEY PAIR RECEIVE");
                        privateKeyCA = keyPairReceive.getKey();
                        publicKeyCA  = keyPairReceive.getValue();

                        Printer.append("NAME OF THE CONTAINER: "+actualcontainer.getID().getName()+"\n");
                        Printer.append("*********************PUBLIC*****************************\n");
                        Printer.append("PUBLIC KEY: "+publicKeyCA+"\n");
                        Printer.append("*********************PUBLIC*****************************\n");
                        Printer.append("*********************SECRET*****************************\n");
                        Printer.append("PRIVATE KEY: "+privateKeyCA+"\n");
                        Printer.append("*********************SECRET*****************************\n");

                        System.out.println("NAME OF THE CONTAINER: "+actualcontainer.getID().getName());
                        System.out.println("*********************PUBLIC*****************************");
                        System.out.println("PUBLIC KEY: "+publicKeyCA);
                        System.out.println("*********************PUBLIC*****************************");
                        System.out.println("*********************SECRET*****************************");
                        System.out.println("PRIVATE KEY: "+privateKeyCA);
                        System.out.println("*********************SECRET*****************************");



                    }catch(Exception e){
                        System.out.println("THERE ARE AN ERROR INIT THE PLATFORM, CHECK USERNAME AND VALUE");
                        Printer.append("THERE ARE AN ERROR INIT THE PLATFORM, CHECK USERNAME AND VALUE");
                    }




                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_LIST)){
                    Agencia.printLog("PROCESSING THE VERTICAL COMMAND LIST REQUEST INTO THE AMS " +
                                    "DESTINATION CONTAINER", Level.INFO, true, this.getClass().getName());

                    Printer.append("NAME OF THE REGISTER HOTSPOTS IN: "+actualcontainer.getID().getName()+"\n");
                    Printer.append("*********************HOTSPOTS*****************************\n");

                    System.out.println("NAME OF THE REGISTER HOTSPOTS IN: "+actualcontainer.getID().getName());
                    System.out.println("*********************HOTSPOTS*****************************");
                    Iterator it = pendingRedirects.entrySet().iterator();
                    while(it.hasNext()){
                        Map.Entry pair = (Map.Entry)it.next();
                        Printer.append(pair.getKey() + " = " + pair.getValue()+"\n");
                        System.out.println(pair.getKey() + " = " + pair.getValue());
                    }
                    System.out.println("*********************HOTSPOTS*****************************");
                    Printer.append("*********************HOTSPOTS*****************************\n");

                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_LIST_ACCEPTED)){

                    Agencia.printLog("PROCESSING THE VERTICAL COMMAND LIST REQUEST INTO THE AMS " +
                            "DESTINATION CONTAINER", Level.INFO, true, this.getClass().getName());

                    Printer.append("NAME OF THE REGISTER HOTSPOTS IN: "+actualcontainer.getID().getName()+"\n");
                    Printer.append("*********************HOTSPOTS*****************************\n");

                    System.out.println("NAME OF THE REGISTER HOTSPOTS IN: "+actualcontainer.getID().getName());
                    System.out.println("*********************HOTSPOTS*****************************");



                    MongoCursor<Document> cursor = collection.find().iterator();
                    while(cursor.hasNext()) {
                        Document str = cursor.next();
                       Printer.append(str.get("_id")+" = "+str.get("SHA256")+"\n");
                    }


                    System.out.println("*********************HOTSPOTS*****************************");
                    Printer.append("*********************HOTSPOTS*****************************\n");

                } else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_ACCEPT)){
                    Agencia.printLog("PROCESSING THE VERTICAL COMMAND LIST CLOUD REQUEST INTO THE AMS " +
                                    "DESTINATION CONTAINER", Level.INFO, true, this.getClass().getName());
                    String index = (String) command.getParams()[0];

                    Document FindHotspot = collection.find(new Document("_id", index)).first();
                    SecureInformationCloud content = pendingRedirects.get(index);
                    if(FindHotspot==null){
                            if(content!=null){

                                //SAVE INTO MONGO
                                Document hotspotNew = new Document("_id", index);
                                hotspotNew.append("PUBLIC_AIK", Agencia.serialize(content.getAIK()));
                                hotspotNew.append("PUBLIC_KEY", Agencia.serialize(content.getKeyPub()));
                                hotspotNew.append("PLATFORM_LOCATION", Agencia.serialize(content.getPlatformLocation()));
                                hotspotNew.append("SHA256", content.getSha256());
                                collection.insertOne(hotspotNew);
                                Printer.append("PLATFORM INSERTED CORRECTLY\n");
                                pendingRedirects.remove(index);
                            }
                    }else{
                        //ESTA METIDO TENGO QUE VER SI EL HASH ES EL MISMO
                        String hashStore = FindHotspot.getString("SHA256");
                        if(content.getSha256().equals(hashStore)){
                            Printer.append("THE PLATFORM IS ALREADY ACCEPTED IN THE CA");
                            pendingRedirects.remove(index);
                        }else{
                            Document hotspotNew = new Document("_id", index);
                            hotspotNew.append("PUBLIC_AIK", Agencia.serialize(content.getAIK()));
                            hotspotNew.append("PUBLIC_KEY", Agencia.serialize(content.getKeyPub()));
                            hotspotNew.append("PLATFORM_LOCATION", Agencia.serialize(content.getPlatformLocation()));
                            hotspotNew.append("SHA256", content.getSha256());
                            collection.insertOne(hotspotNew);

                            Bson filter = Filters.eq("_id", index);
                            Bson update = new Document("$set", hotspotNew);
                            UpdateOptions options = new UpdateOptions().upsert(true);
                            collection.updateOne(filter, update, options);
                            Printer.append("THE PLATFORM IS UPDATE CORRECTLY IN THE CA");
                            pendingRedirects.remove(index);
                        }
                    }
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM)){

                    Pair<String,Object> requestInsert = (Pair<String,Object>)  command.getParams()[0];
                    RequestSecureATT packSecure = (RequestSecureATT) requestInsert.getValue();


                    Printer.append("*********************NEW REQUEST*****************************\n");
                    Printer.append("LOCATION -> "+requestInsert.getKey()+"\n");
                    Printer.append("PUBLIC PASSWORD -> "+packSecure.getPublicPassword()+"\n");
                    Printer.append("*********************NEW REQUEST*****************************\n");

                    System.out.println("*********************NEW REQUEST*****************************");
                    System.out.println("LOCATION -> "+requestInsert.getKey());
                    System.out.println("PUBLIC PASSWORD -> "+packSecure.getPublicPassword());
                    System.out.println("*********************NEW REQUEST*****************************");


                    Document findHostpot = collection.find(new Document("_id", requestInsert.getKey())).first();
                    Document findHostpotNew = collection.find(new Document("_id", packSecure.getPlatformLocationOrigin().getID())).first();


                    if(findHostpot!=null){
                        System.out.println("THE PLATFORM IS ALREADY INCLUDED WITHIN THE CONFIRMED LIST");
                    }else if(findHostpotNew!=null){
                        System.out.println("THE PLATFORM IS ALREADY INCLUDED WITHIN THE PENDING LIST");
                    }else{
                        Printer.append("PCRS LIST:\n");
                        System.out.println("PCRS LIST:");

                        String temPath = "./temp";
                        new File(temPath).mkdir();
                        Printer.append("DESERIALIZE THEIR INFORMATION\n");
                        System.out.println("DESERIALIZE THEIR INFORMATION");
                        AttestationSerialized packetReceive = packSecure.getPCR_Signed();
                        Agencia.deserializeATT(temPath,packetReceive);

                        int result = Agencia.check_attestation_files(temPath,"",true, Printer);
                        if(result==0){
                            Iterator it = null;
                            Printer.append("COMPUTING THE HASH:\n");
                            System.out.println("COMPUTING THE HASH:");
                            String hash = Agencia.computeSHA256(temPath+"/pcr.out", Printer);

                            SecureInformationCloud saveRequest = new SecureInformationCloud(
                                    packSecure.getPublicPassword(),hash,packetReceive.getAIKPub(),
                                    packSecure.getPlatformLocationOrigin());
                            Agencia.deleteFolder(new File(temPath));

                            Printer.append("ADDING THE REQUEST IN THE PENDING LIST\n");
                            System.out.println("ADDING THE REQUEST IN THE PENDING LIST");
                            pendingRedirects.put(packSecure.getPlatformLocationOrigin().getID(),saveRequest);





                            it = pendingRedirects.entrySet().iterator();
                            Printer.append("PLATFORM INSERTED IN THE CORRECTLY PENDING LIST\n");
                            System.out.println("PLATFORM INSERTED IN THE CORRECTLY PENDING LIST");

                            Printer.append("*********************HOTSPOTS*****************************\n");
                            System.out.println("*********************HOTSPOTS*****************************");
                            while(it.hasNext()){
                                Map.Entry pair = (Map.Entry)it.next();
                                SecureInformationCloud iteration = (SecureInformationCloud)pair.getValue();
                                Printer.append(pair.getKey() + " = " + iteration.getSha256()+"\n");
                                System.out.println(pair.getKey() + " = " + iteration.getSha256());
                            }
                            Printer.append("*********************HOTSPOTS*****************************\n");
                            System.out.println("*********************HOTSPOTS*****************************");
                        }else{
                            System.out.println("ERROR READING THE ATTESTATION DATA.");
                        }
                    }
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM)){

                    Pair<String,Object> requestMigrate = (Pair<String,Object>) command.getParams()[0];

                    RequestSecureATT packetReceived = (RequestSecureATT)requestMigrate.getValue();
                    PlatformID originPlatform = packetReceived.getPlatformLocationOrigin();
                    PlatformID destiny = packetReceived.getPlatformCALocationDestiny();


                    Document originCheck = collection.find(new Document("_id", requestMigrate.getKey())).first();
                    Document destinyCheck = collection.find(new Document("_id", packetReceived.getPlatformCALocationDestiny().getID())).first();

                    if(originCheck!=null && destinyCheck!=null){

                        Printer.append("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGE\n");
                        System.out.println("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGE");
                        String challenge = Agencia.getRandomChallenge();

                        Printer.append("**************************************************\n");
                        Printer.append("THE CHALLENGE IS THE FOLLOWING "+challenge+"\n");
                        Printer.append("**************************************************\n");

                        System.out.println("**************************************************");
                        System.out.println("THE CHALLENGE IS THE FOLLOWING "+challenge);
                        System.out.println("**************************************************");

                        AID amsMain = new AID("ams", false);

                        Document hotspotStore = collection.find(new Document("_id", originPlatform.getID())).first();
                        Document hotspotStoreDestiny = collection.find(new Document("_id", destiny.getID())).first();




                        Binary bin = hotspotStore.get("PUBLIC_KEY", org.bson.types.Binary.class);
                        PublicKey destinypub = (PublicKey) Agencia.deserialize(bin.getData());



                        Binary binOrigin = hotspotStore.get("PLATFORM_LOCATION", org.bson.types.Binary.class);
                        PlatformID RegisterOrigin = (PlatformID) Agencia.deserialize(binOrigin.getData());

                        Binary binDestiny = hotspotStoreDestiny.get("PLATFORM_LOCATION", org.bson.types.Binary.class);
                        PlatformID RegisterDestiny = (PlatformID) Agencia.deserialize(binDestiny.getData());



                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                        Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                        amsMainPlatform.addBehaviour(
                                new SenderCAChallenge(message, amsMainPlatform,
                                        SecureCloudTPMService.this,RegisterOrigin,RegisterDestiny,
                                        challenge,SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,destinypub,
                                        publicKeyCA,0, packetReceived.getAgente(),Printer)
                        );
                        actualcontainer.releaseLocalAgent(amsMain);
                    }else{
                        System.out.println("REJECTED REQUEST, PLATFORM IS NOT FOUND WITHIN THE ACCEPTED " +
                                "DESTINATIONS DIRECTORY");
                    }
                }else if(CommandName.equals(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM)){

                    if(command.getParams()[0]!=null){

                        Pair<String,Object> requestZone1Processed = (Pair<String,Object>) command.getParams()[0];

                        Pair<byte [],byte []> PairATTReceive = (Pair<byte[],byte[]>)requestZone1Processed.getValue();
                        AttestationSerialized packetReceive = (AttestationSerialized)
                                Agencia.deserialize(PairATTReceive.getKey());
                        PrivateInformationCA packet_privative = (PrivateInformationCA)
                                Agencia.deserialize(PairATTReceive.getValue());

                        PlatformID origin = packet_privative.getOrigin();
                        PlatformID destiny = packet_privative.getDestiny();

                        if(requestZone1Processed.getKey().equals(origin.getID()) ||
                                requestZone1Processed.getKey().equals(destiny.getID())){

                            String temPath = "./temp";
                            new File(temPath).mkdir();
                            System.out.println("DESERIALIZE THEIR INFORMATION");

                            Document hotspotSaved = collection.find(new Document("_id", origin.getID())).first();

                            try (FileOutputStream stream = new FileOutputStream(temPath+"/akpub.pem")) {
                                Binary binKey = hotspotSaved.get("PUBLIC_AIK", org.bson.types.Binary.class);
                                stream.write(binKey.getData());

                            }
                            Agencia.deserializeATTWAIK(temPath,packetReceive);
                            int result = Agencia.check_attestation_files(temPath,packet_privative.getChallenge(),
                                    false, Printer);
                            if(result==0) {
                                System.out.println("COMPUTING THE HASH");
                                String hash = Agencia.computeSHA256(temPath + "/pcr.out", Printer);
                                String hashSaved =hotspotSaved.getString("SHA256");

                                if(!hashSaved.equals(hash)){

                                    Printer.append("**************************************************\n");
                                    Printer.append("THE PLATFORM IS CORRUPTED BY A MALWARE\n");
                                    Printer.append("**************************************************\n");

                                    System.out.println("**************************************************");
                                    System.out.println("THE PLATFORM IS CORRUPTED BY A MALWARE");
                                    System.out.println("**************************************************");

                                    AID amsMain = new AID("ams", false);
                                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
                                    Binary binDestinypub = hotspotSaved.get("PUBLIC_KEY", org.bson.types.Binary.class);
                                    PublicKey destinypub = (PublicKey) Agencia.deserialize(binDestinypub.getData());
                                    String ms = "THE PLATFORM IS CORRUPTED BY A MALWARE";
                                    amsMainPlatform.addBehaviour(
                                            new SenderCAChallengeError(message, amsMainPlatform,
                                                    SecureCloudTPMHelper.REQUEST_ERROR,destinypub,ms,origin)
                                    );
                                    actualcontainer.releaseLocalAgent(amsMain);

                                    collection.findOneAndDelete(Filters.eq("_id",origin));
                                    //SecureInformationCloud malware = HotspotsRegister.get(origin);
                                    //pendingRedirects.put(origin.getID(),malware);
                                    //HotspotsRegister.remove(origin);
                                }else{

                                    if(packet_privative.getValidation()==1){

                                        Printer.append("**************************************************\n");
                                        Printer.append("BOTH PLATFORMS CONFIRMED\n");
                                        Printer.append("**************************************************\n");

                                        System.out.println("**************************************************");
                                        System.out.println("BOTH PLATFORMS CONFIRMED");
                                        System.out.println("**************************************************");

                                        /**
                                         * NECESITA REVISION PARA SER COMPLETADO CORRECTAMENTE
                                         * EN ESTA PARTE, LO QUE HE PENSADO ES GENERARO UN ID FIRMADO, JUNTO
                                         * CON UN TIMESTAMP Y ENVIARSELO A AMBAS PARTES, POR ELLO, CUANDO
                                         * LA PLATAFORMA ORIGEN DESEE ENVIAR A LA DE DESTINO, INCLUIRA ESTE ID
                                         * JUNTO CON EL AGENTE, CUANDO LLEGUE A LA PLATAFORMA DESTINO, MIRARA EN LA
                                         * LISTA SI EXISTE ESE ID, Y COMPARARA EL TIEMPO OBTENIDO EN EL PAQUETE
                                         * ES COMO SI FUERA UN PERIODO DE GRACIA DEFINIDO O LIMITADO EN CIERTA MEDIDA
                                         * POR LA AGENCIA.
                                         */

                                        AID amsMain = new AID("ams", false);




                                        Document hotspotSavedOR = collection.find(new Document("_id", origin.getID())).first();
                                        Document hotspotSavedDR = collection.find(new Document("_id", destiny.getID())).first();


                                        Binary binDestinyPUB = hotspotSavedOR.get("PUBLIC_KEY", org.bson.types.Binary.class);
                                        PublicKey destinypub = (PublicKey) Agencia.deserialize(binDestinyPUB.getData());
                                        Binary binDestinyREMOTE = hotspotSavedDR.get("PUBLIC_KEY", org.bson.types.Binary.class);
                                        PublicKey destinyremotepub = (PublicKey) Agencia.deserialize(binDestinyREMOTE.getData());
                                        Binary binDestinyORIGIN = hotspotSavedOR.get("PLATFORM_LOCATION", org.bson.types.Binary.class);
                                        PlatformID RegisterOrigin = (PlatformID) Agencia.deserialize(binDestinyORIGIN.getData());
                                        Binary binDestinyREGISTER = hotspotSavedDR.get("PLATFORM_LOCATION", org.bson.types.Binary.class);
                                        PlatformID RegisterDestiny = (PlatformID) Agencia.deserialize(binDestinyREGISTER.getData());


                                        String challenge = Agencia.getRandomChallenge();
                                        Calendar c = Calendar.getInstance();
                                        c.add(Calendar.SECOND, Agencia.getTimeout());
                                        Date timeChallenge = new Date(c.getTimeInMillis()+Agencia.getTimeout());


                                        Printer.append("**************************************************\n");
                                        Printer.append("TOKEN GENERATED: "+challenge+"\n");
                                        Printer.append("TIMESTAMP: "+timeChallenge+"\n");
                                        Printer.append("**************************************************\n");


                                        System.out.println("**************************************************");
                                        System.out.println("TOKEN GENERATED: "+challenge);
                                        System.out.println("TIMESTAMP: "+timeChallenge);
                                        System.out.println("**************************************************");

                                        //SENDING TO THE DESTINY FIRST

                                        Agent amsMainPlatformDestiny = actualcontainer.acquireLocalAgent(amsMain);
                                        ACLMessage messageDestiny = new ACLMessage(ACLMessage.REQUEST);
                                        amsMainPlatformDestiny.addBehaviour(
                                                new SenderCAConfirmation(messageDestiny, amsMainPlatformDestiny,
                                                        RegisterDestiny, RegisterOrigin,
                                                        SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE3_PLATFORM,
                                                        destinyremotepub, destinypub, privateKeyCA, challenge,
                                                        timeChallenge, packet_privative.getAgent())
                                        );
                                        actualcontainer.releaseLocalAgent(amsMain);

                                        //SENDING TO THE ORIGIN SECOND
                                        

                                        Agent amsMainPlatformOrigin = actualcontainer.acquireLocalAgent(amsMain);
                                        ACLMessage messageOrigin = new ACLMessage(ACLMessage.REQUEST);
                                        amsMainPlatformOrigin.addBehaviour(
                                                new SenderCAConfirmation(messageOrigin, amsMainPlatformOrigin,
                                                        RegisterOrigin,RegisterDestiny ,
                                                        SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE2_PLATFORM, destinypub,
                                                        destinyremotepub, privateKeyCA, challenge, timeChallenge,
                                                        packet_privative.getAgent())
                                        );
                                        actualcontainer.releaseLocalAgent(amsMain);

                                    }else{
                                        //SEND ATT REQUEST TO THE SECOND PLATFORM
                                        Printer.append("**************************************************\n");
                                        Printer.append("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGE " +
                                                "TO THE DESTINY\n");
                                        Printer.append("**************************************************\n");


                                        System.out.println("**************************************************");
                                        System.out.println("THE PLATFORM IS RELIABLE, PROCEEDING TO SEND A CHALLENGE " +
                                                "TO THE DESTINY");
                                        System.out.println("**************************************************");

                                        String challenge = Agencia.getRandomChallenge();

                                        Printer.append("**************************************************\n");
                                        Printer.append("THE CHALLENGE IS THE FOLLOWING "+challenge+"\n");
                                        Printer.append("**************************************************\n");

                                        System.out.println("**************************************************");
                                        System.out.println("THE CHALLENGE IS THE FOLLOWING "+challenge);
                                        System.out.println("**************************************************");

                                        //SEND THE CHALLENGE TO THE DESTINY PLATFORM TO ATTESTATE IT
                                        AID amsMain = new AID("ams", false);


                                        Document hotspotSavedOR = collection.find(new Document("_id", packet_privative.getDestiny().getID())).first();


                                        Binary binDestinyPUB = hotspotSavedOR.get("PUBLIC_KEY", org.bson.types.Binary.class);
                                        PublicKey destinypub = (PublicKey) Agencia.deserialize(binDestinyPUB.getData());
                                        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);

                                        PlatformID originPlatform = packet_privative.getOrigin();
                                        PlatformID newDestiny = packet_privative.getDestiny();





                                        Document hotspotSavedOOR = collection.find(new Document("_id", originPlatform.getID())).first();
                                        Document hotspotSavedDDR = collection.find(new Document("_id", newDestiny.getID())).first();



                                        Binary binOR = hotspotSavedOOR.get("PLATFORM_LOCATION", org.bson.types.Binary.class);
                                        PlatformID RegisterOrigin = (PlatformID) Agencia.deserialize(binOR.getData());
                                        Binary binDR = hotspotSavedDDR.get("PLATFORM_LOCATION", org.bson.types.Binary.class);
                                        PlatformID RegisterDestiny = (PlatformID) Agencia.deserialize(binDR.getData());




                                        Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                        amsMainPlatform.addBehaviour(
                                                new SenderCAChallenge(message, amsMainPlatform,
                                                        SecureCloudTPMService.this,RegisterDestiny,
                                                        RegisterOrigin ,challenge,
                                                        SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                                                        destinypub,publicKeyCA,1, packet_privative.getAgent(), Printer)
                                        );
                                        actualcontainer.releaseLocalAgent(amsMain);
                                    }
                                }
                            }else{

                                Printer.append("**************************************************\n");
                                Printer.append("ERROR READING THE INFORMATION, IGNORE THE MESSAGE\n");
                                Printer.append("**************************************************\n");


                                System.out.println("**************************************************");
                                System.out.println("ERROR READING THE INFORMATION, IGNORE THE MESSAGE");
                                System.out.println("**************************************************");

                                AID amsMain = new AID("ams", false);
                                Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                                ACLMessage message = new ACLMessage(ACLMessage.REQUEST);


                                Document hotspotSavedOR = collection.find(new Document("_id", origin)).first();
                                Binary binKEY = hotspotSavedOR.get("PUBLIC_KEY", org.bson.types.Binary.class);
                                PublicKey destinypub = (PublicKey) Agencia.deserialize(binKEY.getData());

                                String ms = "ERROR READING THE INFORMATION, IGNORE THE MESSAGE";
                                amsMainPlatform.addBehaviour(
                                        new SenderCAChallengeError(message, amsMainPlatform,
                                                SecureCloudTPMHelper.REQUEST_ERROR,destinypub,ms,origin)
                                );
                                actualcontainer.releaseLocalAgent(amsMain);
                            }
                        }else{
                            Printer.append("THERE ARE AN ERROR. IGNORING THE REQUEST, A POSSIBLE ATTACKER IS" +
                                    "REQUESTING TO MIGRATE\n");
                            System.out.println("THERE ARE AN ERROR. IGNORING THE REQUEST, A POSSIBLE ATTACKER IS" +
                                               "REQUESTING TO MIGRATE");
                        }
                    }else{
                        Printer.append("ERROR TIMEOUT IGNORING THE REQUEST \n");
                        System.out.println("ERROR TIMEOUT IGNORING THE REQUEST ");
                    }
                }
            }catch(Exception ex){
                Printer.append("AN ERROR HAPPENED WHEN RUNNING THE SERVICE IN THE COMMAND TARGET SINK\n");
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
                    commandResponse.addParam(command.getParams()[1]);
                    commandResponse.addParam(command.getParams()[2]);
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
                    commandResponse.addParam(command.getParams()[0]);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_LIST_ACCEPTED)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                                    "COMPONENT TO REQUEST THE LIST OF THE HOST", Level.INFO, true,
                            this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_LIST_ACCEPTED,
                            SecureCloudTPMHelper.NAME, null);
                } else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_INSERT_PLATFORM)) {
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                                    "COMPONENT TO INSERT A NEW HOTSPOT IN THE SECURE PLATFORM", Level.INFO,
                                    true, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_INSERT_PLATFORM,
                                                        SecureCloudTPMHelper.NAME, null);

                    Pair<String,Object> requestInsert = (Pair<String,Object>)command.getParams()[0];
                    Pair<byte [],byte []> PairRequest = (Pair<byte [],byte []>) requestInsert.getValue();
                    byte [] key = PairRequest.getKey();
                    byte [] object = PairRequest.getValue();

                    byte[] decryptedKey = Agencia.decrypt(privateKeyCA,key);
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length,
                                                              "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(object);

                    RequestSecureATT pack = (RequestSecureATT) Agencia.deserialize(byteObject);
                    Pair<String,RequestSecureATT> newPacket = new Pair<String,RequestSecureATT>(requestInsert.getKey(),
                                                                                                pack);
                    commandResponse.addParam(newPacket);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                                    "COMPONENT TO MIGRATE A NEW HOSTPOT IN THE SECURE PLATFORM", Level.INFO,
                                    true, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_PLATFORM,
                            SecureCloudTPMHelper.NAME, null);

                    Pair<String,Object> requestMigrate = (Pair<String,Object>)command.getParams()[0];
                    Pair<byte [],byte []> PairRequestMigrate = (Pair<byte [],byte []>)requestMigrate.getValue();
                    byte [] key = PairRequestMigrate.getKey();
                    byte [] object = PairRequestMigrate.getValue();

                    byte[] decryptedKey = Agencia.decrypt(privateKeyCA,key);
                    SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length,
                                                              "AES");
                    Cipher aesCipher = Cipher.getInstance("AES");
                    aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
                    byte[] byteObject = aesCipher.doFinal(object);

                    RequestSecureATT packetReceived = (RequestSecureATT) Agencia.deserialize(byteObject);
                    Pair<String,RequestSecureATT> newPacketMigrate = new Pair<String,RequestSecureATT>(
                            requestMigrate.getKey(), packetReceived);
                    commandResponse.addParam(newPacketMigrate);
                }else if(commandReceived.equals(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM)){
                    Agencia.printLog("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CLOUD MD IN THE SERVICE " +
                            "COMPONENT TO CHECK THE ORIGIN", Level.INFO, true, this.getClass().getName());
                    commandResponse = new GenericCommand(SecureCloudTPMHelper.REQUEST_MIGRATE_ZONE1_PLATFORM,
                            SecureCloudTPMHelper.NAME, null);


                    Pair<String,Object> requestZone1 = (Pair<String,Object>) command.getParams()[0];
                    SecureChallengerPacket secrectInformation = (SecureChallengerPacket) requestZone1.getValue();

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

                    PrivateInformationCA packet_Priv = (PrivateInformationCA) Agencia.deserialize(byteObjectPrivatesender);

                    if(((Long)command.getParams()[1]-packet_Priv.getTimestamp())<=Agencia.getTimeout()){
                        Pair<String,Object> requestZone1Processed = new Pair<String,Object>(requestZone1.getKey(),
                                                                                            pairProcessed);
                        Printer.append("Checking the values\n");
                        Printer.append("I receive the following time "+packet_Priv.getTimestamp()+"\n");
                        System.out.println("Checking the values");
                        System.out.println("I receive the following time "+packet_Priv.getTimestamp());
                        commandResponse.addParam(requestZone1Processed);
                    }else{
                        Printer.append("THE AGENCIA IS TIMEOUT, IGNORING THE REQUEST\n");
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