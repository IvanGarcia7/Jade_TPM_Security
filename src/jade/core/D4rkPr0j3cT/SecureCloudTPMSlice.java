package jade.core.D4rkPr0j3cT;

import jade.core.Service;
import jade.core.VerticalCommand;

public interface SecureCloudTPMSlice extends Service.Slice {

    /**
     * HORIZONTAL COMMANDS THAT I DEFINE TO THIS SERVICE.
     */
    static final String REMOTE_REQUEST_START           = "1";
    static final String REMOTE_REQUEST_LIST            = "2";
    static final String REMOTE_REQUEST_INSERT_PLATFORM = "3";
    static final String REMOTE_REQUEST_ACCEPT_PLATFORM = "4";
    static final String REMOTE_REQUEST_PACK_PLATFORM   = "5";
    static final String REMOTE_REQUEST_MIGRATE_PLATFORM   = "6";
    static final String REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM   = "7";
    static final String REMOTE_REQUEST_MIGRATE_ZONE2_PLATFORM   = "8";
    static final String REMOTE_REQUEST_ERROR   = "9";

    /**
     * PUBLIC METHODS THAT SEND THE VERTICAL COMMAND TO THE PROXY.
     */
    void doCommunicateAMS(VerticalCommand command);
    void doRequestListAMS(VerticalCommand command);
    void doInsertHostpotAMS(VerticalCommand command);
    void doStartAttestationHostpotAMS(VerticalCommand command);
    void doCheckAttestationHostpotoRIGIN(VerticalCommand command);
}
