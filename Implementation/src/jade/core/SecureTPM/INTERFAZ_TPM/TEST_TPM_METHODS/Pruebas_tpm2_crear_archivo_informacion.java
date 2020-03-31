package jade.core.SecureTPM.INTERFAZ_TPM.TEST_TPM_METHODS;

import static org.junit.Assert.assertEquals;

import jade.core.SecureTPM.INTERFAZ_TPM.MetodosTPM;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Pruebas_tpm2_crear_archivo_informacion {
    @Test
    public void testAdd(){
        System.out.println("Verificación de la función tpm2_crear_archivo_informacion:");
        String ruta_fichero = "/home/pi/Desktop/testt/ajja/test.txt";
        
        //SUPONIENDO QUE SE INCLUYE INFORMACIÓN DENTRO DEL FICHERO
        try {
            MetodosTPM.tpm2_crear_archivo_informacion("hola", ruta_fichero);
            String content = Files.readString(Paths.get(ruta_fichero));
            assertEquals("Los contenidos difieren","hola",content);

        } catch (IOException e) {
            e.printStackTrace();
        }


        //Ejemplo de escritura dentro de un archivo con múltiples líneas ya caracteres
        //SUPONIENDO QUE SE INCLUYE INFORMACIÓN DENTRO DEL FICHERO
        try {
            MetodosTPM.tpm2_crear_archivo_informacion("Esto\n!Es una prueba:;#@¢\"", ruta_fichero);
            String content = Files.readString(Paths.get(ruta_fichero));
            assertEquals("Los contenidos difieren","Esto\n!Es una prueba:;#@¢\"",content);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //SUPONIENDO QUE NO SE INCLUYE INFORMACIÓN DENTRO DEL FICHERO
        try {
            MetodosTPM.tpm2_crear_archivo_informacion("", ruta_fichero);
            String content = Files.readString(Paths.get(ruta_fichero));
            assertEquals("Los contenidos difieren","",content);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    

}