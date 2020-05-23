package jade.core.vomNew;

import javax.swing.JTextArea;

public interface AgentGui {
	void setAgent(jade.core.vomNew.CAAgent a);
	void show();
	void hide();
	void notifyUser(String message);
	void dispose();
	JTextArea getPrinter();
}