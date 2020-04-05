package jade.core.SecureTPM.INTERFAZ_TPM.TEST_TPM_METHODS;

import java.io.File;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Pruebas_tpm2_generarclaves2 {
    @Test
    public void testAdd() {
        System.out.println("Verificación de la función tpm2_generarclaves2:");

        String ruta_fichero_handler = "/home/pi/Desktop/testt/ajja/key.pair";
        String ruta_fichero_clave = "/home/pi/Desktop/testt/ajja/key.txt";
        String ruta = "/home/pi/Desktop/testt/ajja";

        File tmpDirHandler = new File(ruta_fichero_handler);
        File tmpDirKeys = new File(ruta_fichero_clave);

        tmpDirHandler.delete();
        tmpDirKeys.delete();

        boolean existshandler = tmpDirHandler.exists();
        boolean existKeys = tmpDirKeys.exists();

        assertTrue(existshandler == false);
        assertTrue(existKeys == false);

        try {
            MetodosTPM.tpm2_generar_claves2(ruta);

            existshandler = tmpDirHandler.exists();
            existKeys = tmpDirKeys.exists();

            assertTrue(existshandler == true);
            assertTrue(existKeys == true);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        
        
        
    }
    

}