package jade.core.SecureTPM.INTER;

import jade.core.*;
import jade.core.SecureTPM.INTERFAZ_TPM.Agencia;
import jade.core.SecureTPM.PETICIONES.PeticionAtestacion;
import jade.core.SecureTPM.PETICIONES.PeticionConfirmacion;
import jade.core.SecureTPM.PETICIONES.Peticion_Clonacion;
import jade.core.SecureTPM.PETICIONES.Peticion_Movimiento;
import jade.core.SecureTPM.SecureAgent;
import jade.core.SecureTPM.INTERFAZ_TPM.Utils_TPM;
import jade.core.mobility.AgentMobilitySlice;
import javafx.util.Pair;

import java.util.*;
import java.util.logging.Level;

public class SecureAgentInterService<Triplet> extends BaseService {

    //Variables que son de utilidad para implementar el protocolo especificado
    private Map<AID, Agent> requestersList=new HashMap<AID,Agent>();

    private List<Peticion_Movimiento> lista_peticiones_movimiento = new ArrayList<>();
    private List<Peticion_Clonacion> lista_peticiones_clonacion = new ArrayList<>();

    private AgentContainer contenedor_actual;

    /**
     * Funcion para inicializar en privado el contenedor actual en caso de que
     * fuera requerido en un futuro.
     */

    public void init(AgentContainer ac, Profile p) throws ProfileException {
        super.init(ac, p);
        contenedor_actual=ac;
    }

    /**
     * Metodo generado por defecto a la hora de establecer un nuevo Servicio
     * @return devuelve el nombre del servicio
     */

    @Override
    public String getName() {
        return SecureAgentIntraHelper.NAME;
    }


    public class SecureAgentIntraHelper implements jade.core.SecureTPM.INTRA.SecureAgentIntraHelper{

        private Agent agent;

        /**
         A continuacion, se define el comportamiento que va a tener el metodo
         para realizar el movimiento del agente de forma segura, dentro de la
         misma agencia.
         */

        public  void doSecureMove(SecureAgent agente, Location destino) {
            /**
             En base a las limitaciones de componentes, se ha definido el comportamiento
             en principio monohilo, es decir, solo se puede realizar una operación de
             movimiento o clonación para los agentes que componen la plataforma.

             Si el número de peticiones es 0, entonces puedo solicitar información al
             TPM para posteriormente, obtener los parametros deseado en cada comando
             vertical definido anteriormente.
             */

            StringBuilder sb = new StringBuilder();

            if (lista_peticiones_movimiento.size()!=0 || lista_peticiones_clonacion.size()!=0) {

                Peticion_Movimiento nueva_peticion = new Peticion_Movimiento(agente,destino);
                lista_peticiones_movimiento.add(nueva_peticion);

                sb.append("Comienza el proceso para mover al agente ").append(agente.getAID());
                System.out.println(sb.toString());
                Utils_TPM.printLog("INICIO DEL SERVICIO PARA MOVER AL AGENTE", Level.INFO,true,this.getClass().getName());
                //A PARTIR DE ESTE PUNTO, INICIO EL PROCESO PARA REALIZAR EL MOVIMIENTO DEL AGENTE
                //EN BASE A LA CONFIGURACION ESTABLECIDA ANTERIORMENTE, BLOQUEO QUE SE PUEDA HACER ESTE PROCESO
                //DE FORMA SIMULTANEA ENTRE AGENTES. CREO UN NUEVO COMANDO, SIGUIENDO LA DOCUMENTACIÓN
                GenericCommand comando = new GenericCommand(SecureAgentIntraHelper.REQUEST_ATTESTATION,SecureAgentIntraHelper.NAME,"PETICION DE ATESTACION ENVIADA");
                //SE VA A ENVIAR UN NUEVO OBJETO CON DATOS COMO EL NOMBRE DEL AGENTE, LA LOCALIZACIÓN Y LA AGENCIA
                PeticionAtestacion paquete = new PeticionAtestacion(agente.getAID(),agente.here(),destino,agente.getAMS());
                comando.addParam(paquete);
                //Envio el comando al servicio que lo consuma posteriormente.
                Utils_TPM.printLog("AGENTE SOLICITA MOVERSE Y MANDA PETICION A LA AGENCIA", Level.INFO,true,this.getClass().getName());
                try{
                    SecureAgentInterService.this.submit(comando);
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


        public void doSecureClone(SecureAgent agente, Location destino, String nombre_agente) {
            /**
             En base a las limitaciones de componentes, se ha definido el comportamiento
             en principio monohilo, es decir, solo se puede realizar una operación de
             movimiento o clonación para los agentes que componen la plataforma.

             Si el número de peticiones es 0, entonces puedo solicitar información al
             TPM para posteriormente, obtener los parametros deseado en cada comando
             vertical definido anteriormente.
             */

            StringBuilder sb = new StringBuilder();

            if (lista_peticiones_movimiento.size()>0 || lista_peticiones_clonacion.size()>0) {

                Peticion_Clonacion nueva_peticion = new Peticion_Clonacion(agente,destino,nombre_agente);
                lista_peticiones_clonacion.add(nueva_peticion);

                sb.append("Comienza el proceso para clonar al agente ").append(agente.getAID());
                System.out.println(sb.toString());
                Utils_TPM.printLog("INICIO DEL SERVICIO PARA CLONAR AL AGENTE", Level.INFO,true,this.getClass().getName());
                //A PARTIR DE ESTE PUNTO, INICIO EL PROCESO PARA REALIZAR EL MOVIMIENTO DEL AGENTE
                //EN BASE A LA CONFIGURACION ESTABLECIDA ANTERIORMENTE, BLOQUEO QUE SE PUEDA HACER ESTE PROCESO
                //DE FORMA SIMULTANEA ENTRE AGENTES. CREO UN NUEVO COMANDO, SIGUIENDO LA DOCUMENTACIÓN
                GenericCommand comando = new GenericCommand(SecureAgentIntraHelper.REQUEST_ATTESTATION,SecureAgentIntraHelper.NAME,"PETICION DE ATESTACION ENVIADA");
                //SE VA A ENVIAR UN NUEVO OBJETO CON DATOS COMO EL NOMBRE DEL AGENTE, LA LOCALIZACIÓN Y LA AGENCIA
                PeticionAtestacion paquete = new PeticionAtestacion(agente.getAID(),agente.here(),destino,agente.getAMS());
                comando.addParam(paquete);
                //Envio el comando al servicio que lo consuma posteriormente.
                Utils_TPM.printLog("AGENTE SOLICITA CLONARSE Y MANDA PETICION A LA AGENCIA", Level.INFO,true,this.getClass().getName());
                try{
                    SecureAgentInterService.this.submit(comando);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            else {
                sb.append("NO HA SIDO POSIBLE DAR COMIENZO AL PROCESO PARA CLOANR EL AGENTE ").append(agente.getAID()).append(" TPM OCUPADO CON OTRAS PETICIONES");
                Utils_TPM.printLog("NO ES POSIBLE EJECUTAR EL SERVICIO PARA CLONAR AL AGENTE, DEMASIADAS PETICIONES", Level.INFO,true,this.getClass().getName());
                agente.doSecureCloneError(sb.toString());
            }

        }

        @Override
        public void init(Agent agente) {
            agent=agente;
        }



    }

    private class CommandTargetSink implements Sink {

        @Override
        public void consume(VerticalCommand cmd) {
            try {
                String name = cmd.getName();

                if(name.equals(jade.core.SecureTPM.INTRA.SecureAgentIntraHelper.REQUEST_ATTESTATION)) {
                    ManejadorPeticionAtestacion(cmd);
                }
                else if(name.equals(jade.core.SecureTPM.INTRA.SecureAgentIntraHelper.REQUEST_CONFIRMATION)) {
                    ManejadorPeticionRespuesta(cmd);
                }
            }catch(Exception ioe){
                ioe.printStackTrace();
            }
        }

        private void ManejadorPeticionRespuesta(VerticalCommand comando) throws NotFoundException {
            /**
             * En este punto, el agente que emitio la peticion de atestacion origen recible la confirmacion por parte
             * de la localizacion destino establecida y lo que hace es simplemente iniciar el proceso de movida seguro
             * llamando al metodo antiguo de jade que se ha modificado para la ocasion.
             */
            Utils_TPM.printLog("SE HA RECIBIDO UN COMANDO VERTICAL CON LA CONFIRMACION DE LA PETICION DE ATESTACION", Level.INFO, true, this.getClass().getName());
            Object[] parametros = comando.getParams();
            PeticionConfirmacion paquete_confirmacion = (PeticionConfirmacion) parametros[0];
            Utils_TPM.printLog("PETICION ACEPTADA, SE PROCEDE A EJECUTAR EL METODO DOMOVE", Level.INFO, true, this.getClass().getName());
            SecureAgent agente_preparado =((SecureAgent) requestersList.get(paquete_confirmacion.getNameAgent()));

            //CON LA OPERACION DOMOVE SE HA SOBRECARCADO CON EL OBJETICO DE QUE SI INTRODUCIMOS UNA ESPECIDE DE FLAG DENTRO DE ESTE PARAMETRO,
            //EJECUTE LA OPERACION DEFINIDA PARA SECUIZAR DICHO PROCESO MIENTRAS QUE EN OTRO CASO HAGA USO DEL COMANDO ESTABLCEDIDO COMO POR DEFECTO

            if(lista_peticiones_movimiento.size()>0){
                /*
                    Llegados a este punto tengo que ejecutar el metodo rediseñado conocido como doMoveSecurity
                */
                Peticion_Movimiento peticion = lista_peticiones_movimiento.get(0);
                SecureAgent sa = peticion.getAgent();
                Location sl = peticion.getLocation();
                sa.doMoveSecurityMod(sl);
                lista_peticiones_movimiento.clear();
            }else if(lista_peticiones_clonacion.size()>0){
                /*
                    Llegados a este punto tengo que ejecutar el metodo rediseñado conocido como doCloneSecurity
                */
                Peticion_Clonacion peticion = lista_peticiones_clonacion.get(0);
                SecureAgent sa = peticion.getAgent();
                Location sl = peticion.getLocation();
                String sn = peticion.getString();
                sa.doCloneSecurityMod(sl,sn);
                lista_peticiones_clonacion.clear();
            }
        }

        private void ManejadorPeticionAtestacion(VerticalCommand comando) throws ServiceException {
            /**
             * En este punto, llega al agente destino el mensaje / petición que fue enviado por el nodo
             * origen y lo trata en base a los requerimientos que vea oportuno.
             */
            Utils_TPM.printLog("SE HA RECIBIDO UN COMANDO VERTICAL PARA CONFIRMACION PETICION ATESTACION", Level.INFO, true, this.getClass().getName());
            Object[] parametros = comando.getParams();
            PeticionAtestacion paquete_confirmacion = (PeticionAtestacion) parametros[0];
            PeticionConfirmacion peticion_confirmacion = new PeticionConfirmacion(paquete_confirmacion.getNameAgent(),paquete_confirmacion.getLocationdestino(),paquete_confirmacion.getLocationorigen(),paquete_confirmacion.getAMS());
            GenericCommand comando_generado = new GenericCommand(SecureAgentIntraHelper.REQUEST_CONFIRMATION, SecureAgentIntraHelper.NAME, null);
            SecureAgentInterSlice objetivo_confirmacion = (SecureAgentInterSlice) getSlice(paquete_confirmacion.getLocationorigen().getName());
            comando_generado.addParam(peticion_confirmacion);
            try {
                Utils_TPM.printLog("PETICION VALIDADA", Level.INFO, true, this.getClass().getName());
                objetivo_confirmacion.doRequestConfirmation(comando_generado);
            } catch (Exception ie) {
                ie.printStackTrace();
                objetivo_confirmacion = (SecureAgentInterSlice) getFreshSlice(paquete_confirmacion.getLocationorigen().getName());
                objetivo_confirmacion.doRequestConfirmation(comando_generado);
            }
        }
    }


    private class CommandSourceSink implements Sink {

        //Funcion para consumir los comandos verticales recibidos por los diversos agentes
        public void consume(VerticalCommand comando) {
            //A la hora de gestionar un comando vertical, debo tratar múltiples excepciones que pueden llegar a suceder
            try {
                String tipo_peticion = comando.getName();
                if (tipo_peticion.equals(SecureAgentIntraHelper.REQUEST_ATTESTATION)) {
                    manejador_peticion_atestacion(comando);
                }
            } catch (Exception e) {
                IMTPException ie = new IMTPException("Error en SecureAgentIntra a la hora de tratar el comando vertical");
                comando.setReturnValue(ie);
            }
        }

        private void manejador_peticion_atestacion(VerticalCommand comando) throws ServiceException {
            //En primer lugar obtengo los parametros incluidos dentro del comando vertical
            Object[] parametros = comando.getParams();
            PeticionAtestacion paquete = (PeticionAtestacion) parametros[0];
            SecureAgentInterSlice objetivo = (SecureAgentInterSlice) getSlice(paquete.getLocationdestino().getName());
            try {
                objetivo.doRequestAttestation(comando);
            } catch (Exception ie) {
                ie.printStackTrace();
                Agencia.setStatus(false);
            }

        }
    }

    private class ServiceComponent implements Service.Slice{

        @Override
        public Service getService() {
            return SecureAgentInterService.this;
        }

        @Override
        public Node getNode() throws ServiceException, IMTPException {
            return SecureAgentInterService.this.getLocalNode();
        }

        //Funcion para capturar los comandos y posteriormente tratar los mismos
        public VerticalCommand serve(HorizontalCommand comando){
            GenericCommand comando_recibido = null;
            try{
                Object[] argumentos = comando.getParams();
                if(comando.getName().equals(SecureAgentInterSlice.REMOTE_REQUEST_ATTESTATION)){
                    PeticionAtestacion paquete = (PeticionAtestacion) argumentos[0];
                    Utils_TPM.printLog("EL AGENTE DESTINO HA RECIBIDO LA PETICION DE ATESTACION DE "+paquete.getNameAgent(), Level.INFO,true,this.getClass().getName());
                    comando_recibido = new GenericCommand(SecureAgentIntraHelper.REQUEST_ATTESTATION,SecureAgentIntraHelper.NAME,"Peticion recibida");
                    comando_recibido.addParam(paquete);
                }else if(comando.getName().equals(SecureAgentInterSlice.REMOTE_REQUEST_CONFIRMATION)){
                    PeticionConfirmacion paquete = (PeticionConfirmacion) argumentos[0];
                    Utils_TPM.printLog("EL AGENTE DESTINO HA RECIBIDO LA PETICION DE CONFIRMACION DE "+paquete.getNameAgent(), Level.INFO,true,this.getClass().getName());
                    comando_recibido = new GenericCommand(SecureAgentIntraHelper.REQUEST_CONFIRMATION,SecureAgentIntraHelper.NAME,"Peticion recibida");
                    comando_recibido.addParam(paquete);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return comando_recibido;
        }
    }


}
