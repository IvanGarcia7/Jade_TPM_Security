
# WELCOME TO JADE TPM SECURITY LIBRARY!

In this repo, i will try to create a **secure library** using **Jade Framework**.
To date, both secure migration and agent cloning have been implemented, either intra-platform or cross-platform.
Also, I'm developing a kind of Onion-based protocol that can take advantage of agents, 
making use of some of Infineon's functionalities.

My implementation Services are in the following dirs:

* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureCloud (NEW)
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureAgent (NEW)
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/SecureTPM (NEW)
* https://github.com/IvanGarcia7/Jade_TPM_Security/tree/master/src/jade/core/GUI (NEW)

Here are some **basic examples** to demonstrate how the library works.

## RASPBERRY:

# START SECURE PLATFORM:

java -cp TPM.jar:test.jar:mongo.jar jade.Boot -gui -host localhost -port 8080 -services jade.core.SecureCloud.SecureCloudTPMService -agents CA:vom.CAPlatform


# START AGENT 2

* Context EK: 0x81010020
* Context AK: 0x8101002a


java -cp commons.jar:TPM.jar:test.jar jade.Boot -container-name P2 -gui -host localhost -port 1364 -mtp jade.mtp.http.MessageTransportProtocol\\(http://raspberrypi:36711\) -services jade.core.mobility.AgentMobilityService\\;jade.core.migration.InterPlatformMobilityService\\;jade.core.SecureAgent.SecureAgentTPMService -agents A2:vom.CAAgent


# START AGENT 1

* Context EK: 0x81010030
* Context AK: 0x8101003a
* AID Destiny Test: ams@192.168.0.110:1364/JADE
* Address Destiny Test: http://raspberrypi:36711/acc


java -cp commons.jar:TPM.jar:test.jar jade.Boot -container-name P1 -gui -host localhost -port 1564 -mtp jade.mtp.http.MessageTransportProtocol\\(http://raspberrypi:36811\) -services jade.core.mobility.AgentMobilityService\\;jade.core.migration.InterPlatformMobilityService\\;jade.core.SecureAgent.SecureAgentTPMService -agents A1:vom.CAAgent



