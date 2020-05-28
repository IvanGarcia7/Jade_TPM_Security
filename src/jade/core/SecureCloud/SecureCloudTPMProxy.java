package jade.core.SecureCloud;


import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.SecureTPM.Agencia;
import jade.core.Service;
import jade.core.VerticalCommand;

import java.util.logging.Level;


/**
 * THE SecureCloudTPMProxy, TRY TO SERVE AN HORIZONTAL COMMAND, ROUTING IT TO A REMOTE SLICE IMPLEMENTATION,
 * LIKE FOR EXAMPLE, THE SLICE OF OTHER PLATFORMS.
 */
public class SecureCloudTPMProxy extends Service.SliceProxy implements SecureCloudTPMSlice {

    /**
     *doCommunicateAMS IS USED TO COMMUNICATE WITH THE AMS OF THE MAIN PLATFORM, TO START THE CA
     * @param command
     */
    @Override
    public void doCommunicateAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY",
                    Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_START,
                                                           SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            newCommand.addParam(command.getParams()[1]);
            newCommand.addParam(command.getParams()[2]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST COMMUNICATION THROUGH A HORIZONTAL COMMAND TO THE AMS " +
                            "NODE "+ n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     *doRequestListAMS IS USED TO REQUEST THE LIST OF PENDINGS PLATFORMS THAT HAD BEEN INSERTED PREVIOUSLY
     * @param command
     */
    @Override
    public void doRequestListAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE REQUEST LIST IN THE PROXY", Level.INFO,
                            SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_LIST,
                                                           SecureCloudTPMHelper.NAME, null);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE LIST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                            n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     *doRequestAcceptAMS IS USED TO ACCEPT ONE OF THE PENDING PLATFORMS, ACCORDING TO AN ADDRESS PROVIDED
     * @param command
     */
    @Override
    public void doRequestAcceptAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE REQUEST ACCEPT WITH THE AMS OF THE MAIN PLATFORM IN THE " +
                            "PROXY", Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_ACCEPT,
                    SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST ACCEPT THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                            n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void doRequestAcceptListAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE REQUEST ACCEPT WITH THE AMS OF THE MAIN PLATFORM IN THE " +
                    "PROXY", Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_LIST_ACCEPTED,
                    SecureCloudTPMHelper.NAME, null);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST ACCEPT THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doRequestDeleteAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE REQUEST DELETE WITH THE AMS OF THE MAIN PLATFORM IN THE " +
                    "PROXY", Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_DELETE,
                    SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST DELETE THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     *doInsertHostpotAMS IS USED TO INSERT A NEW HOTSPOT INTO THE SECURE PLATFORMS
     * @param command
     */
    @Override
    public void doInsertHostpotAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY" +
                    " TO INSERT A NEW HOTSPOT", Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_INSERT_PLATFORM,
                                                           SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST INSERT THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                            n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     *doStartAttestationHostpotAMS IS USED TO START THE ATTESTATION PROCESS
     * @param command
     */
    @Override
    public void doStartAttestationHostpotAMS(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE " +
                            "PROXY TO START THE ATTESTATION PROCESS", Level.INFO, SecureCloudTPMHelper.DEBUG,
                            this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM,
                                                           SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST START ATTESTATION THROUGH A HORIZONTAL COMMAND TO THE " +
                            "AMS NODE "+n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     *doCheckAttestationHostpotoRIGIN IS USED TO CHECK THE FILES THAT THE SECURE PLATFORM RECEIVES FROM OTHERS
     * @param command
     */
    @Override
    public void doCheckAttestationHostpotORIGIN(VerticalCommand command) {
        try{
            Agencia.printLog("PROCEED TO DO THE CHECK PLATFORM WITH THE AMS OF THE MAIN PLATFORM IN THE " +
                            "PROXY TO START THE MIGRATION PROCESS", Level.INFO, SecureCloudTPMHelper.DEBUG,
                            this.getClass().getName());
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM,
                                                           SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            newCommand.addParam(command.getParams()[1]);
            Node n = getNode();
            Agencia.printLog("-> SENDING THE REQUEST CHECK ATTESTATION THROUGH A HORIZONTAL COMMAND TO THE " +
                    "AMS NODE "+n.getName(), Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
