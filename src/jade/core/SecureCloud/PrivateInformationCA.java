package jade.core.SecureCloud;

import jade.core.Location;

import java.io.Serializable;

public class PrivateInformationCA implements Serializable {


    private Location destiny;
    private long Timestamp;
    private String Challenge;
    private Location Origin;
    private int validation;


    public PrivateInformationCA(Location dest, long time, String challenge, Location origin, int bitStatus){
        destiny = dest;
        Timestamp = time;
        Challenge = challenge;
        Origin = origin;
        validation = bitStatus;
    }


    //GETTER METHODS
    public String getChallenge() {
        return Challenge;
    }
    public Location getDestiny() {
        return destiny;
    }
    public long getTimestamp() {
        return Timestamp;
    }
    public Location getOrigin() {
        return Origin;
    }
    public int getValidation() {
        return validation;
    }
}
