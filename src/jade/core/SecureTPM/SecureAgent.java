package jade.core.SecureTPM;

import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.SecureIntraTPM.SecureIntraTPMHelper;
import jade.core.SecureIntraTPM.SecureIntraTPMService;
import jade.core.ServiceException;
import jade.core.mobility.Movable;


import java.util.logging.Level;

public class SecureAgent extends Agent {
    private static final long serialVersionUID = 9058618378207435614L;

    //Create the service to call this later
    private transient SecureIntraTPMHelper mobHelperIntra;

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

            }
        }
        catch(ServiceException se) {
            // FIXME: Log a proper warning
            return;
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


}
