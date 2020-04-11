package jade.core.SecureTPM.INTRA;

import jade.core.Location;
import jade.core.SecureTPM.SecureAgent;
import jade.core.ServiceHelper;

/**
 A la hora de modificar o añadir nuevas funcionalidades a un servicio ya
 existente, lo que hago es extender la clase al servicio correspondiente,
 en este caso AgentMobilityService.
 */

public interface SecureAgentIntraHelper extends ServiceHelper {

    /**
     En primer lugar, defino el nombre del servicio, siguiendo la guía
     de desarrollador.
     */

    public static final String NAME = "jade.core.secureMobility.SecureIntraAgentMobility";
    public static final boolean DEBUG = true;

    /**
     Defino en este punto los comandos verticales que voy a usar para
     comprobaciones de seguridad futuras.
     Necesitaremos los siguientes comandos, tal y como lo hemos definido
     dentro del protocolo de atestación:

     1. Solicitud para solicitar información de la plataforma destino.
     2. Solicitud de confirmación por parte de la plataforma destino.
     3. Solicitud de petición de los datos necesarios para verificar el destino.
     4. Solicitud para recibir los datos por parte del destino.

     Los comandos verticales, siguiendo la guia se definen como un String
     de la siguiente forma:

     public static final String nombre_String = "NAME";

     Estos comandos son del tipo vertical, lo que significa que al momento
     de recibirlos en una plataforma o contenedor, estos serán gestionadas
     por las clases que se definan posteriormente.

     Comandos Verticales: Misma Plataforma
     Comandos Horizontales: Diversas Plataformas
     */

    public static final String REQUEST_ATTESTATION = "REMOTE_ATT_ATTESTATION";
    public static final String REQUEST_CONFIRMATION = "REMOTE_ATT_CONFIRMATION";



    /*
        Una vez que se han definido los comandos verticales que vamos a usar
        tal y como se ha definido en el protocolo, pasamos a definir los nuevos
        metodos que va a disponer este servicio.
    */

    public void doSecureMove(SecureAgent agente, Location destino); //Mover el agente, dentro de la misma plataforma o contenedor.
    public void doSecureClone(SecureAgent agente, Location destino, String nombre_agente); //Migrar el agente en plataformas o contenedores distintos.

}
