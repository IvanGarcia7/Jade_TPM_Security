package jade.core.SecureIntraTPM;

import jade.core.*;


public class SecureIntraTPMProxy extends Service.SliceProxy implements SecureIntraTPMSlice {

    /**
     * THIS FUNCTION PROCESS A VERTICAL COMMAND, AND SEND IT TO THE RESPECTIVE COMPONENT TO PROCESS IT.
     * WHEN A AGENT TRY TO ATTESTATE THE DESTINY PLATFORM, THE PROCESS START WITH THIS METHOD.
     * @param command
     */
    @Override
    public void doRequestConfirmation(VerticalCommand command) {
        try {
            System.out.println("PROCEED TO DO REQUEST CONFIRMATION");
            GenericCommand newCommand = new GenericCommand(SecureIntraTPMSlice.REMOTE_REQUEST_CONFIRMATION,
                                                     SecureIntraTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE ATTESTATION REQUEST THROUGH A HORIZONTAL COMMAND TO " +
                               "THE NODE "+n.getName());
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
            GenericCommand newCommand = new GenericCommand(SecureIntraTPMSlice.REMOTE_REQUEST_ATTESTATION,
                                                           SecureIntraTPMHelper.NAME, null);
            newCommand.addParam(command.getParams()[0]);
            Node n = getNode();
            System.out.println("-> SENDING THE ATTESTATION REQUEST THROUGH A HORIZONTAL COMMAND TO " +
                               "THE NODE "+n.getName());
            n.accept(newCommand);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
