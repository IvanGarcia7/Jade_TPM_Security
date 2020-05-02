package jade.core.CloudAgents;

import java.io.Serializable;

public class RequestSecure implements Serializable {

    private KeyPairCloudPlatform packetAgent = null;
    private AttestationSerialized packet_signed = null;

    public RequestSecure(KeyPairCloudPlatform kpc,AttestationSerialized as){
           packetAgent=kpc;
           packet_signed=as;
    }


    public KeyPairCloudPlatform getPacketAgent() {
        return packetAgent;
    }

    public AttestationSerialized getPacket_signed() {
        return packet_signed;
    }
}
