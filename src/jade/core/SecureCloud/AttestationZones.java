package jade.core.SecureCloud;

import java.io.Serializable;

public class AttestationZones implements Serializable {

    private String Challengue;
    byte [] SecretInformation;

    public AttestationZones(String chall,byte [] secret){
        Challengue=chall;
        SecretInformation=secret;
    }

    public String getChallengue(){
        return Challengue;
    }

    public byte[] getSecretInformation() {
        return SecretInformation;
    }
}
