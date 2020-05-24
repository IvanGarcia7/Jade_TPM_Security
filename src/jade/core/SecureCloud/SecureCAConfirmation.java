package jade.core.SecureCloud;


import jade.core.AID;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.Date;


public class SecureCAConfirmation implements Serializable {

    private PublicKey DestinyPublic;
    private PlatformID DestinyPlatform;
    private String Token;
    private Date Timestamp;
    private AID Agent;

    public SecureCAConfirmation(PublicKey key, PlatformID destiny, String token, Date time, AID agent){
        DestinyPublic = key;
        DestinyPlatform = destiny;
        Token = token;
        Timestamp = time;
        Agent = agent;
    }

    //GETTER METHODS
    public PublicKey getDestinyPublic(){ return DestinyPublic; }
    public PlatformID getDestinyPlatform(){ return DestinyPlatform; }
    public String getToken(){ return Token; }
    public Date getTimestamp() { return Timestamp; }
    public AID getAgent() { return Agent; }

}
