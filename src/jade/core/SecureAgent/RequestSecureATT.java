package jade.core.SecureAgent;


import jade.core.AID;
import jade.core.Location;
import jade.core.PlatformID;
import java.io.Serializable;
import java.security.PublicKey;


public class RequestSecureATT implements Serializable {


    private String contextEK;
    private String contextAK;
    private PublicKey publicPassword;
    private PlatformID PlatformLocation;
    private PlatformID PlatformCALocation;
    private PlatformID PlatformCALocationDestiny;
    private AttestationSerialized PCR_Signed;
    private AID Agente;


    public RequestSecureATT(PublicKey publicP, PlatformID locationP, String contextek, String contextak){
        publicPassword  = publicP;
        PlatformCALocation = locationP;
        contextEK = contextek;
        contextAK = contextak;
    }


    public RequestSecureATT(PublicKey publicP, PlatformID locationP){
        publicPassword  = publicP;
        PlatformCALocation = locationP;
    }

    public RequestSecureATT(PlatformID origin, PlatformID destiny,AID agent){
        PlatformLocation = origin;
        PlatformCALocationDestiny=destiny;
        Agente=agent;
    }


    public RequestSecureATT(PublicKey PlatformKey, PlatformID PlatformLoc, AttestationSerialized PCR){
        publicPassword  = PlatformKey;
        PlatformLocation = PlatformLoc;
        PCR_Signed = PCR;
    }


    public RequestSecureATT(PublicKey publicCA, PlatformID CAPlatform, PlatformID destiny, PlatformID myLoc,
                            AID agent){
        publicPassword  = publicCA;
        PlatformCALocation = CAPlatform;
        PlatformCALocationDestiny=destiny;
        PlatformLocation = myLoc;
        Agente = agent;
    }


    //GETTER METHODS

    public PublicKey getPublicPassword(){ return publicPassword; }
    public String getContextEK(){ return contextEK; }
    public String getContextAK(){ return contextAK; }
    public PlatformID getPlatformCALocation() { return PlatformCALocation; }
    public AttestationSerialized getPCR_Signed(){ return PCR_Signed; }
    public PlatformID getPlatformLocationOrigin(){ return PlatformLocation; }
    public PlatformID getPlatformCALocationDestiny(){ return PlatformCALocationDestiny; }
    public AID getAgente(){ return Agente; }

    //SETTER METHODS

    public void setPlatformLocation(PlatformID location){ PlatformLocation=location; }

}
