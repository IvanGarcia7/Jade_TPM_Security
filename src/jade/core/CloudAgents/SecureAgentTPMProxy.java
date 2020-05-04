package jade.core.CloudAgents;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.Service;
import jade.core.VerticalCommand;

public class SecureAgentTPMProxy extends Service.SliceProxy implements SecureAgentTPMSlice {

    @Override
    public void doCommunicateAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_START,
                    SecureAgentTPMHelper.NAME, null);
            KeyPairCloudPlatform keyPack = (KeyPairCloudPlatform) command.getParams()[0];
            newCommand.addParam(keyPack);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST START THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public  void doMigrateAMS(VerticalCommand command){
        try{
            System.out.println("PROCEED TO DO THE MIGRATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_PLATFORM,
                    SecureAgentTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST MIGRATION THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void doAttestateOrginAMS(VerticalCommand command){
        try{
            System.out.println("PROCEED TO DO THE ATTESTATE ZONE 1 WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM,
                    SecureAgentTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST MIGRATION THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doMigrateHostpotAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE MIGRATE ZONE 2 WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_MIGRATE_ZONE2_PLATFORM,
                    SecureAgentTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE CONFIRMATION TO MIGRATE THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doMoveFinal(GenericCommand newCommand) {
        try{
            System.out.println("PROCEED MOVE THE AGENT");
            GenericCommand newCommand2 = new GenericCommand(SecureAgentTPMSlice.REMOTE_REQUEST_START,
                    SecureAgentTPMHelper.NAME, null);
            newCommand2.addParam(newCommand.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE CONFIRMATION TO MIGRATE THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
