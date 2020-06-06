package jade.core.GUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jade.core.Agent;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;
import jade.core.SecureTPM.Pair;


public class CAAgent extends SecureAgentPlatform implements Serializable {


	// private static final long serialVersionUID = 91162339198848092L;
	public List<Pair<PlatformID,String>> hops;
	int index = 0;



/*
    public void setup() {
        launchGUI(this);
    }
  */


	private transient AgentGui myGUI;

	public void setup() {
		myGUI = new AgentGuiImpl(this);
		myGUI.show();
	}




	public void afterMove() {

		if(index>=hops.size()){
			System.out.println("END MIGRATION");
		}else{
			PlatformID newDestination = hops.get(index).getKey();
			index++;
			doSecureMigration(newDestination);
		}


	}

	public void setHops(List<Pair<PlatformID,String>> hopsList){
		hops = hopsList;
	}

	public void dale(PlatformID destiny) {


		System.out.println("hola" + this);
		doMove(destiny);
	}




}











