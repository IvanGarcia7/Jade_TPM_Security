package jade.core.SecureInterTPM;

import jade.core.GenericCommand;
import jade.core.Service;
import jade.core.VerticalCommand;

public interface SecureInterTPMSlice extends Service.Slice {

    /**
     * HORIZONTAL COMMANDS THAT I DEFINE TO THIS SERVICE.
     */
    static final String REMOTE_REQUEST_ATTESTATION = "1";
    static final String REMOTE_REQUEST_CONFIRMATION = "2";


    /**
     * PUBLIC METHODS THAT SEND THE VERTICAL COMMAND TO THE PROXY.
     * @param newCommand
     */
    void doRequestConfirmation(GenericCommand newCommand);
    void doRequestAttestation(VerticalCommand command);
}
