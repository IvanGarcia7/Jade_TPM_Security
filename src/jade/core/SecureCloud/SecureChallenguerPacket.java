package jade.core.SecureCloud;

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

    public void setOTPPriv(byte[] OTPPriv) {
        this.OTPPriv = OTPPriv;
    }

    public void setOTPPub(byte[] OTPPub) {
        this.OTPPub = OTPPub;
    }

    public void setPartPriv(byte[] partPriv) {
        this.partPriv = partPriv;
    }

    public void setPartPublic(byte[] partPublic) {
        this.partPublic = partPublic;
    }
}
