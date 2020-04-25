package jade.core.SecureTPM;

import jade.core.Location;
import java.io.Serializable;
import java.util.Stack;

public class SecureOnionMessageBroadcast implements Serializable {

    private Location destiny;
    private Stack HostDir;

    public SecureOnionMessageBroadcast(Location dest, Stack keydir){
        destiny = dest;
        HostDir = keydir;
    }

    public Location getDestiny(){
        return destiny;
    }

    public Stack getHostdir(){
        return HostDir;
    }

}
