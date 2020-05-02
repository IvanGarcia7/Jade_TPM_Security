package jade.core.CloudAgents;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;

public class AttestationSerialized implements Serializable {

    private byte [] AIKPub;
    private byte [] Sign;
    private byte [] Quoted;
    private byte [] Message;

    public AttestationSerialized(String path){
        File aik = new File(path+"/akpub.pem");
        File sign = new File(path+"/sign.out");
        File quoted = new File(path+"/quote.out");
        File message = new File(path+"/pcr.out");
        try{
            AIKPub = Files.readAllBytes(aik.toPath());
            Sign = Files.readAllBytes(sign.toPath());
            sign.delete();
            Quoted = Files.readAllBytes(quoted.toPath());
            quoted.delete();
            Message = Files.readAllBytes(message.toPath());
            message.delete();
        }catch(Exception e){
            System.out.println("ERROR READING THE FILES TO INIT THE ATTESTATION");
        }
    }

    public byte[] getAIKPub() {
        return AIKPub;
    }

    public byte[] getMessage() {
        return Message;
    }

    public byte[] getQuoted() {
        return Quoted;
    }

    public byte[] getSign() {
        return Sign;
    }
}
