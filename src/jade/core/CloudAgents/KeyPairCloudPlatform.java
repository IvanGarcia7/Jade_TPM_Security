package jade.core.CloudAgents;

import jade.core.Location;

import java.io.Serializable;
import java.security.PublicKey;

public class KeyPairCloudPlatform implements Serializable {

    private PublicKey publicPassword;
    private Location locationPlatform;
    private String contextEK;
    private String contextAK;
    private Location locationDestiny;
    private Location myLocation;

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

    public KeyPairCloudPlatform(PublicKey publicP, Location locationP, Location destiny, Location myLoc){
        publicPassword  = publicP;
        locationPlatform = locationP;
        locationDestiny=destiny;
        myLocation = myLoc;
    }

    public KeyPairCloudPlatform(Location myLoc, Location destiny){
        myLocation=myLoc;
        locationDestiny=destiny;
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

    public Location getLocationDestiny(){
        return locationDestiny;
    }

    public Location getMyLocation(){
        return myLocation;
    }
}
