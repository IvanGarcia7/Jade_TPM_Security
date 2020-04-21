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

    //Interesting variables
    public static final String NAME = "jade.core.SecureInterTPM.SecureInterTPM";
    public static final String VERBOSE = "jade_core_SecureInterTPMS_SecureInterTPMService_verbose";

    //Time var
    long startTime = System.nanoTime();

    //List of petitions
    private List<RequestMove> request_move_list = new ArrayList<RequestMove>();
    private List<RequestClone> request_clone_list = new ArrayList<RequestClone>();

    private Slice actualSlicer=new ServiceComponent();
    private ServiceHelper thisHelper = new SecureInterTPMServiceHelperImpl();

    //Define input and output Filters
    private Filter OutFilter = null;
    private Filter InFilter = null;

    //Define input and output Sinks
    private Sink OutputSink=new CommandSourceSink();
    private Sink InputSink=new CommandTargetSink();

    //Request the commands that i have implemeted
    private String [] actualCommands = new String [] {
            SecureInterTPMHelper.REQUEST_ATTESTATION,
            SecureInterTPMHelper.REQUEST_CONFIRMATION
    };

    //performative printer
    private boolean verbose = false;
    private AgentContainer actualcontainer;

    @Override
    public String getName() {
        return SecureInterTPMHelper.NAME;
    }


    public void init(AgentContainer agentcontainer, Profile prof) throws ProfileException{
        super.init(agentcontainer, prof);
        actualcontainer = agentcontainer;
    }

    //Start-up configurations
    public void boot(Profile prof) throws ServiceException {
        super.boot(prof);
        verbose = prof.getBooleanProperty(VERBOSE, false);
        System.out.println("SECUREINTERTPM SERVICE STARTED CORRECTLY ON CONTAINER CALLED: "+actualcontainer.getID());

    }

    //Function to retrieve the Helper Service
    public ServiceHelper getHelper(Agent agent) throws ServiceException{
        if(agent instanceof SecureAgent){
            return thisHelper;
        }else{
            throw new ServiceException("THIS SERVICE IS NOT ALLOWED TO RUN IN THIS AGENT");
        }
    }

    //Function to get Horizontal Commands
    public Class getHorizontalInterface(){
        return SecureInterTPMSlice.class;
    }

    //Function to get the slice of this service
    public Slice getLocalSlice(){
        return actualSlicer;
    }

    //Function to get the commands that i have implemented
    public String [] getOwnedCommands() {
        return actualCommands;
    }

    //Get the command filter of the service
    public Filter getCommandFilter(boolean direction) {
        if (direction == Filter.OUTGOING) {
            return OutFilter;
        }
        else {
            return InFilter;
        }
    }

    //Get the sink of the service
    public Sink getCommandSink(boolean side) {
        if(side == Sink.COMMAND_SOURCE) {
            return OutputSink;
        }
        else {
            return InputSink;
        }
    }

    public Behaviour getAMSBehaviour() {
        System.out.println("KIWI");
        AID amsAID = new AID("ams", false);
        Agent ams = actualcontainer.acquireLocalAgent(amsAID);
        MessageTemplate mt =
                MessageTemplate.and(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchOntology(SecureInterTPMHelper.REQUEST_ATTESTATION),
                                MessageTemplate.MatchOntology(SecureInterTPMHelper.REQUEST_CONFIRMATION)));
        // Create the behaviour
        ResponserACL resp = new ResponserACL(ams, mt, SecureInterTPMService.this);
        // Release the AMS agent

        actualcontainer.releaseLocalAgent(amsAID);
        return resp;

    }

    public class SecureInterTPMServiceHelperImpl implements SecureInterTPMHelper {

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
         * @param agent
         * @param destiny
         */
        public synchronized void doMoveTPM(SecureAgent agent, Location destiny){
            StringBuilder sb = new StringBuilder();
            if (request_move_list.size()==0 & request_clone_list.size()==0) {
                request_move_list.add(new RequestMove(agent,destiny));

                //INICIALIZE THE HANDLER OPERATIONS TO PERFORM THE MOVE REQUEST

                sb.append("-> THE PROCCES TO MOVE THE AGENT HAS JUST STARTED").append(agent.getAID());
                System.out.println(sb.toString());

                //Agencia.printLog("START THE SERVICE TO MOVE THE SECUREAGENT",
                        //Level.INFO,true,this.getClass().getName());

                /*
                    CREATE A NEW VERTICALCOMMAND TO PERFORM THE OPPERATION THAT I NEED IT,
                    ACCORDING TO THE DOCUMENTATION.
                */

                GenericCommand command = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION,
                        SecureInterTPMHelper.NAME,null);

                //CREATE A NEW PACKET WITH THE INFORMATION THAT I CONSIDER GOOF TO KNOW
                RequestAttestation paquet = new RequestAttestation(agent.getAID(),agent.here(),destiny,agent.getAMS());
                command.addParam(paquet);

                //SEND THE VERTICAL COMMAND, IN ORDER TO CATCH IT IN THE SPECIFIC SERVICE
                //Agencia.printLog("AGENT REQUEST MOVE AND SEND THE REQUEST TO THE AGENCY",
                        //Level.INFO,true,this.getClass().getName());

                //SUBMMIT THE VERTICALCOMMAND
                try{
                    System.out.println("-> THE VERTICAL COMMAND TO MOVE IS CORRECTLY SUBMITED");
                    SecureInterTPMService.this.submit(command);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else {
                sb.append("ITS NOT POSSIBLE TO START THE MOVE PROCESS  ").append(agent.getAID())
                        .append(" BUSSY TPM HANDLER OTHER REQUEST");

                Agencia.printLog("ITS NOT POSSIBLE TO START THE MOVE PROCESS " +
                        " BUSSY TPM HANDLER OTHER REQUEST", Level.INFO,true,this.getClass().getName());

                //CALL THE CLONE ERROR TO PRINT THE MESSAGE
                agent.doSecureMoveError(sb.toString());
            }
        }


        /**
         * This method take an agent, and the location requested to move.
         * First of all, review if the TPM is free to handler with the opperation needit
         * and then inicialize the process
         * @param agent
         * @param destiny
         */
        public synchronized void doCloneTPM(SecureAgent agent, Location destiny, String nameAgent){
            StringBuilder sb = new StringBuilder();
            if (request_move_list.size()!=0 || request_clone_list.size()!=0) {
                request_clone_list.add(new RequestClone(agent,destiny,nameAgent));

                //INICIALIZE THE HANDLER OPERATIONS TO PERFORM THE CLONE REQUEST

                sb.append("THE PROCCES TO CLONE THE AGENT HAS JUST STARTED").append(agent.getAID());
                System.out.println(sb.toString());

                Agencia.printLog("START THE SERVICE TO CLONE THE SECUREAGENT",
                                 Level.INFO,true,this.getClass().getName());

                /*
                    CREATE A NEW VERTICALCOMMAND TO PERFORM THE OPPERATION THAT I NEED IT,
                    ACCORDING TO THE DOCUMENTATION.
                */

                GenericCommand command = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION,
                                                            SecureInterTPMHelper.NAME,null);

                //CREATE A NEW PACKET WITH THE INFORMATION THAT I CONSIDER GOOF TO KNOW
                RequestAttestation paquet = new RequestAttestation(agent.getAID(),agent.here(),destiny,agent.getAMS());
                command.addParam(paquet);

                //SEND THE VERTICAL COMMAND, IN ORDER TO CATCH IT IN THE SPECIFIC SERVICE
                Agencia.printLog("AGENT REQUEST CLONATION AND SEND THE REQUEST TO THE AGENCY",
                                 Level.INFO,true,this.getClass().getName());

                //SUBMMIT THE VERTICALCOMMAND
                try{
                    System.out.println("THE VERTICAL COMMAND TO CLONE IS CORRECTLY SUBMITED");
                    SecureInterTPMService.this.submit(command);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else {
                sb.append("ITS NOT POSSIBLE TO START THE CLONATION PROCESS  ").append(agent.getAID())
                          .append(" BUSSY TPM HANDLER OTHER REQUEST");

                Agencia.printLog("ITS NOT POSSIBLE TO START THE CLONATION PROCESS " +
                                " BUSSY TPM HANDLER OTHER REQUEST", Level.INFO,true,this.getClass().getName());

                //CALL THE CLONE ERROR TO PRINT THE MESSAGE
                agent.doSecureCloneError(sb.toString());
            }
        }
    }

    /**
     *This class consume the vertical commands that i wanted to send
     */
    private class CommandSourceSink implements Sink {

        @Override
        public void consume(VerticalCommand command){
            try{
                String commandName = command.getName();
                if(commandName.equals(SecureInterTPMHelper.REQUEST_ATTESTATION)){
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
                }else{
                    System.out.println("juaas");
                    Agent a = request_move_list.get(0).getSecureAgent();
                    Location b = request_move_list.get(0).getLocation();
                    System.out.println(a);
                    System.out.println(b.getID());
                    System.out.println(b.getName());
                    System.out.println(b.getAddress());
                    a.doMove(b);
                }
            }catch(Exception e){
                command.setReturnValue(new Exception("THERE ARE AN ERROR IN THE SERVICE, " +
                        "REJECT THE REQUEST OPERATION"));
            }
        }
    }


    /**
     *This class consume the vertical commands that i receive
     */
    private class CommandTargetSink implements Sink {

        @Override
        public void consume(VerticalCommand command) {
            try{
                String CommandName = command.getName();
                if(CommandName.equals(SecureInterTPMHelper.REQUEST_ATTESTATION)){
                    //At this point process the request attestation that i send in the other container
                    System.out.println("PROCESSING THE VERTICAL COMMAND ATTESTATION INTO THE DESTINATION CONTAINER");
                    RequestAttestation pack = (RequestAttestation) command.getParams()[0];
                    //CREATING THE NEW VERTICAL COMMAND
                    GenericCommand newCommand = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION,
                                                                   SecureInterTPMHelper.NAME, null);

                    //ADD THE PACK
                    newCommand.addParam(pack);

                    //CREATE THE PACKET ACL TO SEND
                    System.out.println("ESTOY ANTES DE DEFINIR EL AMS");
                    AID amsMain = new AID("ams",false);
                    System.out.println("ESTOY DESPUES DE DEFINIR EL AMS");
                    Agent amsMainPlatform = actualcontainer.acquireLocalAgent(amsMain);
                    System.out.println("ESTOY DESPUES DE DEFINIR EL ACQUIRE DEL CONTENEDOR");
                    ACLMessage message = new ACLMessage(ACLMessage.REQUEST);


                    amsMainPlatform.addBehaviour(
                            //new SenderACL(message, pack, amsMainPlatform, SecureInterTPMService.this)
                            new SenderACL(message,pack,amsMainPlatform,SecureInterTPMService.this)
                    );
                    System.out.println("ESTOY DESPUES DE DEFINIR EL COMPORTAMIENTO");
                    actualcontainer.releaseLocalAgent(amsMain);
                    System.out.println("ESTOY DESPUES DE LIBERAR LA PLATAFORMA");

                }else if(CommandName.equals(SecureInterTPMHelper.REQUEST_CONFIRMATION)){
                    System.out.println("MANO GIRADA");
                    long endTime = System.nanoTime();
                    long timeElapsed = endTime - startTime;
                    if ((timeElapsed/1000000) <= Agencia.getTimeout()){
                        if(AgentContainer.MAIN_CONTAINER_NAME.equals(actualcontainer.getID().getName())){
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
                        }else{
                            if(request_move_list.size()>0){
                                //EXECUTE THE MODIFIED FUNCTION TO MOVE
                                SecureAgent security = request_move_list.get(0).getSecureAgent();
                                security.doMove(request_move_list.get(0).getLocation());
                                request_move_list.clear();
                            }else if(request_clone_list.size()>0){
                                //EXECUTE THE MODIFIED FUNCTION TO CLONE
                                SecureAgent security = request_clone_list.get(0).getSecureAgent();
                                security.doClone(request_clone_list.get(0).getLocation(),
                                        request_clone_list.get(0).getNewName());
                                request_clone_list.clear();
                            }
                        }
                    }else{
                        System.out.println("SISTEM TIMED OUT IN ORDER TO RECEIVE THE ACK");
                        throw new ServiceException("TIMEOUT IN ORDER TO RECEIVE THE ACK");
                    }
                }
            }catch(Exception ex){
                System.out.println("AN ERROR HAPPENED WHEN RUNNING THE SERVICE");
                ex.printStackTrace();
            }
        }
    }


    /**
     *This class consume the horizotal commands that i receive
     */
    private class ServiceComponent implements Slice {

        @Override
        public Service getService() {
            return SecureInterTPMService.this;
        }

        @Override
        public Node getNode() throws ServiceException {
            try{
                return SecureInterTPMService.this.getLocalNode();
            }catch(Exception e){
                throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE SERVICE COMPONENT");
            }
        }

        @Override
        public VerticalCommand serve(HorizontalCommand command) {
            GenericCommand commandResponse = null;
            try{
                String commandReceive = command.getName();
                if(commandReceive.equals(SecureInterTPMSlice.REMOTE_REQUEST_ATTESTATION)){
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND ATTESTATION IN THE SERVICE COMPONENT");
                    //Fetch the packet that i send
                    RequestAttestation pack = (RequestAttestation) command.getParams()[0];
                    //Transform the horizontal command into a vertical command
                    commandResponse = new GenericCommand(SecureInterTPMHelper.REQUEST_ATTESTATION, SecureInterTPMHelper.NAME,null);
                    commandResponse.addParam(pack);
                }else if(commandReceive.equals(SecureInterTPMSlice.REMOTE_REQUEST_CONFIRMATION)){
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CONFIRMATION IN THE SERVICE COMPONENT");
                    //Fetch the packet that i send
                    RequestConfirmation pack = (RequestConfirmation) command.getParams()[0];
                    //Transform the horizontal command into a vertical command
                    commandResponse = new GenericCommand(SecureInterTPMHelper.REQUEST_CONFIRMATION, SecureInterTPMHelper.NAME,null);
                    commandResponse.addParam(pack);
                }
            }catch(Exception e){
                System.out.println("AN ERROR HAPPENED WHEN PROCESS THE VERTICAL COMMAND IN THE SERVICECOMPONENT");
                e.printStackTrace();
            }
            return commandResponse;
        }
    }


}
