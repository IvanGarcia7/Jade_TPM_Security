package jade.core;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.SecureTPM.SecureAgent;
import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import java.util.List;

public class AgentTest extends SecureAgent {

    private List<Location> agencias=new ArrayList<Location>();
    private boolean[] agenciasSeguras={true,true,true,true};
    private int agenciaActual=0;

    public void setup(){
        AID ao = new AID("ams@Dell:2099/JADE",AID.ISGUID);
        ao.addAddresses("http://localhost:8000/acc");
        agencias.add(new PlatformID(ao));

        AID a1 = new AID("ams@Dell:2199/JADE",AID.ISGUID);
        a1.addAddresses("http://localhost:8001/acc");
        agencias.add(new PlatformID(a1));

        AID a2 = new AID("ams@Dell:2299/JADE",AID.ISGUID);
        a2.addAddresses("http://localhost:8002/acc");
        agencias.add(new PlatformID(a2));

        AID a3 = new AID("ams@Dell:2399/JADE",AID.ISGUID);
        a3.addAddresses("http://localhost:8003/acc");
        agencias.add(new PlatformID(a3));

        this.addBehaviour(new Beh1(this));

    }

    public void beforeMove() {
        System.out.println(this.getName()+" : Antes de migrar desde "+this.here());
    }

    public void afterMove() {
        System.out.println(this.getName() + " : Ahora estoy en " + this.here());
        if (agenciaActual == 0) {
            escribeSeguras();
        } else {
            agenciaActual = (agenciaActual + 1) % 2;
            doMove(agencias.get(agenciaActual));
        }
    }

    private void escribeSeguras() {
        for(int i=0;i<4;i++){
            System.out.println("La agencia "+agencias.get(i).getID());
            if(agenciasSeguras[i]){
                System.out.println(" Si es segura");
            }else{
                System.out.println(" No es segura");
            }
        }
    }

    private class Beh1 extends OneShotBehaviour{
        private static final long serialVersionUID = 1L;
        private boolean finished = false;
        public Beh1(Agent a){
            super(a);
        }
        public void action(){
            agenciaActual++;
            doSecureMove(agencias.get(agenciaActual));
        }

    }







}
