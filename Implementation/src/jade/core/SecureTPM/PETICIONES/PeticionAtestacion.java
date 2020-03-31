package jade.core.SecureTPM.PETICIONES;

import jade.core.AID;
import jade.core.Location;

public class PeticionAtestacion {

    /**
       Este objeto será el que se va a enviar a otro agente, a la hora de enviar el primer
       mensaje de solicitud de atestacion.
    */

    private AID nombreAgente;
    private AID nombreAms;
    private Location localizacion;
    private String descripcion;

    public PeticionAtestacion(AID Agente, AID AMS, Location local, String mensaje){
        nombreAgente=Agente;
        nombreAms=AMS;
        localizacion=local;
        descripcion=mensaje;
    }

    public PeticionAtestacion(AID Agente, Location local){
        nombreAgente=Agente;
        localizacion=local;
    }

    public AID getNameAgent(){
        return nombreAgente;
    }

    public AID getNombreAms(){
        return nombreAms;
    }

    public Location getLocation(){
        return localizacion;
    }

    public String getDescripcion(){
        return descripcion;
    }









}
