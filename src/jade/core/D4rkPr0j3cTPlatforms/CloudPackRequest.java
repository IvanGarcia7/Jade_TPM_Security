package jade.core.D4rkPr0j3cTPlatforms;

import jade.core.Location;

import java.io.Serializable;

public class CloudPackRequest implements Serializable {

    private byte [] publicPasswordPlatform;
    private Location locationPlatform;
    private byte [] publicPasswordAgent;
    private Location locationAgent;

    public CloudPackRequest(byte [] publicP, Location locationP, byte [] publicAgent, Location locAgent){
        publicPasswordPlatform = publicP;
        locationPlatform = locationP;
        publicPasswordAgent = publicAgent;
        locationAgent = locAgent;
    }

    public byte [] getpublicPasswordAgent(){
        return publicPasswordAgent;
    }

    public Location getlocationAgent(){
        return locationAgent;
    }

}




