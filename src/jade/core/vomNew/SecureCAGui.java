package jade.core.vomNew;

import jade.core.vomNew.CAPlatform;

import javax.swing.JTextArea;

public interface SecureCAGui {
	void setAgent(CAPlatform a);
	void show();
	void hide();
	void notifyUser(String message);
	void dispose();
	JTextArea getPrinter();
}