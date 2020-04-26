package jade.core.CloudAgents;

import jade.core.Location;
import jade.core.ServiceHelper;

import java.security.PublicKey;

public interface SecureAgentTPMHelper extends ServiceHelper {

    /**
     * THE NAME OF THE SERVICE
     */
    public static final String NAME = "jade.core.CloudAgents.SecureAgentTPM";

    /**
     * VERTICAL COMMANDS
     */
    static final String REQUEST_START           = "START_REQUEST";
    static final String REQUEST_LIST            = "LIST_REQUEST";
    static final String REQUEST_INSERT_PLATFORM = "INSERT_REQUEST";
    static final String REQUEST_ACCEPT_PLATFORM = "ACCEPT_REQUEST";
    static final String REQUEST_PACK_PLATFORM   = "PACK_REQUEST";
    public static final boolean DEBUG = true;


    /**
     * PUBLIC METHODS
     */
    void doStartCloudAgent(SecureAgentPlatform secureAgentPlatform, Location caLocation, PublicKey pubKey);
}
