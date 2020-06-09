package jade.core.SecureTPM;


import jade.core.SecureAgent.AttestationSerialized;
import jade.core.SecureAgent.RequestSecureATT;


import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;




public class Agencia{

    //Variables definidas que puedan ser de utilidad
    //Variable utilizada como semaforo para evitar que se colapse devido a múltiples peticiones dentro del TPM
    public static boolean bloqueado = false;
    public static int time = 1000000000;

    public static boolean getStatusBloqueado(){
        return bloqueado;
    }

    public static void setStatus(boolean estado){
        bloqueado=estado;
    }



    /**
     * This function generate One time password to encrypt the data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey generateOTP() throws NoSuchAlgorithmException {
        KeyGenerator keyGenOTP = KeyGenerator.getInstance("AES");
        keyGenOTP.init(256); // for example
        SecretKey secretKey = keyGenOTP.generateKey();
        return secretKey;
    }


    /**
     * Transform Object into an array of bytes
     * @param obj
     * @return
     * @throws IOException
     */
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(output);
        os.writeObject(obj);
        return output.toByteArray();
    }

    /**
     * Transform array of bytes into an Object
     * @param data
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(input);
        return is.readObject();
    }

    public static void setTimeout(int i) {
        time = i;
    }

    public static int getTimeout(){
        return time;
    }

    public final static void printLog(String mensaje, Level nivel, boolean detalles, String clase) {
        java.util.logging.Logger logger = Logger.getLogger(clase);
        if ((logger.isLoggable(nivel))&&(detalles)) {
            logger.logp(nivel,clase,null,mensaje);
        }
    }

    public static PrivateKey privateKeyPlatform = null;
    public static PublicKey publicKeyPlatform = null;

    public static void genKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "SUN");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(2048, random);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        privateKeyPlatform=privateKey;
        publicKeyPlatform=publicKey;
    }


    public static Pair<PrivateKey, PublicKey> genKeyPairAgent() throws NoSuchAlgorithmException, NoSuchProviderException{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(2048, random);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        Pair<PrivateKey,PublicKey> sal = new Pair<PrivateKey,PublicKey>(privateKey,publicKey);
        return sal;
    }


    public static PrivateKey getPrivateKey(){
        return privateKeyPlatform;
    }

    public static PublicKey getPublicKey(){
        return publicKeyPlatform;
    }

    public static byte[] encrypt(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }


    public static byte[] decrypt(PrivateKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }


    public static byte[] Signed(PrivateKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static byte[] deSigned(PublicKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }




    public static int init_platform(String path, String contextEK, String contextAK, JTextArea Printer){
        int valuereturn = 0;
        System.out.println("EXECUTING THE INIT ATT FUNCTION: ");
        Printer.append("EXECUTING THE INIT ATT FUNCTION: \n");
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

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            String line;
            while((line=reader.readLine())!=null){
                Printer.append(line+"\n");
                output.append(line+"\n");
            }
            valuereturn = p1.waitFor();
            System.out.println(output);
            if(valuereturn==0){
                Process p2 = pb2.start();
                reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                while((line=reader.readLine())!=null){
                    Printer.append(line+"\n");
                    output.append(line+"\n");
                }
                valuereturn = p2.waitFor();
                System.out.println(output);
            }
        }catch(Exception e){
            Printer.append("There are an error in the process \n");
            System.out.println("There are an error in the process");
            valuereturn = 1;
        }
        return valuereturn;
    }


    public static int attestation_files(String path,String context,String nonce,boolean debug, JTextArea Printer){
        int valuereturn = 0;
        Printer.append("EXECUTING THE ATTESTATION FUNCTION INTO THE TPM: \n");
        if(!debug){
            Printer.append("******CREATING THE ATTESTATION FILES WITH THE FOLLOWING INFORMATION:\n");
            Printer.append("SERVER TOKEN: "+nonce+"\n\n");
        }
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
            if(debug){
                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while((line=reader.readLine())!=null){
                    Printer.append(line+"\n");
                    output.append(line+"\n");
                }
                System.out.println(output);
            }
            int exitVal = p.waitFor();
            System.out.println(exitVal);
        }catch(Exception e){
            Printer.append("There are an error in the process \n");
            System.out.println("There are an error in the process");
            valuereturn = 1;
        }
        return valuereturn;
    }

    public static int check_attestation_files(String path,String nonce,boolean debug, JTextArea Printer){
        int valuereturn = 0;
        String sing_path = path+"/sign.out";
        String pcr_path = path+"/pcr.out";
        String quote_path = path+"/quote.out";
        Printer.append("EXECUTING THE CHECK FUNCTION: \n");
        System.out.println("EXECUTING THE CHECK FUNCTION: ");
        String akpub_path = path+"/akpub.pem";
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
            if(debug){
                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while((line=reader.readLine())!=null){
                    output.append(line+"\n");
                }
                System.out.println(output.toString());
                Printer.append(output.toString());
            }
            int exitVal = p.waitFor();
            System.out.println(exitVal);
        }catch(Exception e){
            System.out.println("There are an error in the process");
            valuereturn = 1;
        }
        return valuereturn;
    }

    public static String computeSHA256(String pathPCRFile, JTextArea Printer) throws Exception{
        FileInputStream fis = new FileInputStream(pathPCRFile);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++){
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        Printer.append(sb.toString()+"\n");
        System.out.println(sb.toString());
        return sb.toString();
    }


    public static void deleteFolder(File dir){
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File aFile : files) {
                    deleteFolder(aFile);
                }
            }
            dir.delete();
        } else {
            dir.delete();
        }
    }

    public static String getRandomChallenge(){
        return UUID.randomUUID().toString().replace("-", "");
    }


    public static void deserializeATT(String temPath, AttestationSerialized packetReceive){
        try{
            FileOutputStream streamAIK = new FileOutputStream(temPath+"/akpub.pem");
            streamAIK.write(packetReceive.getAIKPub());
            streamAIK.close();
            FileOutputStream streamSIGN = new FileOutputStream(temPath+"/sign.out");
            streamSIGN.write(packetReceive.getSign());
            streamSIGN.close();
            FileOutputStream streamPCR = new FileOutputStream(temPath+"/pcr.out");
            streamPCR.write(packetReceive.getMessage());
            streamPCR.close();
            FileOutputStream streamQUOTE = new FileOutputStream(temPath+"/quote.out");
            streamQUOTE.write(packetReceive.getQuoted());
            streamQUOTE.close();
        }catch(Exception e){
            System.out.println("ERROR IN THE DESERIALIZATION OF THE ATT INFORMATION RECEIVE");
        }
    }


    public static void deserializeATTWAIK(String temPath, AttestationSerialized packetReceive){
        try{
            FileOutputStream streamSIGN = new FileOutputStream(temPath+"/sign.out");
            streamSIGN.write(packetReceive.getSign());
            streamSIGN.close();
            FileOutputStream streamPCR = new FileOutputStream(temPath+"/pcr.out");
            streamPCR.write(packetReceive.getMessage());
            streamPCR.close();
            FileOutputStream streamQUOTE = new FileOutputStream(temPath+"/quote.out");
            streamQUOTE.write(packetReceive.getQuoted());
            streamQUOTE.close();
        }catch(Exception e){
            System.out.println("ERROR IN THE DESERIALIZATION OF THE ATT INFORMATION RECEIVE");
        }
    }



    /**
     * This function take as a arguments an array of bytes that represent the data, and a SecretKey to cipher it
     * @param data
     * @param key_cipher
     * @return
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] cipherOwner(byte[] data, SecretKey key_cipher) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv_values = new IvParameterSpec("1234567887654321".getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key_cipher, iv_values);
        byte[] result_encrypt = cipher.doFinal(data);
        return result_encrypt;
    }

    /**
     * This function take as a arguments an array of bytes that represent the data, and a SecretKey to decipher it
     * @param data
     * @param key_cipher
     * @return
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decipherOwner(byte[] data, SecretKey key_cipher) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv_values = new IvParameterSpec("1234567887654321".getBytes());
        SecretKeySpec specifications = new SecretKeySpec(key_cipher.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, specifications, iv_values);
        return cipher.doFinal(data);
    }

}