import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;



public class MetodosTPM{

   
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

}