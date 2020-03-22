import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Pruebas_tpm2_cifrado_descifrado {
    @Test
    public void testAdd() {
        System.out.println("Verificación de la función tpm2_cifrar_texto y tpm2_descifrar_texto:");

        String ruta_directorio_seguro = "/home/pi/Desktop/testt/ajja";
        try {
            MetodosTPM.tpm2_generar_claves2(ruta_directorio_seguro);
            String ruta_handler = ruta_directorio_seguro+"/key.pair";
            String ruta_clave = ruta_directorio_seguro+"/key.txt";

            String mensaje = "Leave Me Here MrRobot";
            String ruta_archivo_plano = ruta_directorio_seguro+"/plain.txt";
            MetodosTPM.tpm2_crear_archivo_informacion(mensaje, ruta_archivo_plano);
            
            String ruta_archivo_cifrado = ruta_directorio_seguro+"/cifrado.txt";
            String ruta_archivo_descifrado = ruta_directorio_seguro+"/descifrado.txt";

            MetodosTPM.tpm2_cifrar_texto(ruta_clave, ruta_archivo_plano, ruta_archivo_cifrado);
            MetodosTPM.tpm2_mostrar_contenido(ruta_archivo_cifrado);

            MetodosTPM.tpm2_descifrar_texto(ruta_handler, ruta_archivo_cifrado, ruta_archivo_descifrado);
            String descifrado = MetodosTPM.tpm2_mostrar_contenido(ruta_archivo_descifrado);
            assertEquals("Los contenidos difieren",mensaje,descifrado.trim());

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        



    }
    

}