package jade.core.D4rkPr0j3cT;

import jade.core.Location;

import java.security.PublicKey;

public class SecureInformationCloud {

    private PublicKey KeyPub;
    private String sha256;
    private byte [] AIK;
    private Location platformLocation;

    public SecureInformationCloud(PublicKey key, String hash,byte [] serialAIK, Location destiny){
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

    public Location getPlatformLocation() {
        return platformLocation;
    }
}
