package jade.core.GUI;


import jade.core.SecureCloud.SecureCAPlatform;



public class CAPlatform extends SecureCAPlatform{

    private transient CAGui myGUI;

    public void setup() {
        myGUI = new CAGUIImpl(this);
        myGUI.show();
    }
}