# WELCOME TO JADE TPM SECURITY LIBRARY!

In this repo, i will try to create a **secure library** using **Jade Framework**.
To date, both secure migration and agent cloning have been implemented, either intra-platform or cross-platform.
Also, I'm developing a kind of Onion-based protocol that can take advantage of agents, 
making use of some of Infineon's functionalities.

My implementation Services are in the following dirs:

~~https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureInterTPM~~ *(DEPRECATED)*

* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureCloud (NEW)
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/CloudAgents (NEW)
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureTPM 

Here are some **basic examples** to demonstrate how the library works.

## RASPBERRY:

* START SECURE PLATFORM:

java -cp TPM.jar:test.jar:migration.jar jade.Boot -gui -host localhost -port 8080 -services jade.core.SecureCloud.SecureCloudTPMService\;jade.core.mobility.AgentMobilityService\;jade.core.migration.InterPlatformMobilityService -agents CA:vom.CAPlatform


* START AGENT 2

java -cp commons.jar:TPM.jar:test.jar:migration.jar jade.Boot -container-name P2 -gui -host localhost -port 1364 -mtp jade.mtp.http.MessageTransportProtocol\(http://raspberrypi:36711\) -services jade.core.mobility.AgentMobilityService\;jade.core.migration.InterPlatformMobilityService\;jade.core.SecureInterTPM.SecureInterTPMService\;jade.core.SecureAgent.SecureAgentTPMService -agents A2:vom.CAAgent2

* START AGENT 1

java -cp commons.jar:TPM.jar:test.jar:migration.jar jade.Boot -container-name P1 -gui -host localhost -port 1394 -services jade.core.migration.InterPlatformMobilityService\;jade.core.mobility.AgentMobilityService\;jade.core.SecureInterTPM.SecureInterTPMService\;jade.core.SecureAgent.SecureAgentTPMService -agents A1:vom.CAAgent



### EXECUTE THE FOLLOWING CODE:

* CA PLATFORM:

```
package vom;


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
```

* AGENT 1 EXAMPLE:

```
package vom;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import jade.core.AID;
import jade.core.PlatformID;
import jade.core.SecureAgent.SecureAgentPlatform;


public class CAAgent extends SecureAgentPlatform {
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
			doInitializeAgent(destination,pubKey,"0x81010009","0x8101000a");
			
			System.out.println("IM GOING TO SLEEP");
			Thread.sleep(10000);
			System.out.println("IM GOING TO AWAKE");
					
			AID remoteAMSDestiny = new AID("ams@192.168.0.110:1364/JADE", AID.ISGUID);
			remoteAMSDestiny.addAddresses("http://raspberrypi:45309/acc");
			PlatformID destination2 = new PlatformID(remoteAMSDestiny);
			doSecureMigration(destination2);
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
```

* AGENT 2 EXAMPLE:

```
package vom;


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

```








