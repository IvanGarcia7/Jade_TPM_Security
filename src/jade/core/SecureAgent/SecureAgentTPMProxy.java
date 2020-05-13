package jade.core.SecureAgent;


import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.SecureTPM.Agencia;
import jade.core.Service;
import jade.core.VerticalCommand;
import java.util.logging.Level;


/**
 * THE SecureAgentTPMProxy, TRY TO SERVE AN HORIZONTAL COMMAND, ROUTING IT TO A REMOTE SLICE IMPLEMENTATION,
 * LIKE FOR EXAMPLE, THE SLICE OF THE SECURE PLATFORM OR ANOTHER PLATFORM BEFORE THE MIGRATION BEGINS.
 */
public class SecureAgentTPMProxy extends Service.SliceProxy implements SecureAgentTPMSlice {


    /**
     * doCommunicateAMS IS USED TO SEND THE START INFORMATION TO THE AMS OF THE CURRENT PLATFORM.
     * @param command
     */
    @Override
    public void doCommunicateAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY",
                              Level.INFO,SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_START,
                                                           SecureAgentTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                              n.getName(), Level.INFO,SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * doMigrateAMS IS USED TO SEND THE REQUEST MIGRATE INFORMATION TO THE SECURE PLATFORM.
     * @param command
     */
    public  void doMigrateAMS(VerticalCommand command){
        try{
            Agencia.printLog("PROCEED TO DO THE MIGRATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY",
                              Level.INFO,SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM,
                                                           SecureAgentTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST MIGRATION THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                              n.getName(), Level.INFO,SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * doAttestateOrginAMS IS USED TO ATTESTATE THE ORIGIN PLATFORM ACCORDING TO A CHALLENGE SENT BY THE SECURE PLATFORM.
     * @param command
     */
    public void doAttestateOrginAMS(VerticalCommand command){
        try{
            Agencia.printLog("PROCEED TO DO THE ATTESTATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY",
                              Level.INFO,SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM,
                                                           SecureAgentTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST MIGRATION THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                              n.getName(), Level.INFO,SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * doMigrateHostpotAMS IS USED TO ATTESTATE THE DESTINY PLATFORM ACCORDING TO A CHALLENGE SENT BY THE SECURE
     * PLATFORM.
     * @param command
     */
    public void doMigrateHostpotAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE MIGRATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY",
                              Level.INFO,SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE2_PLATFORM,
                                                           SecureAgentTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE CONFIRMATION TO MIGRATE THROUGH A HORIZONTAL COMMAND TO THE " +
                              "AMS NODE "+ n.getName(), Level.INFO,SecureAgentTPMHelper.DEBUG,
                              this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * doMoveFinal IS USED TO START THE MIGRATION ACCORDING TO THE SERVER RESPONSE.
     * @param command
     */
    @Override
    public void doMoveFinal(GenericCommand command) {
        try{
            Agencia.printLog("PROCEED TO MOVE THE AGENT", Level.INFO,SecureAgentTPMHelper.DEBUG,
                    this.getClass().getName());
            GenericCommand newCommand2 = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_START,
                    SecureAgentTPMHelper.NAME, null);
            newCommand2.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE ACTION TO MIGRATE THROUGH A HORIZONTAL COMMAND TO THE AMS "+
                    "NODE "+ n.getName(), Level.INFO,SecureAgentTPMHelper.DEBUG, this.getClass().getName());
            n.accept(command);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void doMoveFinalMigration(GenericCommand command) {
        try{
            Agencia.printLog("PROCEED TO MOVE THE AGENT", Level.INFO,SecureAgentTPMHelper.DEBUG,
                    this.getClass().getName());
            GenericCommand newCommand2 = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_DO_MIGRATION,
                    SecureAgentTPMHelper.NAME, null);
            newCommand2.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE ACTION TO MIGRATE THROUGH A HORIZONTAL COMMAND TO THE AMS "+
                    "NODE "+ n.getName(), Level.INFO,SecureAgentTPMHelper.DEBUG, this.getClass().getName());
            n.accept(command);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
