package jade.core.SecureIntraTPM;

import jade.core.*;

public interface SecureIntraTPMSlice extends Service.Slice {
    static final String REMOTE_REQUEST_ATTESTATION = "1";
    static final String REMOTE_REQUEST_CONFIRMATION = "2";

    void doRequestConfirmation(GenericCommand newCommand);
    void doRequestAttestation(VerticalCommand command);
}
