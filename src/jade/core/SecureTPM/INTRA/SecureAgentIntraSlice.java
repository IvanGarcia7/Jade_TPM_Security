package jade.core.SecureTPM.INTRA;

import jade.core.*;

public interface SecureAgentIntraSlice extends Service.Slice {

    /**
     Se definen los métodos que van a tratar dichos comandos,
     ejecutando el procedimiento deseado y devolviendo los datos para proseguir
     con el protocolo definido. Como entrada de estos métodos, se definirá un comando
     vertical el cual será tratado en base a los parámetros que contenga el mismo.
     */

    public void doRequestAttestation(VerticalCommand comando);
    public void doRequestConfirmation(VerticalCommand comando);


    /**
     En primer lugar, enumeramos los comandos que hemos definido anteriormente
     en la clase Helper. Esta interfaz es utilizada para tratar los comandos
     verticales que llegen al agente.
     */

    static final String REMOTE_REQUEST_ATTESTATION = "10";
    static final String REMOTE_REQUEST_CONFIRMATION = "11";


}
