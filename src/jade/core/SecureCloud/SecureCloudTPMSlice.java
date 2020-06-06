package jade.core.SecureCloud;

import jade.core.Service;
import jade.core.VerticalCommand;

public interface SecureCloudTPMSlice extends Service.Slice {

    //HORIZONTAL COMMANDS

    static final String REMOTE_REQUEST_START           = "1";
    static final String REMOTE_REQUEST_LIST            = "2";
    static final String REMOTE_REQUEST_INSERT_PLATFORM = "3";
    static final String REMOTE_REQUEST_ACCEPT_PLATFORM = "4";
    static final String REMOTE_REQUEST_PACK_PLATFORM   = "5";
    static final String REMOTE_REQUEST_MIGRATE_PLATFORM   = "6";
    static final String REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM   = "7";
    static final String REMOTE_REQUEST_MIGRATE_ZONE2_PLATFORM   = "8";
    static final String REMOTE_REQUEST_MIGRATE_ZONE3_PLATFORM   = "9";
    static final String REMOTE_REQUEST_ERROR   = "10";
    static final String REMOTE_REQUEST_ACCEPT       = "11";
    static final String REMOTE_REQUEST_LIST_ACCEPTED = "12";
    static final String REMOTE_REQUEST_DELETE      = "13";
    static final String REMOTE_REQUEST_VALIDATE_HASH   = "14";
    static final String REMOTE_REQUEST_DELETE_HASH   = "15";


    //PUBLIC METHODS

    void doCommunicateAMS(VerticalCommand command);
    void doRequestListAMS(VerticalCommand command);
    void doRequestAcceptAMS(VerticalCommand command);
    void doInsertHostpotAMS(VerticalCommand command);
    void doStartAttestationHostpotAMS(VerticalCommand command);
    void doCheckAttestationHostpotORIGIN(VerticalCommand command);
    void doRequestAcceptListAMS(VerticalCommand command);
    void doRequestDeleteAMS(VerticalCommand command);
    void doValidateHashAMS(VerticalCommand command);
    void doDeleteHashAMS(VerticalCommand command);
}
