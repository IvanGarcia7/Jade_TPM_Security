package jade.core.CloudAgents;


import jade.core.Location;
import jade.core.PlatformID;
import java.io.Serializable;
import java.security.PublicKey;


public class RequestSecureATT implements Serializable {


    private Location locationPlatform;

    private Location myDestiny;
    private Location myLocation;
    private String contextEK;
    private String contextAK;
    private PublicKey publicPassword;
    private PlatformID PlatformLocation;
    private PlatformID PlatformCALocation;
    private AttestationSerialized PCR_Signed;


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


    public RequestSecureATT(PublicKey PlatformKey, PlatformID PlatformLoc, AttestationSerialized PCR){
        publicPassword  = PlatformKey;
        PlatformLocation = PlatformLoc;
        PCR_Signed = PCR;
    }

    public RequestSecureATT(Location myLoc, Location destiny){
        myLocation=myLoc;
        myDestiny=destiny;
    }


    public RequestSecureATT(PublicKey publicCA, PlatformID CAPlatform, Location destiny, Location myLoc){
        publicPassword  = publicCA;
        PlatformCALocation = CAPlatform;
        myDestiny=destiny;
        myLocation = myLoc;
    }


    //GETTER METHODS

    public PublicKey getPublicPassword(){ return publicPassword; }

    public Location getLocationPlatform(){ return locationPlatform; }

    public String getContextEK(){ return contextEK; }

    public String getContextAK(){ return contextAK; }

    public Location getLocationDestiny(){ return myDestiny; }

    public Location getMyLocation(){ return myLocation; }

    public PlatformID getPlatformCALocation() { return PlatformCALocation; }

    public AttestationSerialized getPCR_Signed(){ return PCR_Signed; }
}
