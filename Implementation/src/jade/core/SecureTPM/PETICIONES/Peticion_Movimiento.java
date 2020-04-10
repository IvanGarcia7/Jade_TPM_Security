package jade.core.SecureTPM.PETICIONES;

import jade.core.Location;
import jade.core.SecureTPM.SecureAgent;

public class Peticion_Movimiento {

    private SecureAgent nombreAgente;
    private Location localizacion;

    public Peticion_Movimiento(SecureAgent nombre,Location loc){
        nombreAgente=nombre;
        localizacion=loc;
    }

    public SecureAgent getAgent(){
        return nombreAgente;
    }

    public Location getLocation(){
        return localizacion;
    }


}
