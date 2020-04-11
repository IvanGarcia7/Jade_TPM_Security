package jade.core.SecureTPM.INTERFAZ_TPM;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils_TPM {

    /*
        Metodo para mostrar por pantalla el funcionamiento de los servicios que vayamos implementando
        posteriormente, con el fin de mostrar un nivel de detalle adecuado.
    */

    public final static void printLog(String mensaje, Level nivel, boolean detalles, String clase) {
        java.util.logging.Logger logger = Logger.getLogger(clase);
        if ((logger.isLoggable(nivel))&&(detalles)) {
            logger.logp(nivel,clase,null,mensaje);
        }
    }

}
