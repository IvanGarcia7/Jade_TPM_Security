package jade.core.D4rkPr0j3cT;

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
