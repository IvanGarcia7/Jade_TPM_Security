package jade.core.SecureTPM;

import jade.core.Location;

import javax.naming.Name;

public class RequestClone {

    private SecureAgent AgentTPM;
    private Location destiny;
    private String NameAgent;

    public RequestClone(SecureAgent agent, Location location, String NewName){
        AgentTPM = agent;
        destiny = location;
        NameAgent = NewName;
    }

    public SecureAgent getSecureAgent(){
        return AgentTPM;
    }

    public Location getLocation(){
        return destiny;
    }

    public String getNewName(){
        return NameAgent;
    }
}
