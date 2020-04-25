package jade.core.SecureOnionTPM;

import jade.core.Location;
import jade.core.PlatformID;
import jade.core.SecureTPM.KeyStorage;
import jade.core.SecureTPM.SecureAgent;
import jade.core.ServiceHelper;
import jade.core.mobility.Movable;

import java.util.List;

public interface SecureOnionTPMHelper extends ServiceHelper {

    /**
     * THE NAME OF THE SERVICE
     */
    public static final String NAME = "jade.core.SecureOnionTPM.SecureOnionTPM";


    /**
     * VERTICAL COMMANDS
     */
    static final String REQUEST_ADDRESS = "ADDRESS_REQUEST";
    static final String HOPPER_ADDRESS = "ADDRESS_HOPPER";
    static final String CONFIRM_ADDRESS = "ADDRESS_CONFIRM";
    public static final boolean DEBUG = true;

    /**
     * PUBLIC METHODS
     */
    void registerMovable(Movable movable);
    void sendAMSHostpots(SecureAgent secureAgent,List<PlatformID> device_list);
    void sendOnionHostpots(SecureAgent agent,Location destiny);
}
