package jade.core.SecureTPM;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.SecureTPM.INTER.SecureAgentInterHelper;
import jade.core.SecureTPM.INTERFAZ_TPM.Utils_TPM;
import jade.core.SecureTPM.INTRA.SecureAgentIntraHelper;
import jade.core.ServiceException;
import jade.core.mobility.AgentMobilityHelper;

import java.util.logging.Level;

public class SecureAgent extends jade.core.Agent {

    private transient SecureAgentInterHelper mobHelperInter;
    private transient SecureAgentIntraHelper mobHelperIntra;

    /**
        Se ha extendido la clase del agente, con el objetivo de
        crear nuevos métodos, todos ellos orientados en un principio
        a la migración de los agentes.
    */

    /**
     Este metodo es utilizado para mover a un agente, a una localizacion
     pasada como parametro. Inicializa el proceso de migracion.
     Dentro del mismo, se ha diferenciado si la localización corresponde
     con una plataforma disponible dentro del contenedor actual, o bien
     con una nueva plataforma. Para cada uno de los casos, se crearan
     servicios que ejecuten el proceso de forma correcta.
     */

    public void doSecureMove(Location destino) {
        try {
            if(destino instanceof ContainerID){
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA MOVER AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                    .append("Agente: "+this.getAID()+"\n")
                    .append("A LA AGENCIA ACTUAL "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Utils_TPM.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO", Level.INFO,SecureAgentIntraHelper.DEBUG,this.getClass().getName());
                initAgentIntraHelper();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperIntra.doSecureMove(this,destino);
            }else{
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA MOVER AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                        .append("Agente: "+this.getAID()+"\n")
                        .append("OTRA AGENCIA EXTERNA "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Utils_TPM.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO", Level.INFO,SecureAgentInterHelper.DEBUG,this.getClass().getName());
                initAgentInterHelper();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperInter.doSecureMove(this,destino);
            }
        }
        catch(ServiceException se) {
            // FIXME: Log a proper warning
            return;
        }
    }


    /**
        El metodo definido a continuacion, se utiliza con el fin de mostrar mensajes
        de errores que puedan derivarse a lo largo del proceso de migracion de un agente
        en particular, mostrando el error generado y el agente del cual proviene.
    */

    public void doSecureMoveError(String mensaje) {
        System.out.println(mensaje);
    }


    /**
     Este metodo es utilizado para clonar a un agente, a una localizacion
     pasada como parametro. Inicializa el proceso de migracion.
     Dentro del mismo, se ha diferenciado si la localización corresponde
     con una plataforma disponible dentro del contenedor actual, o bien
     con una nueva plataforma. Para cada uno de los casos, se crearan
     servicios que ejecuten el proceso de forma correcta. Por otro lado,
     el segundo parámetro corresponde con el nombre que se le va a dar
     al nuevo agente, una vez que la migracion haya concluido.
     */

    public void doSecureClone(Location destino, String nombre_agente) {
        try {
            if(destino instanceof ContainerID){
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA CLONAR AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                        .append("Agente: "+this.getAID()+"\n")
                        .append("A LA AGENCIA ACTUAL "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Utils_TPM.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO", Level.INFO,SecureAgentIntraHelper.DEBUG,this.getClass().getName());
                initAgentIntraHelper();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperIntra.doSecureClone(this, destino, nombre_agente);
            }else{
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA CLONAR AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                        .append("Agente: "+this.getAID()+"\n")
                        .append("OTRA AGENCIA EXTERNA "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Utils_TPM.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO", Level.INFO,SecureAgentInterHelper.DEBUG,this.getClass().getName());
                initAgentInterHelper();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperInter.doSecureClone(this, destino, nombre_agente);
            }
        }
        catch(ServiceException se) {
            // FIXME: Log a proper warning
            return;
        }
    }

    /*
        Código relacionado con la inicialización del servicio
    */

    private void initAgentIntraHelper() throws ServiceException {
        if (mobHelperIntra == null) {
            mobHelperIntra = (SecureAgentIntraHelper) getHelper(AgentMobilityHelper.NAME);
        }
    }

    private void initAgentInterHelper() throws ServiceException {
        if (mobHelperInter == null) {
            mobHelperInter = (SecureAgentInterHelper) getHelper(AgentMobilityHelper.NAME);
        }
    }
}
