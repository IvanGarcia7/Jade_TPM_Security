package jade.core.D4rkPr0j3cTPlatforms;

import jade.core.Location;
import jade.core.ServiceHelper;

public interface SecureCloudTPMHelperPlatform extends ServiceHelper {

    /**
     * THE NAME OF THE SERVICE
     */
    public static final String NAME = "jade.core.D4rkPr0j3cT.SecureCloudTPM";

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
    void doStartCloudAgent(SecureCAPlatformAgent secureCAPlatformAgent, Location caLocation, byte[] pubKey);
}
