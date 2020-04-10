package jade.core.SecureTPM.INTERFAZ_TPM;

import jade.core.SecureTPM.PETICIONES.Objeto_Sealed;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import java.util.StringTokenizer;

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


    /*
        Este método toma como entrda un String, el cual se desea cifrar, para
        posteriormente incluirlo dentro de un archivo File en la ubicación actual
        para posteriormente enviar dicha información cifrada
    */
    public static void tpm2_crear_archivo_informacion(String datos,String ruta){
        try{
            FileWriter fichero = new FileWriter(ruta);
            fichero.write(datos);
            fichero.close();
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }


    /*
        Este método es utilizado para generar en la ruta especificada tanto el key.pair como la
        clave pública que será intercambiada entre los agentes para cifrar la información de forma
        segura.
    */
    public static void tpm2_generar_claves2(String ruta) throws InterruptedException {
        String rutabuena = ruta+"/key.pair";
        String rutabuena2 = ruta+"/key.txt";

        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2tss-genkey",
                        "-a", "rsa",
                        "-s", "2048",
                        rutabuena
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
                        "-in", rutabuena,
                        "-pubout",
                        "-outform", "pem",
                        "-out",rutabuena2
                });

        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            p.waitFor();
            p.exitValue();
        } catch (IOException exp) {
            exp.printStackTrace();
        }

    }


    public static void tpm2_cifrar_texto(String rutaClave,String rutaArchivo,String rutaDestino){
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-pubin",
                        "-inkey", rutaClave,
                        "-in", rutaArchivo,
                        "-encrypt",
                        "-out", rutaDestino
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
    }

    public static void tpm2_descifrar_texto(String rutaHandler,String rutaArchivo,String rutaDestino){
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-engine","tpm2tss",
                        "-keyform", "ENGINE",
                        "-inkey", rutaHandler,
                        "-decrypt",
                        "-in", rutaArchivo,
                        "-out", rutaDestino
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
    }

    /*
        Este método toma como parámetro la ruta del archivo que se desea mostrar el contenido, y se guarda en un String
        para posteriormente mostrarlo si fuese necesario.
    */
    public static String tpm2_mostrar_contenido(String ruta) throws InterruptedException {

        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "hexdump",
                        "-C", ruta
                });

        pb1.redirectErrorStream(true);
        StringBuilder sb = new StringBuilder();
        try {
            Process p = pb1.start();
            p.waitFor();
            BufferedReader br = new BufferedReader(new FileReader(ruta));
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
        return sb.toString();

    }

    /*
        Este método devuelve un ArrayList con un número determinado
        de números aleatorios, generado por el modulo TPM. Como limitación,
        solo es posible generar hasta 32 dígitos.
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



    public static void tpm2_firmar_informacion(String rutahandler,String rutaArchivo,String rutaDestino){
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-engine", "tpm2tss",
                        "-keyform", "ENGINE",
                        "-inkey", rutahandler,
                        "-sign",
                        "-in", rutaArchivo,
                        "-out", rutaDestino
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
    }



    public static int tpm2_informacion_verificada(String rutaArchivo,String rutaHandler,String rutaCifrada){
        int resultado = 1;
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "openssl", "pkeyutl",
                        "-engine", "tpm2tss",
                        "-keyform", "ENGINE",
                        "-inkey", rutaHandler,
                        "-verify",
                        "-in", rutaArchivo,
                        "-sigfile", rutaCifrada,
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
        return resultado;
    }

    public static boolean getStatusBloqueado(){
        return bloqueado;
    }

    public static void setStatus(boolean estado){
        bloqueado=estado;
    }


    //***************************************************************

    public static byte[] cipherOwner(byte[] data, SecretKey key_cipher) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key_cipher, iv);
        byte[] encryptData = cipher.doFinal(data);
        return encryptData;
    }



    public static byte[] decipherOwner(byte[] data, SecretKey key_cipher) throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        SecretKeySpec spec = new SecretKeySpec(key_cipher.getEncoded(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, spec, iv);
        return cipher.doFinal(data);
    }


    public static SecretKey generateOTP() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for example
        SecretKey secretKey = keyGen.generateKey();
        return secretKey;
    }


    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }


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

        //System.out.println("Los bytes publicos son los siguientes: "+fileContentPublic);
        //System.out.println("Los bytes privados son los siguientes: "+fileContentPrivate);

        public_file.delete();
        private_file.delete();
        entrada_file.delete();
        Objeto_Sealed sealed = new Objeto_Sealed(fileContentPublic, fileContentPrivate, agent);
        //System.out.println("Objeto sealed correctamente serializado ");
        byte[] yourBytes = Agencia.serialize(sealed);
        //System.out.println("Objeto serializado es el siguiente "+yourBytes);
        return yourBytes;

    }

    public static byte [] unseal(byte[] entrada) throws FileNotFoundException, IOException, ClassNotFoundException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
        ProcessBuilder pb1 = new ProcessBuilder(
                new String[]{
                        "tpm2_createprimary",
                        "-H", "o",
                        "-g", "sha256",
                        "-G", "ecc",
                        "-C", "primary.context"
                });

        System.out.println("He ejecutado el contexto en el destino");


        Objeto_Sealed seal = (Objeto_Sealed) Agencia.deserialize(entrada);

        //System.out.println("He descerializado el objeto");

        byte [] public_byte = seal.get_public_part();
        byte [] private_byte = seal.get_private_part();
        byte [] agent = seal.get_agente();

        //System.out.println("hola "+public_byte+" "+private_byte+" "+agent);



        OutputStream os = new FileOutputStream("pub_receive.File");
        os.write(public_byte);
        //System.out.println("Write bytes to file.");

        os.close();

        OutputStream osq = new FileOutputStream("pri_receive.File");
        osq.write(private_byte);
        //System.out.println("Write bytes to file.");

        osq.close();

        //System.out.println("He llegado");


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

        //System.out.println("He llegado dos");

        File public_files = new File("pub_receive.File");
        public_files.delete();

        File private_files = new File("pri_receive.File");
        private_files.delete();

        //System.out.println("He llegado tres");

        File key_recevived = new File("entrada.File");
        byte[] fileContent = Files.readAllBytes(key_recevived.toPath());

        //System.out.println("He llegado cuatro");

        SecretKey clave = new SecretKeySpec(fileContent,0,fileContent.length,"AES");

        //System.out.println("He llegado cinco");

        byte [] salida = Agencia.decipherOwner(agent, clave);

        //System.out.println("He llegado seis");

        return salida;


    }


}