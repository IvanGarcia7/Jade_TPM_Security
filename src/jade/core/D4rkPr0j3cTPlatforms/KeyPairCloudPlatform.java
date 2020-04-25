package jade.core.D4rkPr0j3cTPlatforms;

import jade.core.Location;

import java.io.Serializable;

public class KeyPairCloudPlatform implements Serializable {

    private byte [] publicPassword;
    private Location locationPlatform;

    public KeyPairCloudPlatform(byte [] publicP, Location locationP){
        publicPassword  = publicP;
        locationPlatform = locationP;
    }

    public byte [] getPublicPassword(){
        return publicPassword;
    }

    public Location getLocationPlatform(){
        return locationPlatform;
    }
}
