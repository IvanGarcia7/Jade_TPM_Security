package jade.core.SecureTPM.INTRA;

import jade.core.Service;
import jade.core.VerticalCommand;

public interface SecureAgentIntraSlice extends Service.Slice {

    /**
     Se definen los métodos que van a tratar dichos comandos,
     ejecutando el procedimiento deseado y devolviendo los datos para proseguir
     con el protocolo definido. Como entrada de estos métodos, se definirá un comando
     vertical el cual será tratado en base a los parámetros que contenga el mismo.
     */

    public void doRequestAttestation(VerticalCommand comando);
    public void doRequestConfirmation(VerticalCommand comando);
    public void doRequestData(VerticalCommand comando);
    public void doRequestResponse(VerticalCommand comando);
    public void doRequestInform(VerticalCommand comando);

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

}
