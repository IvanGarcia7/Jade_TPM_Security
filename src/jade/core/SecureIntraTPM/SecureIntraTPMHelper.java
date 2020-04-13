package jade.core.SecureIntraTPM;

import jade.core.Location;
import jade.core.SecureTPM.SecureAgent;
import jade.core.ServiceHelper;
import jade.core.mobility.Movable;

public interface SecureIntraTPMHelper extends ServiceHelper {

    public static final String NAME = "jade.core.SecureIntraTPM.SecureIntraTPM";

    static final String REQUEST_ATTESTATION = "REMOTE_ATT_ATTESTATION";
    static final String REQUEST_CONFIRMATION = "REMOTE_ATT_CONFIRMATION";
    public static final boolean DEBUG = true;

    //METHODS THAT I CREATED
    void doMoveTPM(SecureAgent agent, Location destiny);
    void doCloneTPM(SecureAgent agent, Location destiny, String nameAgent);

}
