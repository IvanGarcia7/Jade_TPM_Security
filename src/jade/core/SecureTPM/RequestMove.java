package jade.core.SecureTPM;

import jade.core.Location;

public class RequestMove {

    private SecureAgent AgentTPM;
    private Location destiny;

    public RequestMove(SecureAgent agent, Location location){
        AgentTPM = agent;
        destiny = location;
    }

    public SecureAgent getSecureAgent(){
        return AgentTPM;
    }

    public Location getLocation(){
        return destiny;
    }

    public void setAgentTPM(SecureAgent agent) {
        AgentTPM = agent;
    }

    public void setLocation(Location destiny_location){
        destiny = destiny_location;
    }
}
