package jade.core.SecureTPM;

import jade.core.Location;

public class KeyStorage {

    /**
     * THIS CLASS, REPRESENT AN OBJECT THAT WILL BE SAVE IN THE KEYSTORAGE DIR,
     * IN ORDER TO FIND THE PATH TO THE DESTINY.
     */

    private byte [] certificate = null;
    private Location address = null;

    public KeyStorage(byte [] my_certificate, Location myLocation){
        certificate = my_certificate;
        address = myLocation;
    }

    public void setCertificate(byte [] certification){
        certificate = certification;
    }

    public void setLocation(Location myAddress){
        address = myAddress;
    }

    public byte [] getCertificate(){
        return certificate;
    }

    public Location getLocation(){
        return address;
    }
}
