package jade.core.SecureIntraTPM;

import jade.core.*;
import jade.core.SecureTPM.*;
import jade.core.mobility.Movable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SecureIntraTPMService extends BaseService {

    //INTERESTING VARIABLES.
    public static final String NAME = "jade.core.SecureIntraTPM.SecureIntraTPM";
    public static final String VERBOSE = "jade_core_SecureIntraTPMS_SecureIntraTPMService_verbose";

    //TIME VAR THAT THE AGENCY USE TO SEE THE TIME THAT A REQUEST IT'S TAKEN.
    long startTime = System.nanoTime();

    //LIST OF PETITIONS.
    private List<RequestMove> request_move_list = new ArrayList<RequestMove>();
    private List<RequestClone> request_clone_list = new ArrayList<RequestClone>();

    //DEFINE THE SLICER AND THE HELPER TO EXECUTE CORRECTLY THE SERVICE.
    private Service.Slice actualSlicer=new ServiceComponent();
    private ServiceHelper thisHelper = new SecureIntraTPMServiceHelperImpl();

    //DEFINE INPUT AND OUTPUT FILTERS.
    private Filter OutFilter = null;
    private Filter InFilter = null;

    //DEFINE INPUT AND OUTPUT SINKS.
    private Sink OutputSink=new CommandSourceSink();
    private Sink InputSink=new CommandTargetSink();

    //REQUEST THE COMMANDS THAT I HAVE IMPLEMENTED AND SAVE INTO A LIST.
    private String [] actualCommands = new String [] {
            SecureIntraTPMHelper.REQUEST_ATTESTATION,
            SecureIntraTPMHelper.REQUEST_CONFIRMATION
    };

    //PERFORMATIVE PRINTER.
    private boolean verbose = false;

    //ACTUAL CONTAINER THO REFERENCE IT IN THE SERVICE.
    private AgentContainer actualcontainer;

    /**
     * THIS FUNCTION GET THE NAME OF THE ACTUAL SERVICE.
     * @return
     */
    @Override
    public String getName() {
        return SecureIntraTPMHelper.NAME;
    }


    /**
     * THIS FUNCTION INIT THE SERVICE, CALLING THE SUPER METHODS THAT CONTAINS THE BASE SERVICE.
     * @param agentcontainer
     * @param prof
     * @throws ProfileException
     */
    public void init(AgentContainer agentcontainer, Profile prof) throws ProfileException{
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
        System.out.println("SECURE INTRA-TPM SERVICE STARTED CORRECTLY ON CONTAINER CALLED: "+actualcontainer.getID());
    }

    /**
     * THIS FUNCTION RETRIEVE THE SERVICE HELPER.
     * @param agent
     * @return
     * @throws ServiceException
     */
    public ServiceHelper getHelper(Agent agent) throws ServiceException{
        if(agent instanceof SecureAgent){
            return thisHelper;
        }else{
            throw new ServiceException("THIS SERVICE IS NOT ALLOWED TO RUN IN THIS AGENT");
        }
    }

    /**
     * FUNCTION TO GET THE SLICE TO EXECUTE HORIZONTAL COMMANDS.
     * @return
     */
    public Class getHorizontalInterface(){
        return SecureIntraTPMSlice.class;
    }

    /**
     * FUNCTION TO GET THE ACTUAL SLICER.
     * @return
     */
    public Service.Slice getLocalSlice(){
        return actualSlicer;
    }

    /**
     * FUNCTION TO GET THE COMMANDS THAT I IMPLEMENTED FOR THIS SERVICE IN THE HELPER.
     * @return
     */
    public String [] getOwnedCommands() {
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
        }
        else {
            return InFilter;
        }
    }

    /**
     * RETRIEVE THE SINKS IF IT WAS NECESSARY
     * @param side
     * @return
     */
    public Sink getCommandSink(boolean side) {
        if(side == Sink.COMMAND_SOURCE) {
            return OutputSink;
        }
        else {
            return InputSink;
        }
    }

    /**
     * THUS FUNCTION SEE ITH THE TPM IS WORKING ON ANOTHER REQUEST.
     * RETURN TRUE IF THE TPM IS IN USE.
     */
    public boolean isBlocked(){
        return (request_clone_list.size()+request_move_list.size()) > 0;
    }


    public class SecureIntraTPMServiceHelperImpl implements SecureIntraTPMHelper{

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
            IN BOTH OF THEM.
        */

        /**
         * THIS METHOD TAKE AN AGENT, AND THE LOCATION REQUESTED TO MOVE.
         * FIRST OF ALL, REVIEW IF THE TPM IS FREE TO HANDLER WITH THE OPERATION THAT NEED IT
         * AND THEN INITIALIZE THE PROCESS
         * @param agent
         * @param destiny
         */
        public synchronized void doMoveTPM(SecureAgent agent, Location destiny){
            StringBuilder sb = new StringBuilder();
            if (!isBlocked()) {
                request_move_list.add(new RequestMove(agent,destiny));
                System.out.println("THE REQUEST HAS BEEN SAVED IN THE SERVICE");
                sb.append("-> THE PROCCES TO MOVE THE AGENT HAS JUST STARTED").append(agent.getAID());
                System.out.println(sb.toString());
                Agencia.printLog("START THE SERVICE TO MOVE THE SECUREAGENT",
                                  Level.INFO,true,this.getClass().getName());
                System.out.println("CREATE A NEW VERTICAL ATTESTATION COMMAND TO PERFORM THE OPERATION THAT " +
                                   "THE SERVICE NEED TO PERFORM");
                GenericCommand command = new GenericCommand(SecureIntraTPMHelper.REQUEST_ATTESTATION,
                                                            SecureIntraTPMHelper.NAME,null);
                System.out.println("CREATING THE PACKET WITH THE INFORMATION THAT I WANT TO SEND FROM MY NODE");
                RequestAttestation pack = new RequestAttestation(agent.getAID(),agent.here(),destiny,agent.getAMS());
                command.addParam(pack);
                Agencia.printLog("AGENT REQUEST MOVE AND SEND THE REQUEST TO THE AGENCY",
                                  Level.INFO,true,this.getClass().getName());
                try{
                    System.out.println("-> THE VERTICAL COMMAND TO MOVE IS CORRECTLY SUBMITTED");
                    SecureIntraTPMService.this.submit(command);
                }catch(Exception e){
                    System.out.println("FATAL ERROR, DO_MOVE_TPM CAN BE PERFORMED, TRY IT LATER");
                    e.printStackTrace();
                }
            }
            else {
                sb.append("ITS NOT POSSIBLE TO START THE MOVE PROCESS  ").append(agent.getAID())
                        .append(" BUSSY TPM HANDLER OTHER REQUEST");
                System.out.println(sb.toString());
                Agencia.printLog("ITS NOT POSSIBLE TO START THE MOVE PROCESS " +
                                 "BUSSY TPM HANDLER OTHER REQUEST", Level.INFO,true,this.getClass().getName());
                agent.doSecureMoveError(sb.toString());
            }
        }


        /**
         * THIS METHOD TAKE AN AGENT, AND THE LOCATION REQUESTED TO MOVE.
         * FIRST OF ALL, REVIEW IF THE TPM IS FREE TO HANDLER WITH THE OPPERATION NEEDIT
         * AND THEN INICIALIZE THE PROCESS.
         * @param agent
         * @param destiny
         */
        public synchronized void doCloneTPM(SecureAgent agent, Location destiny, String nameAgent){
            StringBuilder sb = new StringBuilder();
            if (!isBlocked()) {
                request_clone_list.add(new RequestClone(agent,destiny,nameAgent));
                System.out.println("THE REQUEST HAS BEEN SAVED IN THE SERVICE");
                sb.append("-> THE PROCCES TO CLONE THE AGENT HAS JUST STARTED").append(agent.getAID());
                System.out.println(sb.toString());
                Agencia.printLog("START THE SERVICE TO CLONE THE SECUREAGENT",
                        Level.INFO,true,this.getClass().getName());
                System.out.println("CREATE A NEW VERTICAL ATTESTATION COMMAND TO PERFORM THE OPERATION THAT " +
                                   "THE SERVICE NEED TO PERFORM");
                GenericCommand command = new GenericCommand(SecureIntraTPMHelper.REQUEST_ATTESTATION,
                                                            SecureIntraTPMHelper.NAME,null);
                System.out.println("CREATING THE PACKET WITH THE INFORMATION THAT I WANT TO SEND FROM MY NODE");
                RequestAttestation paquet = new RequestAttestation(agent.getAID(),agent.here(),destiny,agent.getAMS());
                command.addParam(paquet);
                Agencia.printLog("AGENT REQUEST CLONATION AND SEND THE REQUEST TO THE AGENCY",
                                 Level.INFO,true,this.getClass().getName());
                try{
                    System.out.println("-> THE VERTICAL COMMAND TO CLONE IS CORRECTLY SUBMITTED");
                    SecureIntraTPMService.this.submit(command);
                }catch(Exception e){
                    System.out.println("FATAL ERROR, DO_CLONE_TPM CAN BE PERFORMED, TRY IT LATER");
                    e.printStackTrace();
                }
            }
            else {
                sb.append("ITS NOT POSSIBLE TO START THE CLONATION PROCESS  ").append(agent.getAID())
                          .append(" BUSSY TPM HANDLER OTHER REQUEST");
                System.out.println(sb.toString());
                Agencia.printLog("ITS NOT POSSIBLE TO START THE CLONATION PROCESS " +
                                " BUSSY TPM HANDLER OTHER REQUEST", Level.INFO,true,this.getClass().getName());
                agent.doSecureCloneError(sb.toString());
            }
        }
    }

    /**
     *THE COMMAND SOURCE SINK CONSUME THE VERTICAL COMMAND THAT I WANTED TO SEND.
     */
    private class CommandSourceSink implements Sink {

        @Override
        public void consume(VerticalCommand command){
            try{
                String commandName = command.getName();
                if(commandName.equals(SecureIntraTPMHelper.REQUEST_ATTESTATION)){
                    System.out.println("-> VERTICAL COMMAND CATCH BY CONSUME SOURCE SINK");
                    Object[] params = command.getParams();
                    RequestAttestation pack = (RequestAttestation) params[0];
                    SecureIntraTPMSlice obj = (SecureIntraTPMSlice) getSlice(pack.getOriginLocation().getName());
                    try {
                        System.out.println("SENDING THE INFORMATION TO THE SLICE");
                        obj.doRequestAttestation(command);
                        startTime = System.nanoTime();
                    } catch (Exception ie) {
                        ie.printStackTrace();
                        Agencia.setStatus(false);
                    }
                }
            }catch(Exception e){
                command.setReturnValue(new Exception("THERE ARE AN ERROR IN THE SERVICE, " +
                                                     "REJECT THE REQUEST OPERATION"));
            }
        }
    }


    /**
     *THE COMMAND TARGET SINK PROCESS THE VERTICAL COMMAND THAT  I WANTED TO RECEIVE.
     */
    private class CommandTargetSink implements Sink {

        @Override
        public void consume(VerticalCommand command) {
            try{
                String CommandName = command.getName();
                if(CommandName.equals(SecureIntraTPMHelper.REQUEST_ATTESTATION)){
                    System.out.println("PROCESSING THE VERTICAL COMMAND ATTESTATION INTO THE DESTINATION CONTAINER");
                    RequestAttestation pack = (RequestAttestation) command.getParams()[0];
                    GenericCommand newCommand = new GenericCommand(SecureIntraTPMHelper.REQUEST_ATTESTATION,
                                                                   SecureIntraTPMHelper.NAME, null);
                    System.out.println("CREATING THE CONFIRMATION PACKET WITH THE INFORMATION THAT I WANT TO " +
                                       "SEND FROM MY NODE");
                    RequestConfirmation new_pack = new RequestConfirmation(pack.getNameAgent(),
                                                                          pack.getDestinyLocation(),
                                                                          pack.getOriginLocation(),pack.getAMSName());
                    newCommand.addParam(new_pack);
                    SecureIntraTPMSlice newSlice = (SecureIntraTPMSlice)getSlice(pack.getOriginLocation().getName());
                    try {
                        System.out.println("SENDING THE VERTICAL COMMAND");
                        newSlice.doRequestConfirmation(newCommand);
                    }
                    catch(Exception ex) {
                        newSlice = (SecureIntraTPMSlice) getFreshSlice(pack.getOriginLocation().getName());
                        newSlice.doRequestConfirmation(newCommand);
                    }
                }else if(CommandName.equals(SecureIntraTPMHelper.REQUEST_CONFIRMATION)){
                    System.out.println("PROCESSING THE VERTICAL COMMAND CONFIRMATION INTO THE DESTINATION CONTAINER");
                    long endTime = System.nanoTime();
                    long timeElapsed = endTime - startTime;
                    System.out.println("THE TIME OBTAINED IS THE FOLLOWING "+timeElapsed);
                    if ((timeElapsed/1000000) <= Agencia.getTimeout()){
                        System.out.println("PROCESSING THE VERTICAL COMMAND CONFIRMATION INTO THE " +
                                           "DESTINATION CONTAINER");
                        RequestConfirmation pack = (RequestConfirmation) command.getParams()[0];
                        if(request_move_list.size()>0){
                            SecureAgent security = request_move_list.get(0).getSecureAgent();
                            System.out.println("MOVING INTRA PLATFORM MOBILITY");
                            security.doMove(request_move_list.get(0).getLocation());
                            request_move_list.clear();
                        }else if(request_clone_list.size()>0){
                            SecureAgent security = request_clone_list.get(0).getSecureAgent();
                            System.out.println("CLONING INTRA PLATFORM MOBILITY");
                            security.doClone(request_clone_list.get(0).getLocation(),
                                             request_clone_list.get(0).getNewName());
                            request_clone_list.clear();
                        }
                    }else{
                        System.out.println("SYSTEM TIMED OUT IN ORDER TO RECEIVE THE ACK");
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
     *THIS CLASS CONSUME THE HORIZONTAL COMMAND THAT I RECEIVE.
     */
    private class ServiceComponent implements Service.Slice {

        @Override
        public Service getService() {
            return SecureIntraTPMService.this;
        }

        @Override
        public Node getNode() throws ServiceException {
            try{
                return SecureIntraTPMService.this.getLocalNode();
            }catch(Exception e){
                throw new ServiceException("AN ERROR HAPPENED WHEN RUNNING THE SERVICE COMPONENT");
            }
        }

        @Override
        public VerticalCommand serve(HorizontalCommand command) {
            GenericCommand commandResponse = null;
            try{
                String commandReceive = command.getName();
                if(commandReceive.equals(SecureIntraTPMSlice.REMOTE_REQUEST_ATTESTATION)){
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND ATTESTATION IN THE " +
                                       "SERVICE COMPONENT");
                    System.out.println("CONSUME THE HORIZONTAL COMMAND FROM THE PLATFORM DESTINATION THAT I HAVE " +
                                       "RECEIVED FROM ATTESTATION");
                    RequestAttestation pack = (RequestAttestation) command.getParams()[0];
                    System.out.println("CREATING HIS RESPECTIVE VERTICAL COMMAND");
                    commandResponse = new GenericCommand(SecureIntraTPMHelper.REQUEST_ATTESTATION,
                                                         SecureIntraTPMHelper.NAME,null);
                    commandResponse.addParam(pack);
                }else if(commandReceive.equals(SecureIntraTPMSlice.REMOTE_REQUEST_CONFIRMATION)){
                    System.out.println("+*-> I HAVE RECEIVED A HORIZONTAL COMMAND CONFIRMATION IN THE " +
                                       "SERVICE COMPONENT");
                    System.out.println("CONSUME THE HORIZONTAL COMMAND FROM THE PLATFORM DESTINATION THAT I HAVE " +
                            "RECEIVED FROM CONFIRMATION");
                    RequestConfirmation pack = (RequestConfirmation) command.getParams()[0];
                    System.out.println("CREATING HIS RESPECTIVE VERTICAL COMMAND");
                    commandResponse = new GenericCommand(SecureIntraTPMHelper.REQUEST_CONFIRMATION,
                                                         SecureIntraTPMHelper.NAME,null);
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
