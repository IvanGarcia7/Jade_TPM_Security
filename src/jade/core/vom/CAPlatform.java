package jade.core.vom;


import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import jade.core.SecureCloud.SecureCAPlatform;


public class CAPlatform extends SecureCAPlatform{
	public void setup() {
		try {
			String publicKeyGen = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxWn5INyIwdQlCFnw+45a91O3LrmRtWkj9mEXcXUViblgTrNEYpuY2HuU7wDn6tIs3WrZcxfNbw8vYKnYGmsCvyua2eqhYQ7AW31Itj+fsOy/XrX5a02aNrqwVOs+Evcx9d9Ap5gWU1XJ2Vl47wsCShxFFhadR2ILZNj5XhTeqMwEalsXcQ+D8DIIyy5eKrgZ1KP79s8Kf2UrVFMADsjt39hM4ajB2F9Pge5m5/tQmt3sBKnGFMf+kaIiHd6INZYJB+5+UdcFzBYzF2PMJpU54kywpIyjZ+xo6RLzMnmlP4lEJnPwKai94mUUV4K9V/fe17DPpEey1SKk7I2DMokNSwIDAQAB";
			String privateKeyGen = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDFafkg3IjB1CUIWfD7jlr3U7cuuZG1aSP2YRdxdRWJuWBOs0Rim5jYe5TvAOfq0izdatlzF81vDy9gqdgaawK/K5rZ6qFhDsBbfUi2P5+w7L9etflrTZo2urBU6z4S9zH130CnmBZTVcnZWXjvCwJKHEUWFp1HYgtk2PleFN6ozARqWxdxD4PwMgjLLl4quBnUo/v2zwp/ZStUUwAOyO3f2EzhqMHYX0+B7mbn+1Ca3ewEqcYUx/6RoiId3og1lgkH7n5R1wXMFjMXY8wmlTniTLCkjKNn7GjpEvMyeaU/iUQmc/ApqL3iZRRXgr1X997XsM+kR7LVIqTsjYMyiQ1LAgMBAAECggEBALmHEYmvmJrInBEQLejL7XOY6oPwBB8Ac7q9noGTLq2jWP49fZxKbMeuxNYk/M/zvBROsZN3oCqqk7T9icmyHf+5pCr+VbHYYjDZOjeE7bghluwUixYr9S1QIB1+g3ThecN/j8yxovGooy0v5/aHWxx5GvhaQm9ulhSt0RV4+ZSZmSW0T6zj+ukmRIQlBNnEssRG37Pvjm8aWCkYk6MkeOBau6z+MtglRlmYHDtvRFTaGRVMM+nn4G/v3TyLZjsFzDRUol45kRFCHl01lgpZhfocVGGq9rtuhORdX0x1YZ8lzdKQZdhuNsfWynIqP839b/XqTFZg0k6b1IYrAKrsI4kCgYEA54Io9AvEaugranylZWbMoUtOzW1XXZEbpcrKjZpF+WDoQqxKrSK406zC52mQFr22XGs62jKhtkg4n71+t5L/nxKwpDQaI8K0sBm41D6kluX0vlKBzi1PUdVAhW/mTbQQVAT7HIIEkvSk2n7iVoL8OuQuxA0eD4Hv2X3QS9F8NM8CgYEA2kxykvuryTXXeA/CCBH75zodzjMVN0irjwqM4yVgXF/mHkkHLbFMS4O1uccIvWkyMYMkG8tbPyGBOrvyUpBj91KQHARs4Nxwycg+xD/5tobW4e59Xc7tX/P2MTfHzvt+zYoLEM828H4xibd9CvpAu7uiyet+Ate1rTYFrBnTdsUCgYEAtUw2PCA2+q3EykjA0e82Ux4BoVh+cZTgvO5Zv5jQMaJVYVlBxeKKMaJ6o/UEVPrOpAOJfDTodTKLvXUNlj91FaLyWDVBPz4MeFg8aWKBTHbbOmysoMTU+DXzqEvgZHudyd54tHORl3Ak5cM2Bx/e3VOy1++Z2fUc2wrvI2DzTvsCgYEAymMF9m+OHMXmNlGluoWx3fZrm7iQeujM5ZkKda05YWsnlfxuw1YVPg2mdc6ps7HL70t+NqaaujT98s49I37qr2nEYbVYnEDD8M/OIXSOt0LcBitt34LxYHx2S7tDbAqOzVCmYbc7YEwe8WdEbuEFEwgrLLk/4rLKpQPozl5DR2ECgYA57KTO9XCAng3LkDKVZAam9k5xqFjy4fwj/GRM+1X4+cpzhLYM4Qini65jSxeWs07Z6fA2EwDW2PiL2xrDWuQNc/Iq370X/bOYRsm7313pslTcFvzqt0f0LW0C2A5DK3oS65GDU9TMivfroA0Ix7fquxDAnrRY8jqAHtt/BMNiJA==";
			byte[] publicBytes = Base64.getDecoder().decode(publicKeyGen);
	        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PublicKey pubKey = keyFactory.generatePublic(keySpec);
	        
	        byte[] privateBytes = Base64.getDecoder().decode(privateKeyGen);
	        PKCS8EncodedKeySpec keySpecPriv = new PKCS8EncodedKeySpec(privateBytes);
	        KeyFactory keyFactoryPriv = KeyFactory.getInstance("RSA");
	        PrivateKey pubKeyPriv = keyFactoryPriv.generatePrivate(keySpecPriv);
		
			doInitializeCA(pubKeyPriv,pubKey);
			//doListCA();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}