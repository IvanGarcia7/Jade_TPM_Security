package jade.core.SecureTPM;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TPMHighLevel {
    public static void main(String [] args){
        init_platform("paco","0x81010009","0x8101000a");
        attestation_files("paco","0x8101000a","abc123");
        check_attestation_files("paco","abc123");
    }

    public static int init_platform(String path,String contextEK,String contextAK){
        int valuereturn = 0;
        System.out.println("EXECUTING THE INIT FUNCTION: ");
        new File(path).mkdirs();
        String akpub_path = path+"/akpub.pem";

        //FLUSH TPM NVRAM CONTENT TO SAVE THE NEW EK KEY
        ProcessBuilder pb0 = new ProcessBuilder(
                new String[]{
                        "tpm2_evictcontrol",
                        "-c", contextEK
                }
        );

        //CREATE A NEW EK KEY AND SAVE INTO THE TPM
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_createek",
                        "-c", contextEK
                }
        );

        //CREATE A NEW AK KEY AND SAVE INTO A TPM
        ProcessBuilder pb2 = new ProcessBuilder(
                new String[]{
                        "tpm2_createak",
                        "-C", contextEK,
                        "-c", contextAK,
                        "-G", "rsa",
                        "-s", "rsassa",
                        "-g", "sha256",
                        "-u", akpub_path,
                        "-f", "pem",
                        "-n", "ak.name"
                }
        );

        try{
            Process p0 = pb0.start();
            p0.waitFor();
            pb1.redirectErrorStream(true);
            pb2.redirectErrorStream(true);
            Process p1 = pb1.start();
            Process p2 = pb2.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            String line;
            while((line=reader.readLine())!=null){
                output.append(line+"\n");
            }
            valuereturn = p1.waitFor();
            System.out.println(output);
            if(valuereturn==0){
                reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                while((line=reader.readLine())!=null){
                    output.append(line+"\n");
                }
                valuereturn = p2.waitFor();
                System.out.println(output);
            }
        }catch(Exception e){
            System.out.println("There are an error in the process");
            valuereturn = 1;
        }
        return valuereturn;
    }


    public static int attestation_files(String path,String context,String nonce){
        int valuereturn = 0;
        System.out.println("EXECUTING THE SALIDA FUNCTION: ");
        new File(path).mkdirs();
        String sing_path = path+"/sign.out";
        String pcr_path = path+"/pcr.out";
        String quote_path = path+"/quote.out";
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_quote",
                        "-c", context,
                        "-l", "sha256:15,16,22",
                        "-q", nonce,
                        "-m", quote_path,
                        "-s", sing_path,
                        "-o", pcr_path,
                        "-g", "sha256"
                }
        );
        try{
            pb1.redirectErrorStream(true);
            Process p = pb1.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while((line=reader.readLine())!=null){
                output.append(line+"\n");
            }
            int exitVal = p.waitFor();
            System.out.println(output);
            System.out.println(exitVal);
        }catch(Exception e){
            System.out.println("There are an error in the process");
            valuereturn = 1;
        }
        return valuereturn;
    }

    public static int check_attestation_files(String name,String nonce){
        int valuereturn = 0;
        String sing_path = "/home/pi/Agentes/"+name+"/"+name+"_sign.out";
        String pcr_path = "/home/pi/Agentes/"+name+"/"+name+"_pcr.out";
        String quote_path = "/home/pi/Agentes/"+name+"/"+name+"_quote.out";
        System.out.println("EXECUTING THE CHECK FUNCTION: ");
        new File("/home/pi/Agentes/"+name).mkdirs();
        String akpub_path = "/home/pi/Agentes/"+name+"/"+name+"_akpub.pem";
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_checkquote",
                        "-u", akpub_path,
                        "-m", quote_path,
                        "-s", sing_path,
                        "-f", pcr_path,
                        "-g", "sha256",
                        "-q", nonce
                }
        );
        try{
            pb1.redirectErrorStream(true);
            Process p = pb1.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while((line=reader.readLine())!=null){
                output.append(line+"\n");
            }
            int exitVal = p.waitFor();
            System.out.println(output);
            System.out.println(exitVal);
        }catch(Exception e){
            System.out.println("There are an error in the process");
            valuereturn = 1;
        }
        return valuereturn;
    }
}

