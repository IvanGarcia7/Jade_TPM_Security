package jade.core.SecureTPM.INTERFAZ_TPM.TEST_TPM_METHODS;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import jade.core.SecureTPM.INTERFAZ_TPM.Agencia;
import org.junit.Test;


public class Pruebas_tpm2_getrandom {
    @Test
    public void testAdd(){
        System.out.println("Verificación de la función tpm2_getrandom:");
        assertTrue(Agencia.numeros_aleatorios(-1).size()==0);
        assertTrue(Agencia.numeros_aleatorios(0).size()==0);
        assertTrue(Agencia.numeros_aleatorios(1).size()==1);
        Random r = new Random();
        for(int l=0;l<100;l++){
            int valor = r.nextInt(32);
            assertEquals("Error ",Agencia.numeros_aleatorios(valor).size(),valor);
        }
    }
    

}