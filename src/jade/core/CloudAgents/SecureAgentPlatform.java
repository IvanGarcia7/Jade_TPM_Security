package jade.core.CloudAgents;

import jade.core.Agent;
import jade.core.Location;
import jade.core.SecureTPM.Agencia;
import jade.core.ServiceException;

import java.security.PublicKey;
import java.util.logging.Level;

public class SecureAgentPlatform extends Agent{
    private static final long serialVersionUID = 9058618378207435612L;

    /**
     * SERVICE CREATION, INTRA E INTER PLATFORM. SERVICES ARE INSTALLED WHEN THE PROGRAM REQUIRES IT
     */
    private transient SecureAgentTPMHelper mobHelperCloudPlatform;

    /**
     * THIS SERVICE, ACCEPT REQUEST FROM ANOTHER PLATFORMS, AND PUT THEM INTO A LIST
     * THAT IN THE FUTURE, ONE PERSONAL DESIGNED TO ID WILL PROCEED TO ACCEPT
     * SOME OF THESE PLATFORMS.
     */
    public void doInitializeAgent(Location CALocation, PublicKey pubKey,String contextEK, String contextAK){
        System.out.println("INITIALIZING THE SECURE CLOUD PLATFORM AGENT");
        System.out.println("hihihihih "+CALocation);
        StringBuilder sb = new StringBuilder();
        System.out.println(sb.toString());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                    Level.INFO, SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
            mobHelperCloudPlatform.doStartCloudAgent(this, CALocation, pubKey, contextEK,  contextAK);
        }
        catch(ServiceException se) {
            System.out.println("THE SECURE PLATFORM HAS NOT BEEN ABLE TO START");
            se.printStackTrace();
            return;
        }
    }

    public void doSecureMigration(Location destiny){
        System.out.println("EXECUTING THE MIGRATION PROCESS");
        StringBuilder sb = new StringBuilder();
        System.out.println(sb.toString());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                    Level.INFO, SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
            mobHelperCloudPlatform.doStartMigration(this, destiny);
        }
        catch(ServiceException se) {
            System.out.println("THE SECURE PLATFORM HAS NOT BEEN ABLE TO START");
            se.printStackTrace();
            return;
        }
    }



    /**
     * CALL THIS FUNCTION TO INICALIZE THE CLOUD SERVICE
     * @throws ServiceException
     */
    private void initmobHelperCloud() throws ServiceException {
        if(mobHelperCloudPlatform == null){
            mobHelperCloudPlatform = (SecureAgentTPMHelper) getHelper(SecureAgentTPMHelper.NAME);
            System.out.println(mobHelperCloudPlatform);
        }
    }

}



