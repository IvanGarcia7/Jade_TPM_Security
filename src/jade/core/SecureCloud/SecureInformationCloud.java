package jade.core.SecureCloud;

import jade.core.PlatformID;

import java.security.PublicKey;

public class SecureInformationCloud {

    private PublicKey KeyPub;
    private String sha256;
    private byte [] AIK;
    private PlatformID platformLocation;

    public SecureInformationCloud(PublicKey key, String hash,byte [] serialAIK, PlatformID destiny){
        KeyPub=key;
        sha256=hash;
        AIK=serialAIK;
        platformLocation = destiny;
    }

    public String getSha256() {
        return sha256;
    }

    public PublicKey getKeyPub() {
        return KeyPub;
    }

    public byte[] getAIK() {
        return AIK;
    }

    public PlatformID getPlatformLocation() {
        return platformLocation;
    }
}
