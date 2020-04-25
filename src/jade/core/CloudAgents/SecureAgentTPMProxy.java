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
}
