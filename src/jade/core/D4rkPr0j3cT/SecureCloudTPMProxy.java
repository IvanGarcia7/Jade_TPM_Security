package jade.core.D4rkPr0j3cT;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.Service;
import jade.core.VerticalCommand;


import java.security.PrivateKey;
import java.security.PublicKey;

public class SecureCloudTPMProxy extends Service.SliceProxy implements SecureCloudTPMSlice {

    @Override
    public void doCommunicateAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_START,
                    SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doRequestListAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_LIST,
                    SecureCloudTPMHelper.NAME, null);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doInsertHostpotAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY TO" +
                               "REGISTER A NEW HOSTPOT");
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_INSERT_PLATFORM,
                    SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doStartAttestationHostpotAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY TO" +
                    "START THE ATTESTATION PROCESS");
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM,
                    SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doCheckAttestationHostpotoRIGIN(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE check origin hostpot WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY TO" +
                    "START THE ATTESTATION PROCESS");
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM,
                    SecureCloudTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            newCommand.addParam(command.getParams()[1]);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
