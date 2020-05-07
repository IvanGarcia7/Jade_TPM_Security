package jade.core.D4rkPr0j3cT;

import java.io.Serializable;

public class SecureChallenguerPacket implements Serializable {

    private byte [] OTPPub;
    private byte [] OTPPriv;
    private byte [] partPublic;
    private byte [] partPriv;

    public SecureChallenguerPacket(byte [] OTPprivate, byte [] OTPpublic, byte [] partpub, byte [] partpriv){
        OTPPub =OTPpublic;
        OTPPriv = OTPprivate;
        partPublic=partpub;
        partPriv=partpriv;
    }

    public byte[] getOTPPriv() {
        return OTPPriv;
    }

    public byte[] getOTPPub() {
        return OTPPub;
    }

    public byte[] getPartPriv() {
        return partPriv;
    }

    public byte[] getPartPublic() {
        return partPublic;
    }
}
