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
    public void doRequestData(VerticalCommand comando);
    public void doRequestResponse(VerticalCommand comando);
    public void doRequestInform(VerticalCommand comando);
    public void doRequestError(VerticalCommand comando_generado);

    /**
     En primer lugar, enumeramos los comandos que hemos definido anteriormente
     en la clase Helper. Esta interfaz es utilizada para tratar los comandos
     verticales que llegen al agente.
     */

    static final String REMOTE_REQUEST_ATTESTATION = "10";
    static final String REMOTE_REQUEST_CONFIRMATION = "11";
    static final String REMOTE_REQUEST_DATA = "12";
    static final String REMOTE_REQUEST_RESPONSE = "13";
    static final String REMOTE_REQUEST_INFORM = "14";
    static final String REMOTE_REQUEST_ERROR = "15";




}
