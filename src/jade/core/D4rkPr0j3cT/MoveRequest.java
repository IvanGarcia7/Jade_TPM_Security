package jade.core.D4rkPr0j3cT;

import jade.core.Location;

public class MoveRequest {

    private Location origen;
    private Location destiny;
    private String challengue;
    private long time;

    public MoveRequest(Location o, Location d, String c, long tim){
        origen = o;
        destiny = d;
        challengue = c;
        time = tim;
    }

    public Location getDestiny() {
        return destiny;
    }

    public Location getOrigen() {
        return origen;
    }

    public String getChallengue() {
        return challengue;
    }

    public long getTime(){
        return time;
    }
}
