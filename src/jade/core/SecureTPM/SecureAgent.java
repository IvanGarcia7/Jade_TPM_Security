package jade.core.SecureTPM;

import jade.core.*;
import jade.core.SecureInterTPM.SecureInterTPMHelper;
import jade.core.SecureIntraTPM.SecureIntraTPMHelper;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SecureAgent extends Agent {
    private static final long serialVersionUID = 9058618378207435614L;

    //Create the service to call this later
    private transient SecureIntraTPMHelper mobHelperIntra;
    private transient SecureInterTPMHelper mobHelperInter;

    public final void doMoveOld(Location destiny){
        super.doMove(destiny);
    }

    public final void doCloneOld(Location destiny, String name){
        super.doClone(destiny, name);
    }

    public void doSecureMove(Location destino) {
        //System.out.println("Estoy dentro del metodo que acabo de llamar");
        try {
            if(destino instanceof ContainerID){
                //System.out.println("Estoy en el primero");
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA MOVER AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                        .append("Agente: "+this.getAID()+"\n")
                        .append("A LA AGENCIA ACTUAL "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Agencia.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO",
                                 Level.INFO,SecureIntraTPMHelper.DEBUG,this.getClass().getName());
                initmobHelperIntra();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperIntra.doMoveTPM(this,destino);
            }else{
                //System.out.println("Estoy en el primero");
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA MOVER AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                        .append("Agente: "+this.getAID()+"\n")
                        .append("A LA AGENCIA ACTUAL "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Agencia.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO",
                        Level.INFO,SecureIntraTPMHelper.DEBUG,this.getClass().getName());
                initmobHelperInter();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperInter.doMoveTPM(this,destino);
            }
        }
        catch(ServiceException se) {
            // FIXME: Log a proper warning
            return;
        }
    }

    public void doSecureClone(Location destino, String nombre_agente) {
        try {
            if(destino instanceof ContainerID){
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA CLONAR AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                        .append("Agente: "+this.getAID()+"\n")
                        .append("A LA AGENCIA ACTUAL "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Agencia.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO", Level.INFO,
                                 SecureIntraTPMHelper.DEBUG,this.getClass().getName());
                initmobHelperIntra();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperIntra.doCloneTPM(this, destino, nombre_agente);
            }else{
                StringBuilder sb = new StringBuilder();
                sb.append("SE HA REGISTRADO UNA PETICION PARA CLONAR AL AGENTE CON LOS SIGUIENTES DATOS:\n")
                        .append("Agente: "+this.getAID()+"\n")
                        .append("A LA AGENCIA ACTUAL "+destino.getName()+" CON DIRECCION "+destino.getAddress());
                //Mensaje nivel detalle y clase
                Agencia.printLog("COMIENZO DE LA EJECUCION DEL SERVICIO", Level.INFO,
                        SecureIntraTPMHelper.DEBUG,this.getClass().getName());
                initmobHelperInter();
                //Se llama al Helper que gestiona la migración en el mismo contenedor.
                mobHelperInter.doCloneTPM(this, destino, nombre_agente);
            }
        }
        catch(ServiceException se) {
            // FIXME: Log a proper warning
            return;
        }
    }

    public void doSecureSend(){
        AMSAgentDescription[] agents = null;
        List<AID> confirmagents = new ArrayList<AID>();
        try {
            SearchConstraints c = new SearchConstraints();
            c.setMaxResults ( new Long(-1) );
            agents = AMSService.search( this, new AMSAgentDescription (), c );
        }catch(Exception e) {
            e.printStackTrace();
        }

        AID myID = getAID();
        for (int i=0; i<agents.length;i++)
        {
            AID agentID = agents[i].getName();
            System.out.println(( agentID.equals( myID ) ? "*** " : "    ")+ i + ": " + agentID.getName());
            if(agentID.getName().contains("Server")) {
                confirmagents.add(agentID);
            }
        }

        for(int l=0; l<confirmagents.size();l++) {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent( "HELLO SERVER" );
            msg.addReceiver(confirmagents.get(l));
            send(msg);
            System.out.println("ACK SENDED TO THE SERVER CORRECTLY, WAITING THE KEY");

            ACLMessage mensaje = blockingReceive();
            System.out.println("I HAVE RECEIVED FROM THE TRACKING NODE: "+mensaje.getContent());
            System.out.println("KEY FROM THE SERVER OBTAINED, ENCRYPTING THE MESSAGE AND SEND TO THE SERVER");

            ACLMessage reply = mensaje.createReply();
            reply.setPerformative( ACLMessage.INFORM );
            double publicKey = Math.random()*6 ;
            reply.setContent(Double.toString(publicKey));
            send(reply);
        }
    }



    //Function to print if appear one error in the secure move
    public void doSecureMoveError(String ms){
        System.out.println(ms);
    }

    //Function to print if appear one error in the secure clonation
    public void doSecureCloneError(String ms){
        System.out.println(ms);
    }

    //Call this function to inicialize the service
    private void initmobHelperIntra() throws ServiceException {
        if(mobHelperIntra == null){
            mobHelperIntra = (SecureIntraTPMHelper) getHelper(SecureIntraTPMHelper.NAME);
            System.out.println(mobHelperIntra);
        }
    }

    //Call this function to inicialize the service
    private void initmobHelperInter() throws ServiceException {
        if(mobHelperInter == null){
            mobHelperInter = (SecureInterTPMHelper) getHelper(SecureInterTPMHelper.NAME);
            System.out.println(mobHelperInter);
        }
    }


}
