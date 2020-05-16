package jade.core.SecureAgent;


import jade.core.PlatformID;
import jade.core.SecureCloud.SecureCAConfirmation;
import jade.core.ServiceHelper;

import javax.swing.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;


public interface SecureAgentTPMHelper extends ServiceHelper {

    //NAME OF THE SERVICE
    public static final String NAME = "jade.core.SecureAgent.SecureAgentTPM";

    //VERTICAL COMMANDS

    static final String REQUEST_START           = "START_REQUEST";
    static final String REQUEST_LIST            = "LIST_REQUEST";
    static final String REQUEST_INSERT_PLATFORM = "INSERT_REQUEST";
    static final String REQUEST_ACCEPT_PLATFORM = "ACCEPT_REQUEST";
    static final String REQUEST_PACK_PLATFORM   = "PACK_REQUEST";
    static final String REQUEST_MIGRATE_PLATFORM   = "MIGRATE_REQUEST";
    static final String REQUEST_MIGRATE_ZONE1_PLATFORM   = "MIGRATE_ZONE1_REQUEST";
    static final String REQUEST_MIGRATE_ZONE2_PLATFORM   = "MIGRATE_ZONE2_REQUEST";
    static final String REQUEST_MIGRATE_ZONE3_PLATFORM   = "MIGRATE_ZONE3_REQUEST";
    static final String REQUEST_DO_MIGRATION   = "REQUEST_DO_MIGRATION";
    static final String REQUEST_ERROR   = "REQUEST_ERROR";
    static final String REQUEST_MOVE = "REQUEST_MOVE";

    public static final boolean DEBUG = true;


    //PUBLIC METHODS

    void doStartCloudAgent(SecureAgentPlatform secureAgentPlatform, PlatformID caLocation, PublicKey pubKey,
                           String contextEK, String contextAK);
    void doStartMigration(SecureAgentPlatform secureAgentPlatform, PlatformID destiny);
    PrivateKey getPrivKey();
    Map<String, SecureCAConfirmation> getPassList();
}
