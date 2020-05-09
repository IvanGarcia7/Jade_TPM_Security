package jade.core.SecureAgent;


import jade.core.SecureTPM.Agencia;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;


/**
 * THIS IS AN OBJECT THAT SAVE ALL THE TEMP FILES RELATED TO THE ATTESTATION. SERIALIZE ALL THE INFORMATION THAT
 * THE SECURE PLATFORM NEED IN ORDER TO CHECK IF THE PLATFORM IS RELIABLE ACCORDING TO ITS PCR VALUES.
 */
public class AttestationSerialized implements Serializable {

    private byte [] AIKPub;
    private byte [] Sign;
    private byte [] Quoted;
    private byte [] Message;


    /**
     * THIS FUNCTION RECEIVES A PATH AS A PARAM, AND THEM SERIALIZE:
     * - THE AIK PUB OF THE PLATFORM (KEY USE TO SIGN THE FILES) AKPUB.PEM
     * - THE SIGNATURE OF THE PCR VALUES SIGN.OUT
     * - THE QUOTE GENERATED WITH THE TPM QUOTE.OUT
     * - THE PLATDOEM PCR VALUES THAT HAVE BEEN VERIFIED.
     * @param path
     */
    public AttestationSerialized(String path){
        File aik = new File(path+"/akpub.pem");
        File sign = new File(path+"/sign.out");
        File quoted = new File(path+"/quote.out");
        File message = new File(path+"/pcr.out");
        try{
            AIKPub = Files.readAllBytes(aik.toPath());
            Sign = Files.readAllBytes(sign.toPath());
            Quoted = Files.readAllBytes(quoted.toPath());
            Message = Files.readAllBytes(message.toPath());
            File folderParent = new File(path);
            Agencia.deleteFolder(folderParent);
        }catch(Exception e){
            System.out.println("ERROR READING THE FILES TO INIT THE ATTESTATION");
            e.printStackTrace();
        }
    }


    //GETTER METHODS

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
