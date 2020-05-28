package jade.core.GUI;

import javax.swing.*;

public interface CAGui {
    void show();
    void hide();
    void dispose();

    JTextArea getPrinterStart();
    JTextArea getPrinterList();
    JTextArea getPrinterCRUD();
    JTextArea getPrinterInformation();
}
