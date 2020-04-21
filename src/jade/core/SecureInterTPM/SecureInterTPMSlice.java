package jade.core.SecureInterTPM;

import jade.core.GenericCommand;
import jade.core.Service;
import jade.core.VerticalCommand;

public interface SecureInterTPMSlice extends Service.Slice {
    static final String REMOTE_REQUEST_ATTESTATION = "1";
    static final String REMOTE_REQUEST_CONFIRMATION = "2";

    void doRequestConfirmation(GenericCommand newCommand);
    void doRequestAttestation(VerticalCommand command);
}
