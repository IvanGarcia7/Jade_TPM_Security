package jade.core.SecureTPM;

import jade.core.Location;
import jade.core.PlatformID;

public class OnionPacketOrigin {

    private String originContainer;
    private Location destiny;

    public OnionPacketOrigin(String or, Location dest){
        originContainer=or;
        destiny=dest;
    }

    /**
     * GETTER METHODS IN ORDER TO RETRIEVE THE INITIAL INFORMATION TO START THE ONION PROXY
     * @return
     */
    public String getOriginContainer(){
        return originContainer;
    }

    public Location getPlatformDestiny(){
        return destiny;
    }
}
