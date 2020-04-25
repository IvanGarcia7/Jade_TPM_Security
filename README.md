# WELCOME TO JADE TPM SECURITY LIBRARY!

In this repo, i will try to create a **secure library** using **Jade Framework**.
To date, both secure migration and agent cloning have been implemented, either intra-platform or cross-platform.
Also, I'm developing a kind of Onion-based protocol that can take advantage of agents, 
making use of some of Infineon's functionalities.

My implementation Services are in the following dirs:
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureTPM
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureIntraTPM
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureInterTPM
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureOnionTPM

* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/D4rkPr0j3cT (NEW)
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/D4rkPr0j3cTPlatforms (NEW)

Here are some **basic examples** to demonstrate how the library works.

## EXAMPLE INTRA PLATFORM:

* java -cp /Users/ivan/Desktop/D3fc0M.jar jade.Boot -container-name Pruebas1 -gui -service jade.core.mobility.AgentMobilityService;jade.core.SecureIntraTPM.SecureIntraTPMService;

* java -cp /Users/ivan/Desktop/D3fc0M.jar jade.Boot -container -container-name pruebas2  -services jade.core.mobility.AgentMobilityService;jade.core.SecureIntraTPM.SecureIntraTPMService;

* java -cp /Users/ivan/Desktop/D3fc0M.jar jade.Boot -container -container-name pruebas3  -services jade.core.mobility.AgentMobilityService;jade.core.SecureIntraTPM.SecureIntraTPMService;

## INTRA PLATFORM PROTOCOL:

### EXECUTE THE FOLLOWING CODE:

```
package org;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.SecureTPM.SecureAgent;
import jade.core.behaviours.OneShotBehaviour;

public class AgenteSeguro extends SecureAgent{
	private List<Location> agencias = new ArrayList<Location>();
	private int agenciaActual = 0;
	public void setup() {
		//Contenedor1 definido correctamente
		ContainerID destination1 = new ContainerID("Pruebas1",null);
		//destination1.setAddress("192.168.0.101");
		agencias.add(destination1);
		//Contenedor2 definido correctamente
		ContainerID destination2 = new ContainerID("pruebas2",null);
		//destination2.setAddress("192.168.0.101");
		agencias.add(destination2);
		//Contenedor3 definido correctamente
		ContainerID destination3 = new ContainerID("pruebas3",null);
		//destination3.setAddress("192.168.0.101");
		agencias.add(destination3);
		//this.addBehaviour(new Beh1(this));
		doSecureMove(destination3);	
	}
	public void beforeMove() {
		System.out.println("SECURE SERVICE EXECUTE CORRECTLY ;)");
	}
	
	public void afterMove() {
		System.out.println("************************");
	}
}
```

## EXAMPLE INTRA PLATFORM:

* java -cp /Users/ivan/Desktop/D3fc0M.jar jade.Boot -gui -host localhost -port 4333 -accept-foreign-agents true -services jade.core.migration.InterPlatformMobilityService;jade.core.mobility.AgentMobilityService;jade.core.SecureInterTPM.SecureInterTPMService;jade.core.SecureIntraTPM.SecureIntraTPMService;

* java -cp /Users/ivan/Desktop/D3fc0M.jar jade.Boot -container -container-name Pruebas1 -host localhost -port 4333 -accept-foreign-agents true -services jade.core.migration.InterPlatformMobilityService;jade.core.mobility.AgentMobilityService;jade.core.SecureInterTPM.SecureInterTPMService;jade.core.SecureIntraTPM.SecureIntraTPMService;

* java -cp /Users/ivan/Desktop/D3fc0M.jar jade.Boot -gui -host localhost -port 5333 -accept-foreign-agents true -agents RASTREADOR:vom.Rastreator -services jade.core.migration.InterPlatformMobilityService;jade.core.mobility.AgentMobilityService;jade.core.SecureInterTPM.SecureInterTPMService;jade.core.SecureIntraTPM.SecureIntraTPMService;

### EXECUTE THE FOLLOWING CODE:

```
package vom;
import java.io.Serializable;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.PlatformID;
import jade.core.SecureTPM.SecureAgent;


public class Rastreator extends SecureAgent implements Serializable{
	private static final long serialVersionUID = -2241822868465327314L;
	public void setup() {
		AID remoteAMS = new AID("ams@192.168.0.100:4333/JADE", AID.ISGUID);
		remoteAMS.addAddresses("http://192.168.0.100:7778/acc");
		//this.addBehaviour(new Beh1(this));
		PlatformID destination = new PlatformID(remoteAMS);
		doSecureMove(destination);
	}
	public void beforeMove() {
		System.out.println("SECURE SERVICE EXECUTE CORRECTLY ;)");
	}
	public void afterMove() {
		System.out.println("***");
		ContainerID destination1 = new ContainerID("Pruebas1",null);
		doSecureMove(destination1);
		
	}	
}
```

*ON A INTRA-PLATFORM MOVE, COMMUNICATION IS POSSIBLE THROUGH HORIZONTAL AND VERTICAL COMMANDS,
HOWEVER, IN INTERCOMMUNICATION, YOU MUST SEND ACL MESSAGES* 


## EXAMPLE ONION PROTOCOL:

* java -cp /Users/ivan/Desktop/onion.jar jade.Boot -gui -host localhost -port 4333 -services jade.core.SecureOnionTPM.SecureOnionTPMService

* java -cp /Users/ivan/Desktop/onion.jar jade.Boot -container -container-name PRUEBAS -host localhost -port 4333 -services jade.core.SecureOnionTPM.SecureOnionTPMService -agents DARK:vom.RASTREATOR


### EXECUTE THE FOLLOWING CODE:

```
package vom;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.PlatformID;
import jade.core.ServiceException;
import jade.core.SecureTPM.KeyStorage;
import jade.core.SecureTPM.SecureAgent;


public class RASTREATOR extends SecureAgent implements Serializable{
	private static final long serialVersionUID = -2241822868465327314L;
	public void setup() {
		
		AID remoteAMS1 = new AID("ams@192.168.0.100:4333/JADE", AID.ISGUID);
		remoteAMS1.addAddresses("http://192.168.0.100:7778/acc");
		PlatformID destination1 = new PlatformID(remoteAMS1);
		
		AID remoteAMS2 = new AID("ams@192.168.0.100:5333/JADE", AID.ISGUID);
		remoteAMS2.addAddresses("http://192.168.0.100:7778/acc");
		PlatformID destination2 = new PlatformID(remoteAMS2);
		
		AID remoteAMS3 = new AID("ams@192.168.0.100:6333/JADE", AID.ISGUID);
		remoteAMS3.addAddresses("http://192.168.0.100:7778/acc");
		PlatformID destination3 = new PlatformID(remoteAMS3);

	
		List<KeyStorage> list_dir = new ArrayList<KeyStorage>();
		
		
		KeyStorage st1 = new KeyStorage(null,destination1);
		KeyStorage st2 = new KeyStorage(null, destination2);
		
		list_dir.add(st1);
		list_dir.add(st2);
		
		try {
			doSecureOnionTransfer(destination3,list_dir);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
```

# WARNING
============
> **Note:** **THE TPM MODULE IS NOT IMPLEMENTED ACTUALLY ON THIS CODE TO SPEED UP THE TEST
IN THE FOLLOWINGS VERSIONS WILL BE ADDED**.

