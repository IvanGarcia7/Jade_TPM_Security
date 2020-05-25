package jade.core.Interfaces.InterfazCASegura.src.vom;

import jade.core.Interfaces.InterfazCASegura.src.sample.SecureCAController;
import jade.core.SecureCloud.SecureCAPlatform;



public class CAPlatform extends SecureCAPlatform {

    private SecureCAController gui;

    public void setup(){
        launchGUI();
    }

    private void launchGUI() {
        gui = new SecureCAController(this);
    }


}
