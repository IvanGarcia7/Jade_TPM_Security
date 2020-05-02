package jade.core.CloudAgents;

import jade.core.Location;

import java.io.Serializable;
import java.security.PublicKey;

public class KeyPairCloudPlatform implements Serializable {

    private PublicKey publicPassword;
    private Location locationPlatform;
    private String contextEK;
    private String contextAK;

    public KeyPairCloudPlatform(PublicKey publicP, Location locationP,String contextek, String contextak){
        publicPassword  = publicP;
        locationPlatform = locationP;
        contextEK = contextek;
        contextAK = contextak;
    }

    public KeyPairCloudPlatform(PublicKey publicP, Location locationP){
        publicPassword  = publicP;
        locationPlatform = locationP;
    }

    public PublicKey getPublicPassword(){
        return publicPassword;
    }

    public Location getLocationPlatform(){
        return locationPlatform;
    }

    public String getContextEK(){
        return contextEK;
    }

    public String getContextAK(){
        return contextAK;
    }
}
