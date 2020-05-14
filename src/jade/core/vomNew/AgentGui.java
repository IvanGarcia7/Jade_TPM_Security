package jade.core.vomNew;

import javax.swing.JTextArea;

public interface AgentGui {
	void setAgent(CAAgent a);
	void show();
	void hide();
	void notifyUser(String message);
	void dispose();
	JTextArea getPrinter();
}