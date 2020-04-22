package jade.core.SecureOnionTPM;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.Service;
import jade.core.VerticalCommand;

public class SecureOnionTPMProxy extends Service.SliceProxy implements SecureOnionTPMSlice {


    public void doCommunicateAMS(VerticalCommand command){
        try{
            System.out.println("PROCEED TO DO THE COMMUNICATION WITH THE AMS OF THE MAIN PLATFORM IN THE PROXY");
            GenericCommand newCommand = new GenericCommand(SecureOnionTPMSlice.REMOTE_ADDRESS_REQUEST,
                                                           SecureOnionTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE REQUEST COMMUNICATION THROUGH A HORIZONTAL COMMAND TO THE AMS NODE "+
                    n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }




}
