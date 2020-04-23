package jade.core.SecureTPM;

import jade.core.PlatformID;

public class OnionPacketOrigin {

    private String originContainer;
    private PlatformID destiny;

    public OnionPacketOrigin(String or, PlatformID dest){
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

    public PlatformID getPlatformDestiny(){
        return destiny;
    }
}
