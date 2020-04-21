package jade.core.SecureIntraTPM;

import jade.core.*;

import java.util.logging.Level;

public class SecureIntraTPMProxy extends Service.SliceProxy implements SecureIntraTPMSlice {

    @Override
    public void doRequestConfirmation(GenericCommand command) {
        try {
            System.out.println("PROCEED TO DO REQUEST CONFIRMATION");
            GenericCommand newCommand = new GenericCommand(SecureIntraTPMSlice.REMOTE_REQUEST_CONFIRMATION,
                                                     SecureIntraTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("SENDING THE HORIZONTAL COMMAND TO THE NODE: "+n.getName());
            Object result = n.accept(newCommand);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doRequestAttestation(VerticalCommand command) {
        try{
            System.out.println("-> PROCEED TO DO REQUEST ATTESTATION");
            GenericCommand newCommand = new GenericCommand(SecureIntraTPMSlice.REMOTE_REQUEST_ATTESTATION,
                                                           SecureIntraTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node nodo = getNode();
            System.out.println("-> SENDING THE ATTESTATION REQUEST THROUGH A HORIZONTAL COMMAND TO THE NODE "+nodo.getName());
            nodo.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
