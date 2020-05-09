package jade.core.SecureCloud;

import jade.core.Location;

import java.io.Serializable;

public class SecretInformation implements Serializable {

    private Location destiny;
    private long Timestamp;
    private String Challengue;
    private Location origin;
    private int validation;

    public SecretInformation(Location dest, long time, String chall, Location or,int val){
        destiny = dest;
        Timestamp = time;
        Challengue = chall;
        origin = or;
        validation = val;
    }

    public String getChallengue() {
        return Challengue;
    }

    public Location getDestiny() {
        return destiny;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public Location getOrigin() {
        return origin;
    }

    public int getValidation() {
        return validation;
    }
}
