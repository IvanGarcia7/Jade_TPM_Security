package jade.core.SecureOnionTPM;

import jade.core.GenericCommand;
import jade.core.Service;
import jade.core.VerticalCommand;

public interface SecureOnionTPMSlice extends Service.Slice {

    /**
     * HORIZONTAL COMMANDS THAT I DEFINE TO THIS SERVICE.
     */
    static final String REMOTE_ADDRESS_REQUEST = "1";
    static final String REMOTE_ADDRESS_CONFIRM = "2";

    void doCommunicateAMS(VerticalCommand command);

    /**
     * PUBLIC METHODS THAT SEND THE VERTICAL COMMAND TO THE PROXY.
     */

}
