package jade.core.SecureIntraTPM;

import jade.core.Location;
import jade.core.SecureTPM.SecureAgent;
import jade.core.ServiceHelper;
import jade.core.mobility.Movable;

public interface SecureIntraTPMHelper extends ServiceHelper {

    public static final boolean DEBUG = true;

    /**
     * NAME OF THE SERVICE
     */
    public static final String NAME = "jade.core.SecureIntraTPM.SecureIntraTPM";

    /**
     * VERTICAL COMMANDS THAT THROW THE SERVICE IF IT HAS REQUIRED
     */
    static final String REQUEST_ATTESTATION = "REMOTE_ATT_ATTESTATION";
    static final String REQUEST_CONFIRMATION = "REMOTE_ATT_CONFIRMATION";

    /**
     * PUBLIC METHODS THAT IMPLEMENTS THE SERVICE
     */
    void doMoveTPM(SecureAgent agent, Location destiny);
    void doCloneTPM(SecureAgent agent, Location destiny, String nameAgent);

    void registerMovable(Movable movable);
}
