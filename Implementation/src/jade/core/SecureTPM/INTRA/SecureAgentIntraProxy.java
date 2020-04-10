package jade.core.SecureTPM.INTRA;

import jade.core.*;
import jade.core.SecureTPM.INTER.SecureAgentInterSlice;
import jade.core.SecureTPM.INTERFAZ_TPM.Utils_TPM;
import jade.core.mobility.AgentMobilitySlice;

import java.util.logging.Level;

public class SecureAgentIntraProxy extends Service.SliceProxy implements SecureAgentIntraSlice {


    @Override
    public void doRequestAttestation(VerticalCommand comando) {
        try{
            GenericCommand comando_nuevo = new GenericCommand(SecureAgentIntraSlice.REMOTE_REQUEST_ATTESTATION, SecureAgentIntraHelper.NAME, null);
            comando_nuevo.addParam(comando.getParams()[0]);
            Node nodo = getNode();
            Utils_TPM.printLog("ENVIANDO LA PETICION DE ATESTACION A TRAVES DE UN COMANDO HORIZONTAL AL NODO "+nodo.getName(), Level.INFO,true,this.getClass().getName());
            nodo.accept(comando_nuevo);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doRequestConfirmation(VerticalCommand comando) {
        try{
            GenericCommand comando_nuevo = new GenericCommand(SecureAgentIntraSlice.REMOTE_REQUEST_CONFIRMATION, SecureAgentIntraHelper.NAME, null);
            comando_nuevo.addParam(comando.getParams()[0]);
            Node nodo = getNode();
            Utils_TPM.printLog("ENVIANDO LA CONFIRMACION DE ATESTACION A TRAVES DE UN COMANDO HORIZONTAL AL NODO "+nodo.getName(), Level.INFO,true,this.getClass().getName());
            nodo.accept(comando_nuevo);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


}
