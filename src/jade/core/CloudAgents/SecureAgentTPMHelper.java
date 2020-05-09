package jade.core.CloudAgents;


import jade.core.PlatformID;
import jade.core.ServiceHelper;
import java.security.PublicKey;


public interface SecureAgentTPMHelper extends ServiceHelper {

    //NAME OF THE SERVICE
    public static final String NAME = "jade.core.CloudAgents.SecureAgentTPM";

    //VERTICAL COMMANDS

    static final String REQUEST_START           = "START_REQUEST";
    static final String REQUEST_LIST            = "LIST_REQUEST";
    static final String REQUEST_INSERT_PLATFORM = "INSERT_REQUEST";
    static final String REQUEST_ACCEPT_PLATFORM = "ACCEPT_REQUEST";
    static final String REQUEST_PACK_PLATFORM   = "PACK_REQUEST";
    static final String REQUEST_MIGRATE_PLATFORM   = "MIGRATE_REQUEST";
    static final String REQUEST_MIGRATE_ZONE1_PLATFORM   = "MIGRATE_ZONE1_REQUEST";
    static final String REQUEST_MIGRATE_ZONE2_PLATFORM   = "MIGRATE_ZONE2_REQUEST";
    static final String REQUEST_ERROR   = "REQUEST_ERROR";
    static final String REQUEST_MOVE = "REQUEST_MOVE";

    public static final boolean DEBUG = true;


    //PUBLIC METHODS

    void doStartCloudAgent(SecureAgentPlatform secureAgentPlatform, PlatformID caLocation, PublicKey pubKey,
                           String contextEK, String contextAK);
    void doStartMigration(SecureAgentPlatform secureAgentPlatform, PlatformID destiny);

}
