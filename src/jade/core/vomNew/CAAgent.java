package jade.core.vomNew;


import javax.swing.JTextArea;

import jade.core.AID;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;


public class CAAgent extends SecureAgentPlatform {
	
	private AgentGui myGUI;
	
	public void setup() {
		myGUI = new AgentGuiImpl(this);
		myGUI.show();
		//this.setGUI(myGUI.getPrinter());
	}
	
	public void afterMove() {
		AID remoteAMSDestiny = new AID("ams@192.168.0.110:1064/JADE", AID.ISGUID);
		remoteAMSDestiny.addAddresses("http://192.168.0.100:36411/acc");
		PlatformID destination2 = new PlatformID(remoteAMSDestiny);
		doSecureMigration(destination2);
	}

}
