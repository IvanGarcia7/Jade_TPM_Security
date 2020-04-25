package jade.core.D4rkPr0j3cT;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.Service;
import jade.core.VerticalCommand;

public class SecureCloudTPMProxy extends Service.SliceProxy implements SecureCloudTPMSlice {

    @Override
    public void doCommunicateAMS(VerticalCommand command) {
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureCloudTPMSlice.REMOTE_REQUEST_START,
                    SecureCloudTPMHelper.NAME, null);
            KeyPairCloud keyPack = (KeyPairCloud) command.getParams()[0];
            newCommand.addParam(keyPack);
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

}
