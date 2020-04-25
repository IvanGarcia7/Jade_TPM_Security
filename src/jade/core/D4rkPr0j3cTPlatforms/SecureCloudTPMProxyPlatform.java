package jade.core.D4rkPr0j3cTPlatforms;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.Service;
import jade.core.VerticalCommand;

public class SecureCloudTPMProxyPlatform extends Service.SliceProxy implements SecureCloudTPMSlicePlatform {

    @Override
    public void doCommunicateAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlicePlatform.REMOTE_REQUEST_START,
                    SecureCloudTPMHelperPlatform.NAME, null);
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
