package jade.core.SecureTPM.INTERFAZ_TPM.TEST_TPM_METHODS;

import static org.junit.Assert.assertEquals;

import jade.core.SecureTPM.INTERFAZ_TPM.MetodosTPM;
import org.junit.Test;


public class Pruebas_tpm2_mostrar_contenido {
    @Test
    public void testAdd(){
        System.out.println("Verificación de la función tpm2_mostrar_contenido");
        String ruta_fichero = "/home/pi/Desktop/testt/ajja/test.txt";
        
        //SUPONIENDO QUE SE INCLUYE INFORMACIÓN DENTRO DEL FICHERO
        try {
            MetodosTPM.tpm2_crear_archivo_informacion("hola", ruta_fichero);
            String content = MetodosTPM.tpm2_mostrar_contenido(ruta_fichero);
            assertEquals("Los contenidos difieren","hola",content.trim());

            MetodosTPM.tpm2_crear_archivo_informacion("", ruta_fichero);
            content = MetodosTPM.tpm2_mostrar_contenido(ruta_fichero);
            assertEquals("Los contenidos difieren","",content.trim());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}