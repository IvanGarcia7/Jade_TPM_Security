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

import com.jfoenix.controls.JFXTextArea;


public class Agencia{

    //Variables definidas que puedan ser de utilidad
    //Variable utilizada como semaforo para evitar que se colapse devido a múltiples peticiones dentro del TPM
    public static boolean bloqueado = false;
    public static int time = 1000000000;



    /**
     * This method create a key pair and returns a Pair with the information.
     * @throws InterruptedException
     * @throws IOException
     */
    public static void tpm2_generar_claves2() throws InterruptedException, IOException {
        String route = "./key.pair";
        String route_pub = "./keypub.pair";
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2tss-genkey",
                        "-a", "rsa",
                        "-s", "2048",
                        route
                });
        pb1.redirectErrorStream(true);
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        ProcessBuilder pb = new ProcessBuilder(
                new String[]{
                        "openssl","rsa",
                        "-engine", "tpm2tss",
                        "-inform", "engine",
                        "-in", route,
                        "-pubout",
                        "-outform", "pem",
                        "-out",route_pub
                });
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            p.waitFor();
            p.exitValue();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        File private_key = new File(route);
        File public_key = new File(route_pub);
        byte[] priv_key_byte = Files.readAllBytes(private_key.toPath());
        byte[] pub_key_byte = Files.readAllBytes(public_key.toPath());
        Pair<byte [], byte []> result = new Pair<byte [], byte []>(priv_key_byte,pub_key_byte);
        private_key.delete();
        public_key.delete();
    }

    /**
     * This method take the public key and the data and return a cipher array of bytes
     * @param pubkey
     * @param data
     * @return
     * @throws IOException
     */
    public static byte [] tpm2_cifrar_texto(byte[] pubkey,byte [] data) throws IOException {
        OutputStream os = new FileOutputStream("./pubkey.File");
        os.write(pubkey);
        os.close();
        OutputStream osdata = new FileOutputStream("./data_plain.File");
        osdata.write(data);
        osdata.close();
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-pubin",
                        "-inkey", "./pubkey.File",
                        "-in", "./data_plain.File",
                        "-encrypt",
                        "-out", "./cipherdata.File"
                });
        pb1.redirectErrorStream(true);
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File datacipher = new File("./cipherdata.File");
        byte[] data_cipher_bytes = Files.readAllBytes(datacipher.toPath());
        datacipher.delete();
        File pub = new File("./pubkey.File");
        pub.delete();
        File data_plan = new File("./data_plain.File");
        data_plan.delete();
        return data_cipher_bytes;
    }

    /**
     * This method take as a argument an array of data an the key serialized
     * and return an array of bytes with the decripted data.
     * @param data
     * @param keyPrivate
     * @return
     * @throws IOException
     */
    public static byte [] tpm2_descifrar_texto(byte [] data, byte [] keyPrivate) throws IOException {
        OutputStream os = new FileOutputStream("./datacipher.File");
        os.write(data);
        os.close();
        OutputStream oskey = new FileOutputStream("./keyprivate.File");
        oskey.write(data);
        oskey.close();
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-engine","tpm2tss",
                        "-keyform", "ENGINE",
                        "-inkey", "./keyprivate.File",
                        "-decrypt",
                        "-in", "./datacipher.File",
                        "-out", "./decipherdata.File"
                });
        pb1.redirectErrorStream(true);
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File decipherdata = new File("./decipherdata.File");
        byte[] fileContent = Files.readAllBytes(decipherdata.toPath());
        decipherdata.delete();
        File dat = new File("./datacipher.File");
        dat.delete();
        File key = new File("./keyprivate.File");
        key.delete();
        return fileContent;
    }

    /**
     * This method takes an array of data to show it.
     * @param data
     * @return
     * @throws InterruptedException
     */
    public static String tpm2_mostrar_contenido(byte [] data) throws InterruptedException, IOException {
        OutputStream os = new FileOutputStream("./datashow.File");
        os.write(data);
        os.close();
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "hexdump",
                        "-C", "./datashow.File"
                });
        pb1.redirectErrorStream(true);
        StringBuilder sb = new StringBuilder();
        try {
            Process p = pb1.start();
            p.waitFor();
            BufferedReader br = new BufferedReader(new FileReader("./datashow.File"));
            String line = br.readLine();
            while(line!=null){
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            p.exitValue();
            br.close();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
        File salida = new File("./datashow.File");
        salida.delete();
        return sb.toString();
    }

    /**
     * This method return an array list of random numbers.
     * @param numeros
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static ArrayList numeros_aleatorios(int numeros){
        ArrayList<String> lista_numeros = new ArrayList<String>();
        try {
            String result="";
            String line;
            String comando = "tpm2_getrandom "+numeros;
            Process p = Runtime.getRuntime().exec(comando);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line=in.readLine())!=null){
                result+=line;
            }
            in.close();
            StringTokenizer st = new StringTokenizer(result);
            while(st.hasMoreTokens()){
                lista_numeros.add(st.nextToken());
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
        return lista_numeros;
    }



    public static byte [] tpm2_firmar_informacion(byte [] privkey,byte [] data) throws IOException {
        OutputStream oskey = new FileOutputStream("./keyprivatesign.File");
        oskey.write(privkey);
        oskey.close();
        OutputStream osdata = new FileOutputStream("./dataunsigned.File");
        osdata.write(data);
        osdata.close();
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-engine", "tpm2tss",
                        "-keyform", "ENGINE",
                        "-inkey", "./keyprivatesign.File",
                        "-sign",
                        "-in", "./dataunsigned.File",
                        "-out", "./datasigned.File"
                });
        pb1.redirectErrorStream(true);
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File priv = new File("./keyprivatesign.File");
        priv.delete();
        File pub = new File("./dataunsigned.File");
        pub.delete();
        File signeddata = new File("./datasigned.File");
        byte[] fileContent = Files.readAllBytes(signeddata.toPath());
        signeddata.delete();
        return fileContent;
    }


    public static int tpm2_informacion_verificada(byte [] data,byte [] pubkey) throws IOException {
        OutputStream oskey = new FileOutputStream("./pubkey.File");
        oskey.write(pubkey);
        oskey.close();
        OutputStream osdata = new FileOutputStream("./data.File");
        osdata.write(data);
        osdata.close();
        int resultado = 1;
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-engine", "tpm2tss",
                        "-keyform", "ENGINE",
                        "-inkey", "./pubkey.File",
                        "-verify",
                        "-in", "./data.File",
                        "-sigfile", "signed_data.File",
                });
        pb1.redirectErrorStream(true);
        String result="";
        try {
            String line;
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line=in.readLine())!=null){
                result+=line;
            }
            in.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String validacion = "engine \"tpm2tss\" set.Signature Verified Successfully";
        if(result.equals(validacion)){
            resultado = 0;
        }
        File keypub = new File("./pubkey.File");
        keypub.delete();
        File signdata = new File("./data.File");
        signdata.delete();
        return resultado;
    }

    public static boolean getStatusBloqueado(){
        return bloqueado;
    }

    public static void setStatus(boolean estado){
        bloqueado=estado;
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

    /**
     * Seal take as a parameters one array ob bytes that represent the key that has been used to encrypt the agent,
     * and the agent serialized and return one object that contains the public and private part of the key sealed, and
     * the cipher agent serialized.
     * @param entrada
     * @param agent
     * @return
     * @throws IOException
     */
    /*
    public static byte[] seal(byte [] entrada, byte [] agent) throws IOException{
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_createprimary",
                        "-H", "o",
                        "-g", "sha256",
                        "-G", "ecc",
                        "-C", "primary.context"
                });
        try (FileOutputStream fos = new FileOutputStream("entrada.File")) {
            fos.write(entrada);
            fos.close();
        }
        ProcessBuilder pb2 = new ProcessBuilder(
                new String[]{
                        "tpm2_create",
                        "-c", "primary.context",
                        "-K", "def456",
                        "-g", "sha256",
                        "-G", "keyedhash",
                        "-I", "entrada.File",
                        "-u", "opusalida.File",
                        "-r", "oprsalida.File"
                });
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
            Process p2 = pb2.start();
            p2.waitFor();
            p2.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File public_file = new File("opusalida.File");
        File private_file = new File("oprsalida.File");
        File entrada_file = new File("entrada.File");
        byte[] fileContentPublic  = Files.readAllBytes(public_file.toPath());
        byte[] fileContentPrivate = Files.readAllBytes(private_file.toPath());
        public_file.delete();
        private_file.delete();
        entrada_file.delete();
        Sealed_Object sealed = new Sealed_Object(fileContentPublic, fileContentPrivate, agent);
        byte[] yourBytes = Agencia.serialize(sealed);
        return yourBytes;
    }
    */

    /**
     * Unseal take as a parameters one array ob bytes that represent the object sealed serialized,
     * and return the decipher agent serialized.
     * @param entrada
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    /*
    public static byte [] unseal(byte[] entrada) throws FileNotFoundException, IOException, ClassNotFoundException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_createprimary",
                        "-H", "o",
                        "-g", "sha256",
                        "-G", "ecc",
                        "-C", "primary.context"
                });
        Sealed_Object seal = (Sealed_Object) Agencia.deserialize(entrada);
        byte [] public_byte = seal.get_public_part();
        byte [] private_byte = seal.get_private_part();
        byte [] agent = seal.get_agente();
        OutputStream os = new FileOutputStream("pub_receive.File");
        os.write(public_byte);
        os.close();
        OutputStream osq = new FileOutputStream("pri_receive.File");
        osq.write(private_byte);
        osq.close();
        ProcessBuilder pb2 = new ProcessBuilder(
                new String[]{
                        "tpm2_load",
                        "-c", "primary.context",
                        "-u", "pub_receive.File",
                        "-r", "pri_receive.File",
                        "-C", "loadedKey.handle"
                });
        ProcessBuilder pb3 = new ProcessBuilder(
                new String[]{
                        "tpm2_unseal",
                        "-c", "loadedKey.handle",
                        "-P", "def456",
                        "-o", "entrada.File"
                });
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
            Process p2 = pb2.start();
            p2.waitFor();
            p2.exitValue();
            Process p3 = pb3.start();
            p3.waitFor();
            p3.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File public_files = new File("pub_receive.File");
        public_files.delete();
        File private_files = new File("pri_receive.File");
        private_files.delete();
        File key_recevived = new File("entrada.File");
        byte[] fileContent = Files.readAllBytes(key_recevived.toPath());
        SecretKey clave = new SecretKeySpec(fileContent,0,fileContent.length,"AES");
        byte [] salida = Agencia.decipherOwner(agent, clave);
        return salida;
    }
*/

    /**
     * Seal take as a parameters one array ob bytes that represent the message
     * and return one object that contains the public and private part of the message sealed
     * the cipher agent serialized.
     * @param entrada
     * @return
     * @throws IOException
     */
    /*
    public static byte[] seal_message(byte [] entrada) throws IOException{
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_createprimary",
                        "-H", "o",
                        "-g", "sha256",
                        "-G", "ecc",
                        "-C", "primary.context"
                });
        try (FileOutputStream fos = new FileOutputStream("entrada.File")) {
            fos.write(entrada);
            fos.close();
        }
        ProcessBuilder pb2 = new ProcessBuilder(
                new String[]{
                        "tpm2_create",
                        "-c", "primary.context",
                        "-K", "def456",
                        "-g", "sha256",
                        "-G", "keyedhash",
                        "-I", "entrada.File",
                        "-u", "opusalida.File",
                        "-r", "oprsalida.File"
                });
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
            Process p2 = pb2.start();
            p2.waitFor();
            p2.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File public_file = new File("opusalida.File");
        File private_file = new File("oprsalida.File");
        File entrada_file = new File("entrada.File");
        byte[] fileContentPublic  = Files.readAllBytes(public_file.toPath());
        byte[] fileContentPrivate = Files.readAllBytes(private_file.toPath());
        public_file.delete();
        private_file.delete();
        entrada_file.delete();
        Sealed_Object sealed = new Sealed_Object(fileContentPublic, fileContentPrivate);
        byte[] yourBytes = Agencia.serialize(sealed);
        return yourBytes;
    }

*/
    /**
     * Unseal take as a parameters one array ob bytes that represent the object sealed serialized,
     * and return the decipher message serialized.
     * @param entrada
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    
    /*
    public static byte [] unseal_message(byte[] entrada) throws FileNotFoundException, IOException, ClassNotFoundException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_createprimary",
                        "-H", "o",
                        "-g", "sha256",
                        "-G", "ecc",
                        "-C", "primary.context"
                });
        Sealed_Object seal = (Sealed_Object) Agencia.deserialize(entrada);
        byte [] public_byte = seal.get_public_part();
        byte [] private_byte = seal.get_private_part();
        OutputStream os = new FileOutputStream("pub_receive.File");
        os.write(public_byte);
        os.close();
        OutputStream osq = new FileOutputStream("pri_receive.File");
        osq.write(private_byte);
        osq.close();
        ProcessBuilder pb2 = new ProcessBuilder(
                new String[]{
                        "tpm2_load",
                        "-c", "primary.context",
                        "-u", "pub_receive.File",
                        "-r", "pri_receive.File",
                        "-C", "loadedKey.handle"
                });
        ProcessBuilder pb3 = new ProcessBuilder(
                new String[]{
                        "tpm2_unseal",
                        "-c", "loadedKey.handle",
                        "-P", "def456",
                        "-o", "entrada.File"
                });
        try {
            Process p = pb1.start();
            p.waitFor();
            p.exitValue();
            Process p2 = pb2.start();
            p2.waitFor();
            p2.exitValue();
            Process p3 = pb3.start();
            p3.waitFor();
            p3.exitValue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File public_files = new File("pub_receive.File");
        public_files.delete();
        File private_files = new File("pri_receive.File");
        private_files.delete();
        File key_recevived = new File("entrada.File");
        byte[] fileContent = Files.readAllBytes(key_recevived.toPath());
        //String s = Base64.getEncoder().encodeToString(fileContent);
        return fileContent;
    }

*/
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


    /**
     * THIS METHOD IS USED TO DECIPHER TE CONTENT OF A REQUEST IN THE PLATFORM,
     * THAT HAS BEEN CIPHER WITH THE PUBLIC KEY OF THE CA
     * @param contentObject
     * @return
     */
    public static RequestSecureATT decypher(Serializable contentObject) {
        RequestSecureATT pp = null;
        return pp;
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
        Printer.append("EXECUTING THE SALIDA FUNCTION: \n");
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

    public static int check_attestation_files(String path,String nonce,boolean debug, JFXTextArea Printer){
        int valuereturn = 0;
        String sing_path = path+"/sign.out";
        String pcr_path = path+"/pcr.out";
        String quote_path = path+"/quote.out";
        Printer.appendText("EXECUTING THE CHECK FUNCTION: \n");
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
                System.out.println(output);
            }
            int exitVal = p.waitFor();
            System.out.println(exitVal);
        }catch(Exception e){
            System.out.println("There are an error in the process");
            valuereturn = 1;
        }
        return valuereturn;
    }

    public static String computeSHA256(String pathPCRFile, JFXTextArea Printer) throws Exception{
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
        Printer.appendText(sb.toString()+"\n");
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





}