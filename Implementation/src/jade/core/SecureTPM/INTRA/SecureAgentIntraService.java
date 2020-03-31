package jade.core.SecureTPM.INTRA;

import jade.core.*;
import jade.core.SecureTPM.INTERFAZ_TPM.Agencia;
import jade.core.SecureTPM.INTERFAZ_TPM.Pruebas_Borrado;
import jade.core.SecureTPM.PETICIONES.PeticionAtestacion;
import jade.core.SecureTPM.SecureAgent;
import jade.core.SecureTPM.INTERFAZ_TPM.Utils_TPM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SecureAgentIntraService extends BaseService {

    //Variables que son de utilidad para implementar el protocolo especificado
    private Map<AID, Agent> requestersList=new HashMap<AID,Agent>();

    /**
     * Metodo generado por defecto a la hora de establecer un nuevo Servicio
     * @return devuelve el nombre del servicio
     */

    @Override
    public String getName() {
        return SecureAgentIntraHelper.NAME;
    }


    /**
        A continuacion, se define el comportamiento que va a tener el metodo
        para realizar el movimiento del agente de forma segura, dentro de la
        misma agencia.
    */

    public synchronized void secureMove(SecureAgent agente, Location destino) {
        /**
            En base a las limitaciones de componentes, se ha definido el comportamiento
            en principio multihilo, es decir, solo se puede realizar una operación de
            movimiento o clonación para los agentes que componen la plataforma.

            Si el número de peticiones es 0, entonces puedo solicitar información al
            TPM para posteriormente, obtener los parametros deseado en cada comando
            vertical definido anteriormente.
        */

        StringBuilder sb = new StringBuilder();

        if ((Agencia.agentes_pendientes_clonacion.size() + Agencia.agentes_pendientes_moverse.size())==0) {
            sb.append("Comienza el proceso para mover al agente ").append(agente.getAID());
            System.out.println(sb.toString());
            Utils_TPM.printLog("INICIO DEL SERVICIO PARA MOVER AL AGENTE", Level.INFO,true,this.getClass().getName());
            //A PARTIR DE ESTE PUNTO, INICIO EL PROCESO PARA REALIZAR EL MOVIMIENTO DEL AGENTE
            Agencia.agentes_pendientes_moverse.add(agente.getAID()); //INTRODUZCO AL AGENTE EN LA LISTA
            //EN BASE A LA CONFIGURACION ESTABLECIDA ANTERIORMENTE, BLOQUEO QUE SE PUEDA HACER ESTE PROCESO
            //DE FORMA SIMULTANEA ENTRE AGENTES. CREO UN NUEVO COMANDO, SIGUIENDO LA DOCUMENTACIÓN
            GenericCommand comando = new GenericCommand(SecureAgentIntraHelper.REQUEST_ATTESTATION,SecureAgentIntraHelper.NAME,"PETICION DE ATESTACION ENVIADA");
            //SE VA A ENVIAR UN NUEVO OBJETO CON DATOS COMO EL NOMBRE DEL AGENTE, LA LOCALIZACIÓN Y LA AGENCIA
            PeticionAtestacion paquete = new PeticionAtestacion(agente.getAID(),destino);
            comando.addParam(paquete);
            //Envio el comando al servicio que lo consuma posteriormente.
            Utils_TPM.printLog("AGENTE SOLICITA MOVERSE Y MANDA PETICION A LA AGENCIA", Level.INFO,true,this.getClass().getName());
            try{
                SecureAgentIntraService.this.submit(comando);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        else {
            sb.append("NO HA SIDO POSIBLE DAR COMIENZO AL PROCESO PARA MOVER EL AGENTE ").append(agente.getAID()).append(" TPM OCUPADO CON OTRAS PETICIONES");
            Utils_TPM.printLog("NO ES POSIBLE EJECUTAR EL SERVICIO PARA MOVER AL AGENTE, DEMASIADAS PETICIONES", Level.INFO,true,this.getClass().getName());
            agente.doSecureMoveError(sb.toString());
        }

    }


    //Funcion para consumir los comandos verticales recibidos por los diversos agentes
    public void consume(VerticalCommand comando){
        //A la hora de gestionar un comando vertical, debo tratar múltiples excepciones que pueden llegar a suceder
        try{
            String tipo_peticion = comando.getName();
            if(tipo_peticion.equals(SecureAgentIntraHelper.REQUEST_ATTESTATION)){
                    manejador_peticion_atestacion(comando);
            }
        }catch(Exception e){
            IMTPException ie = new IMTPException("Error en SecureAgentIntra a la hora de tratar el comando vertical");
            comando.setReturnValue(ie);
        }
    }

    private void manejador_peticion_atestacion(VerticalCommand comando) throws ServiceException {
        //En primer lugar obtengo los parametros incluidos dentro del comando vertical
        Object[] parametros = comando.getParams();
        PeticionAtestacion paquete = (PeticionAtestacion) parametros[0];
        SecureAgentIntraSlice objetivo = (SecureAgentIntraSlice) getSlice(paquete.getLocation().getName());
        try{
            objetivo.doRequestAttestation(comando);
        }catch(Exception ie){
            ie.printStackTrace();
        }

    }


}
