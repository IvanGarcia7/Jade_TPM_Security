package jade.core.GUI;


import jade.core.SecureCloud.SecureCAPlatform;


public class CAPlatform extends SecureCAPlatform{
	
	private jade.core.GUI.SecureCAGui myGUI;
	
	public void setup() {
		
		myGUI = new jade.core.GUI.SecureCAGuiImpl(this);
		myGUI.show();
		this.setGUI(myGUI.getPrinter());
	
	}
}