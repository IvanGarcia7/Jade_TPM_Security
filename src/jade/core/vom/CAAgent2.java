package jade.core.vom;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import jade.core.AID;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;


public class CAAgent2 extends SecureAgentPlatform {
	public void setup() {
	
		try {
			String publicKeyGen = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxWn5INyIwdQlCFnw+45a91O3LrmRtWkj9mEXcXUViblgTrNEYpuY2HuU7wDn6tIs3WrZcxfNbw8vYKnYGmsCvyua2eqhYQ7AW31Itj+fsOy/XrX5a02aNrqwVOs+Evcx9d9Ap5gWU1XJ2Vl47wsCShxFFhadR2ILZNj5XhTeqMwEalsXcQ+D8DIIyy5eKrgZ1KP79s8Kf2UrVFMADsjt39hM4ajB2F9Pge5m5/tQmt3sBKnGFMf+kaIiHd6INZYJB+5+UdcFzBYzF2PMJpU54kywpIyjZ+xo6RLzMnmlP4lEJnPwKai94mUUV4K9V/fe17DPpEey1SKk7I2DMokNSwIDAQAB";
			byte[] publicBytes = Base64.getDecoder().decode(publicKeyGen);
	        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PublicKey pubKey = keyFactory.generatePublic(keySpec);
			// Build the AID of the corresponding remote platform’s AMS
			AID remoteAMS = new AID("ams@192.168.0.110:8080/JADE", AID.ISGUID);
			// Specify the MTP by setting the transport address of the remote AMS
			remoteAMS.addAddresses("http://raspberrypi:7778/acc");
			// Create the Location object
			PlatformID destination = new PlatformID(remoteAMS);
			doInitializeAgent(destination,pubKey,"0x81010019","0x8101001a");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
