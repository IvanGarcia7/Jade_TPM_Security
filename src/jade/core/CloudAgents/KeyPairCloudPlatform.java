package jade.core.CloudAgents;

import jade.core.Location;

import java.io.Serializable;
import java.security.PublicKey;

public class KeyPairCloudPlatform implements Serializable {

    private PublicKey publicPassword;
    private Location locationPlatform;

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
}
