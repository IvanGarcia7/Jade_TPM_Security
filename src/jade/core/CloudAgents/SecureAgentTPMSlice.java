package jade.core.CloudAgents;

import jade.core.GenericCommand;
import jade.core.Service;
import jade.core.VerticalCommand;

public interface SecureAgentTPMSlice extends Service.Slice {

    //HORIZONTAL COMMANDS

    static final String REMOTE_REQUEST_START           = "1";
    static final String REMOTE_REQUEST_MIGRATE_PLATFORM   = "2";
    static final String REMOTE_REQUEST_MIGRATE_ZONE1_PLATFORM   = "3";
    static final String REMOTE_REQUEST_MIGRATE_ZONE2_PLATFORM   = "4";
    static final String REMOTE_REQUEST_ERROR   = "5";


   //PUBLIC METHODS

    void doCommunicateAMS(VerticalCommand command);
    void doMigrateAMS(VerticalCommand command);
    void doAttestateOrginAMS(VerticalCommand command);
    void doMigrateHostpotAMS(VerticalCommand command);
    void doMoveFinal(GenericCommand newCommand);
}
