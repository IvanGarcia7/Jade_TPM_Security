package jade.core.GUI;

import javax.swing.JTextArea;

public interface AgentGui {
	void show();
	void hide();
	void dispose();

	JTextArea getPrinterStart();
	JTextArea getPrinterHops();
	JTextArea getPrinterInformation();
}