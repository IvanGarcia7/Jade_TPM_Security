package jade.core.vomNew;


import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import jade.core.SecureCloud.SecureCAPlatform;


public class CAPlatform extends SecureCAPlatform{
	
	private jade.core.vomNew.SecureCAGui myGUI;
	
	public void setup() {
		
		myGUI = new jade.core.vomNew.SecureCAGuiImpl(this);
		myGUI.show();
		this.setGUI(myGUI.getPrinter());
	
	}
}