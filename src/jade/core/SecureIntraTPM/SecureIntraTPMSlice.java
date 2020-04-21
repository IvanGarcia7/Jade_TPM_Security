package jade.core.SecureIntraTPM;

import jade.core.*;

public interface SecureIntraTPMSlice extends Service.Slice {

    /**
     * HORIZONTAL COMMANDS THAT I DEFINE TO THIS SERVICE.
     */
    static final String REMOTE_REQUEST_ATTESTATION = "1";
    static final String REMOTE_REQUEST_CONFIRMATION = "2";

    /**
     * PUBLIC METHODS THAT SEND THE VERTICAL COMMAND TO THE PROXY.
     * @param newCommand
     */
    void doRequestConfirmation(VerticalCommand newCommand);
    void doRequestAttestation(VerticalCommand command);
}
