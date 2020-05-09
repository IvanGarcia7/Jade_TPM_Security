package jade.core.SecureCloud;

import java.io.Serializable;

public class KeyPairCloud implements Serializable {

    private byte [] publicPassword;
    private byte [] privatePassword;

    public KeyPairCloud(byte [] publicP, byte [] privateP){
        publicPassword  = publicP;
        privatePassword = privateP;
    }

    public byte [] getPublicPassword(){
        return publicPassword;
    }

    public byte [] getPrivatePassword(){
        return privatePassword;
    }
}
