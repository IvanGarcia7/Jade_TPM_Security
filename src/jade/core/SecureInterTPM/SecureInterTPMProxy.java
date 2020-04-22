package jade.core.SecureInterTPM;

import jade.core.GenericCommand;
import jade.core.Node;
import jade.core.Service;
import jade.core.VerticalCommand;

public class SecureInterTPMProxy extends Service.SliceProxy implements SecureInterTPMSlice {

    /**
     * THIS FUNCTION PROCESS A VERTICAL COMMAND, AND SEND IT TO THE RESPECTIVE COMPONENT TO PROCESS IT.
     * WHEN A AGENT TRY TO ATTESTATE THE DESTINY PLATFORM, THE PROCESS START WITH THIS METHOD.
     * @param command
     */
    @Override
    public void doRequestConfirmation(GenericCommand command) {
        try {
            System.out.println("PROCEED TO DO REQUEST CONFIRMATION");
            GenericCommand newCommand = new GenericCommand(SecureInterTPMSlice.REMOTE_REQUEST_CONFIRMATION,
                                                     SecureInterTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("SENDING THE HORIZONTAL COMMAND TO THE NODE: "+n.getName());
            Object result = n.accept(newCommand);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * THIS FUNCTION PROCESS A VERTICAL COMMAND, AND SEND IT TO THE RESPECTIVE COMPONENT TO PROCESS IT.
     * WHEN A AGENT TRY TO CONFIRM THE ATTESTATION MESSAGE, THE PROCESS START WITH THIS METHOD.
     * @param command
     */
    @Override
    public void doRequestAttestation(VerticalCommand command) {
        try{
            System.out.println("-> PROCEED TO DO REQUEST ATTESTATION");
            GenericCommand newCommand = new GenericCommand(SecureInterTPMSlice.REMOTE_REQUEST_ATTESTATION,
                                                           SecureInterTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node nodo = getNode();
            System.out.println("-> SENDING THE ATTESTATION REQUEST THROUGH A HORIZONTAL COMMAND TO THE NODE "+
                               nodo.getName());
            nodo.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
