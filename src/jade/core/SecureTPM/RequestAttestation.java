package jade.core.SecureTPM;

import jade.core.AID;
import jade.core.Location;
import jade.util.leap.Serializable;

public class RequestAttestation implements Serializable {

    private AID NameAgent;
    private AID NameAms;
    private Location OriginLocation;
    private Location DestinyLocation;
    private String Description;
    private long receivedTime;

    public RequestAttestation(AID agent, Location agent_origin_location, Location destiny_location, AID AMS_origin) {
        NameAgent = agent;
        NameAms = AMS_origin;
        OriginLocation = agent_origin_location;
        DestinyLocation = destiny_location;
    }

    public RequestAttestation(AID agent, Location agent_origin_location, Location destiny_location, AID AMS_origin, long time) {
        NameAgent = agent;
        NameAms = AMS_origin;
        OriginLocation = agent_origin_location;
        DestinyLocation = destiny_location;
        receivedTime = time;
    }


    //SETTER METHODS
    public void setNameAgent(AID newName){
        NameAgent=newName;
    }

    public void setNombreAms(AID newNameAMS){
        NameAms = newNameAMS;
    }

    public void setLocationorigen(Location new_location){
        OriginLocation = new_location;
    }

    public void setLocationdestino(Location new_location){
        DestinyLocation = new_location;
    }

    public void setDescripcion(String newDescription){
        Description = newDescription;
    }

    //GETTER METHODS
    public AID getNameAgent(){
        return NameAgent;
    }

    public Location getOriginLocation(){
        return OriginLocation;
    }

    public Location getDestinyLocation(){
        return DestinyLocation;
    }

    public String getDescripcion(){
        return Description;
    }

    public AID getAMSName(){
        return NameAms;
    }

    public long getTime(){
        return receivedTime;
    }

}
