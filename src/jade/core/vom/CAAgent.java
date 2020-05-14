package jade.core.vom;


import javax.swing.JTextArea;
import jade.core.SecureAgent.SecureAgentPlatform;


public class CAAgent extends SecureAgentPlatform {
	
	private AgentGui myGUI;
	
	public void setup() {
		myGUI = new AgentGuiImpl(this);
		myGUI.show();
		this.setGUI(myGUI.getPrinter());
	}

	public void setPrinter(JTextArea logTA) {
		// TODO Auto-generated method stub
		
	}
}
