package jade.core.D4rkPr0j3cT;

import java.security.PublicKey;

public class SecureInformationCloud {

    private PublicKey KeyPub;
    private String sha256;
    private byte [] AIK;

    public SecureInformationCloud(PublicKey key, String hash,byte [] serialAIK){
        KeyPub=key;
        sha256=hash;
        AIK=serialAIK;
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
}
