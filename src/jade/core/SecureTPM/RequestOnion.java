package jade.core.SecureTPM;

import jade.core.PlatformID;
import javafx.application.Platform;

import java.io.Serializable;
import java.util.List;

public class RequestOnion implements Serializable {

    private PlatformID destiny;
    private List<PlatformID> hostpots_devices;

    public  RequestOnion(PlatformID platform, List<PlatformID> platform_host) {
        platform = destiny;
        hostpots_devices = platform_host;
    }

    public PlatformID getPlatformID(){
        return destiny;
    }

    public List<PlatformID> getListHost(){
        return hostpots_devices;
    }

    public void setPlatformID(PlatformID config_platform){
        destiny = config_platform;
    }

    public void setHostpots(List<PlatformID> platforms){
        hostpots_devices = platforms;
    }
}
