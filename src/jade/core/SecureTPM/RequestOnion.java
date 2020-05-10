package jade.core.SecureTPM;

import jade.core.PlatformID;
import javafx.application.Platform;

import java.io.Serializable;
import java.util.List;

public class RequestOnion implements Serializable {

    private PlatformID destiny;
    private List<PlatformID> hotspots_devices;

    public  RequestOnion(PlatformID platform, List<PlatformID> platform_host) {
        platform = destiny;
        hotspots_devices = platform_host;
    }

    public PlatformID getPlatformID(){
        return destiny;
    }

    public List<PlatformID> getListHost(){
        return hotspots_devices;
    }

    public void setPlatformID(PlatformID config_platform){
        destiny = config_platform;
    }

    public void setHotspots(List<PlatformID> platforms){
        hotspots_devices = platforms;
    }
}
