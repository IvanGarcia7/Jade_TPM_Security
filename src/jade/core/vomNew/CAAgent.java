package jade.core.vomNew;


import javax.swing.JTextArea;
import jade.core.SecureAgent.SecureAgentPlatform;


public class CAAgent extends SecureAgentPlatform {

	private AgentGui myGUI;

	public void setup() {
		myGUI = new AgentGuiImpl(this);
		myGUI.show();
		this.setGUI(myGUI);
	}

}
