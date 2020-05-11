package jade.core.SecureCloud;


import jade.core.Location;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;

import java.io.Serializable;


public class PrivateInformationCA implements Serializable {


    private PlatformID destiny;
    private long Timestamp;
    private String Challenge;
    private PlatformID Origin;
    private int validation;
    private SecureAgentPlatform Agent;


    public PrivateInformationCA(PlatformID dest, long time, String challenge, PlatformID origin, int bitStatus,
                                SecureAgentPlatform agent){
        destiny = dest;
        Timestamp = time;
        Challenge = challenge;
        Origin = origin;
        validation = bitStatus;
        Agent = agent;
    }


    //GETTER METHODS
    public String getChallenge() {
        return Challenge;
    }
    public PlatformID getDestiny() {
        return destiny;
    }
    public long getTimestamp() {
        return Timestamp;
    }
    public PlatformID getOrigin() {
        return Origin;
    }
    public int getValidation() {
        return validation;
    }
    public SecureAgentPlatform getAgent() { return Agent; }

}
