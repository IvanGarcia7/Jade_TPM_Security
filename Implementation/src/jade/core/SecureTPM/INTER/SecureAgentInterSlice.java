package jade.core.SecureTPM.INTER;

import jade.core.Service;

public interface SecureAgentInterSlice extends Service.Slice{

    /**
     En primer lugar, enumeramos los comandos que hemos definido anteriormente
     en la clase Helper. Esta interfaz es utilizada para tratar los comandos
     verticales que llegen al agente.
    */

    static final String REQUEST_ATTESTATION = "10";
    static final String REQUEST_CONFIRMATION = "11";
    static final String REQUEST_DATA = "12";
    static final String REQUEST_RESPONSE = "13";
    static final String REQUEST_INFORM = "14";

    /**
     Posteriormente, solo queda definir los métodos que van a tratar dichos comandos,
     ejecutando el procedimiento deseado y devolviendo los datos para proseguir
     con el protocolo definido.
    */


}
