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
    private Location localizacion_origen;
    private Location localizacion_destino;
    private String descripcion;

    public PeticionAtestacion(AID agente_emisor, Location direccion_emisor, Location direccion_destino, AID AMS_origen) {
        nombreAgente = agente_emisor;
        nombreAms = AMS_origen;
        localizacion_origen = direccion_emisor;
        localizacion_destino = direccion_destino;
    }

    //SETTER METHODS

    public void setNameAgent(AID nombre_nuevo){
        nombreAgente=nombre_nuevo;
    }

    public void setNombreAms(AID nombre_nuevo_AMS){ nombreAms = nombre_nuevo_AMS; }

    public void setLocationorigen(Location nueva_localizacion){ localizacion_origen = nueva_localizacion; }

    public void setLocationdestino(Location nueva_localizacion){ localizacion_destino = nueva_localizacion; }

    public void setDescripcion(String nueva_descripcion){
        descripcion = nueva_descripcion;
    }

    public void setAMS(AID nuevoAMS){ nombreAms = nuevoAMS; }

    //GETTER METHODS

    public AID getNameAgent(){
        return nombreAgente;
    }

    public AID getNombreAms(){
        return nombreAms;
    }

    public Location getLocationorigen(){ return localizacion_origen; }

    public Location getLocationdestino(){ return localizacion_destino; }

    public String getDescripcion(){
        return descripcion;
    }

    public AID getAMS(){
        return nombreAms;
    }

}
