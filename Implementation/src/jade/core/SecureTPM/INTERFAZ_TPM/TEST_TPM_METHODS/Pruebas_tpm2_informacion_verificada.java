package jade.core.SecureTPM.INTERFAZ_TPM.TEST_TPM_METHODS;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Pruebas_tpm2_informacion_verificada {
    @Test
    public void testAdd() {
        System.out.println("Verificación de la función tpm2_informacion_verificada:");

        String ruta_directorio_seguro = "/home/pi/Desktop/testt/ajja2";
        try {
            MetodosTPM.tpm2_generar_claves2(ruta_directorio_seguro);
            String ruta_handler = ruta_directorio_seguro+"/key.pair";

            String mensaje = "Leave Me Here MrRobot";
            String ruta_archivo_plano = ruta_directorio_seguro+"/plain.txt";

            MetodosTPM.tpm2_crear_archivo_informacion(mensaje, ruta_archivo_plano);
            
            String ruta_archivo_firmado = ruta_directorio_seguro+"/signed.txt";

            MetodosTPM.tpm2_firmar_informacion(ruta_handler, ruta_archivo_plano, ruta_archivo_firmado);

            int resultado = MetodosTPM.tpm2_informacion_verificada(ruta_archivo_plano, ruta_handler, ruta_archivo_firmado);
            
            assertEquals("No es el resultado esperado",resultado,0);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        



    }
    

}