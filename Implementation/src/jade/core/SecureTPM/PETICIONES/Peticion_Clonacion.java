package jade.core.SecureTPM.PETICIONES;

import jade.core.Location;
import jade.core.SecureTPM.SecureAgent;

public class Peticion_Clonacion {

    private SecureAgent nombreAgente;
    private Location localizacion;
    private String nombreAgenteNuevo;

    public Peticion_Clonacion(SecureAgent nombre,Location loc,String nuevonombre){
        nombreAgente=nombre;
        localizacion=loc;
        nombreAgenteNuevo=nuevonombre;
    }

    public SecureAgent getAgent(){
        return nombreAgente;
    }

    public Location getLocation(){
        return localizacion;
    }

    public String getString(){
        return nombreAgenteNuevo;
    }



}
