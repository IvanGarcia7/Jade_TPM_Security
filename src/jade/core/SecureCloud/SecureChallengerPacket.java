package jade.core.SecureCloud;

import java.io.Serializable;

public class SecureChallengerPacket implements Serializable {


    private byte [] OTPPub;
    private byte [] OTPPriv;
    private byte [] partPublic;
    private byte [] partPriv;

    public SecureChallengerPacket(byte [] OTPprivate, byte [] OTPpublic, byte [] partpub, byte [] partpriv){
        OTPPub =OTPpublic;
        OTPPriv = OTPprivate;
        partPublic=partpub;
        partPriv=partpriv;
    }

    //GETTER METHODS
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

    //SETTER METHODS
    public void setOTPPriv(byte[] OTPPriv) { this.OTPPriv = OTPPriv; }
    public void setOTPPub(byte[] OTPPub) { this.OTPPub = OTPPub; }
    public void setPartPriv(byte[] partPriv) { this.partPriv = partPriv; }
    public void setPartPublic(byte[] partPublic) { this.partPublic = partPublic; }
}
