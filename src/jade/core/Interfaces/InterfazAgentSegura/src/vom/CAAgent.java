package jade.core.Interfaces.InterfazAgentSegura.src.vom;

import jade.core.Interfaces.InterfazAgentSegura.src.sample.SecureAgentController;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;


import java.util.List;


public class CAAgent extends SecureAgentPlatform {

    private SecureAgentController gui;
    public List<PlatformID> hops;
    int index = 0;

    public void setup() {
        launchGUI();
    }

    private void launchGUI() {
        gui = new SecureAgentController(this);
    }

    public void afterMove() {

        if(index>=hops.size()){
            System.out.println("END MIGRATION");
        }else{
            PlatformID newDestination = hops.get(index);
            index++;
            doSecureMigration(newDestination);
        }
    }

    public void setHops(List<PlatformID> hopsList){
        hops = hopsList;
    }

}




