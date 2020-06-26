
# Implementation of a security library using the trusted platform module on Mobile Agent Systems

In this repository, a new service for the **development framework called JADE** has been developed, making use of certain functionalities provided by a **trusted platform module**. This new service allows agents to migrate safely, guaranteeing the confidentiality and integrity of the target platform.

To implement this new functionality, asymmetric keys have been used, as well as system PCR indexes.

The implementation of the secure migration service is in the following dirs:

* Deploys a system status verification Agent: 
https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureCloud 
* Deploy a conventional Agent: 
https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureAgent 
* Conveniently run TPM methods as well as other key usage related functions:
https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureTPM 
* Graphical interface developed for each of the deployed agents, in this case, the conventional platforms and the CA:
https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/GUI


# USER CASE:

## START SECURE CA AGENT:


In the case of the CA, all the information generated will be stored in a mongo cluster. Therefore, it is required to log in previously. The credentials to gain access are as follows:

``` 
USERNAME: JADE
PASSWORD: JADE
```

``` 
java -cp TPM.jar:test.jar:mongo.jar jade.Boot -gui -host localhost -port 2511 -services jade.core.SecureCloud.SecureCloudTPMService -agents CA:vom.CAPlatform
``` 

![CA GUI](https://github.com/IvanGarcia7/Jade_TPM_Security/blob/master/Images/CAAgent.png?raw=true "CA GUI")


## START CONVENTIONAL AGENTS:

![CONVENTIONAL AGENTS GUI](https://github.com/IvanGarcia7/Jade_TPM_Security/blob/master/Images/ConventionalAgent.png?raw=true "CONVENTIONAL AGENTS GUI")

### START CONVENTIONAL AGENT 1:

* Context EK: 0x81010030
* Context AK: 0x8101003a

``` 
java -cp commons.jar:TPM.jar:test.jar jade.Boot -container-name P1 -gui -host localhost -port 1564 -mtp jade.mtp.http.MessageTransportProtocol\\(http:/raspberrypi:36811\) -services jade.core.mobility.AgentMobilityService\;jade.core.migration.InterPlatformMobilityService\\;jade.core.SecureAgent.SecureAgentTPMService -agents A1:vom.CAAgent\;WORKERA:vom.AgentWorker
``` 

### START CONVENTIONAL AGENT 2:

* Context EK: 0x81010020
* Context AK: 0x8101002a

``` 
java -cp commons.jar:TPM.jar:test.jar jade.Boot -container-name P2 -gui -host localhost -port 1364 -mtp jade.mtp.http.MessageTransportProtocol\(http://raspberrypi:36711\) -services jade.core.mobility.AgentMobilityService\;jade.core.migration.InterPlatformMobilityService\\;jade.core.SecureAgent.SecureAgentTPMService -agents A2:vom.CAAgent\;WORKERB:vom.AgentWorker
``` 

### START CONVENTIONAL AGENT 3:

* Context EK: 0x81010040
* Context AK: 0x8101004a

``` 
java -cp commons.jar:TPM.jar:test.jar jade.Boot -container-name P3 -gui -host localhost -port 1864 -mtp jade.mtp.http.MessageTransportProtocol\(http://raspberrypi:36911\) -services jade.core.mobility.AgentMobilityService\;jade.core.migration.InterPlatformMobilityService\;jade.core.SecureAgent.SecureAgentTPMService -agents A3:vom.CAAgent\;WORKERC:vom.AgentWorker
``` 
# USERCASE EXAMPLE:

## HOPS:
* ams@192.168.0.110:1364/JADE
  http://raspberrypi:36711/acc
  ImageProcessing
* ams@192.168.0.110:1864/JADE
  http://raspberrypi:36911/acc
  ImageProcessing


# Operation Diagram:

![First Part](https://github.com/IvanGarcia7/Jade_TPM_Security/blob/master/Images/FirstPart.jpg?raw=true "First Part")

![Second Part](https://github.com/IvanGarcia7/Jade_TPM_Security/blob/master/Images/SecondPart.jpg?raw=true "Second Part")


# SECURE SYSTEM:

![Secure System](https://github.com/IvanGarcia7/Jade_TPM_Security/blob/master/Images/SecureSystem.jpeg?raw=true "Secure System")

# REQUIREMENTS:

* JRE > 4.0
* Infineon OPTIGATM TPM SLI 9670 TPM2.0


© 2020 Iván García Aguilar 
All Rights Reserved.
