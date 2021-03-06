package jade.core.SecureCloud;


import jade.core.ServiceHelper;

import javax.swing.*;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface SecureCloudTPMHelper extends ServiceHelper {


    //NAME OF THE SERVICE
    public static final String NAME = "jade.core.SecureCloud.SecureCloudTPM";


    //VERTICAL COMMANDS
    static final String REQUEST_START           = "START_REQUEST";
    static final String REQUEST_LIST            = "LIST_REQUEST";
    static final String REQUEST_ACCEPT       = "ACCEPT_LIST_REQUEST";
    static final String REQUEST_DELETE      = "DELETE_LIST_REQUEST";
    static final String REQUEST_INSERT_PLATFORM = "INSERT_REQUEST";
    static final String REQUEST_ACCEPT_PLATFORM = "ACCEPT_REQUEST";
    static final String REQUEST_PACK_PLATFORM   = "PACK_REQUEST";
    static final String REQUEST_LIST_ACCEPTED = "LIST_ACCEPTED";
    static final String REQUEST_MIGRATE_PLATFORM   = "MIGRATE_REQUEST";
    static final String REQUEST_MIGRATE_ZONE1_PLATFORM   = "MIGRATE_ZONE1_REQUEST";
    static final String REQUEST_MIGRATE_ZONE2_PLATFORM   = "MIGRATE_ZONE2_REQUEST";
    static final String REQUEST_MIGRATE_ZONE3_PLATFORM   = "MIGRATE_ZONE3_REQUEST";
    static final String REQUEST_MIGRATE_ZONE4_PLATFORM   = "MIGRATE_ZONE4_REQUEST";
    static final String REQUEST_VALIDATE_HASH   = "VALIDATE_HASH";
    static final String REQUEST_DELETE_HASH   = "DELETE_HASH";
    static final String REQUEST_ERROR   = "REQUEST_ERROR";

    public static final boolean DEBUG = true;


    //PUBLIC METHODS
    void doStartCloud(SecureCAPlatform secureCAPlatform, PrivateKey priv, PublicKey pub, String username, String password,
                      JTextArea STARTAREA, JTextArea PRINTERAREA, JTextArea CRUDAREA,
                      JTextArea INFORMATIONAREA, JButton Start);
    void listPlatforms(SecureCAPlatform secureCAPlatform);
    void doAcceptCloud(SecureCAPlatform secureCAPlatform, String index);
    void doDeleteCloud(SecureCAPlatform secureCAPlatform, String index);
    void listAcceptedPlatforms(SecureCAPlatform secureCAPlatform);
    void doValidateHash(SecureCAPlatform secureCAPlatform, String hash);
    void doDeleteHash(SecureCAPlatform secureCAPlatform, String hash);

    PrivateKey getPrivKey();
}
