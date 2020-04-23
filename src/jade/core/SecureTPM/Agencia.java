package jade.core.SecureTPM;

import javafx.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import java.util.Base64;
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


    /**
     * Seal take as a parameters one array ob bytes that represent the message
     * and return one object that contains the public and private part of the message sealed
     * the cipher agent serialized.
     * @param entrada
     * @return
     * @throws IOException
     */
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


    public static byte[] InfineonCertificate() {
		return null;

        //TO-DO

    }
}