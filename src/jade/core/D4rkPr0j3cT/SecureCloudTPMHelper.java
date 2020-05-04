package jade.core.D4rkPr0j3cT;

import jade.core.ServiceHelper;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface SecureCloudTPMHelper extends ServiceHelper {

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
    static final String REQUEST_MIGRATE_PLATFORM   = "MIGRATE_REQUEST";
    static final String REQUEST_MIGRATE_ZONE1_PLATFORM   = "MIGRATE_ZONE1_REQUEST";
    static final String REQUEST_MIGRATE_ZONE2_PLATFORM   = "MIGRATE_ZONE2_REQUEST";
    static final String REQUEST_ERROR   = "REQUEST_ERROR";
    public static final boolean DEBUG = true;


    /**
     * PUBLIC METHODS
     */
    void doStartCloud(SecureCAPlatform secureCAPlatform, PrivateKey priv, PublicKey pub);
    void listPlatforms(SecureCAPlatform secureCAPlatform);
    void doAcceptCloud(SecureCAPlatform secureCAPlatform, byte[] index);
}
