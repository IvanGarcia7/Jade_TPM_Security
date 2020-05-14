package jade.core.SecureAgent;


import jade.core.Agent;
import jade.core.Location;
import jade.core.PlatformID;
import jade.core.SecureTPM.Agencia;
import jade.core.ServiceException;
import jade.core.migration.InterPlatformMobilityHelper;
import jade.core.mobility.AgentMobilityHelper;
import jade.core.mobility.AgentMobilityService;

import javax.swing.*;
import java.security.PublicKey;
import java.util.logging.Level;


public class SecureAgentPlatform extends Agent{

    private static final long serialVersionUID = 9058618378207435612L;
    private transient SecureAgentTPMHelper mobHelperCloudPlatform;
    private JTextArea Printer;


    /**
     * THIS FUNCTION, SENDS A REQUEST TO THE SECURE PLATFORM TO INCLUDE IT AS A RELIABLE HOST.
     * @param CALocation THE PLATFORMID INFORMATION OF THE SECURE CA
     * @param pubKey THE PUBLIC KEY OF THE SECURE CA
     * @param contextEK THE INDEX WHERE THE EK IS LOADED INTO THE TPM
     * @param contextAK THE INDEX WHERE THE PRIVATE AIK IS LOADED INTO THE TPM
     */
    public void doInitializeAgent(PlatformID CALocation, PublicKey pubKey, String contextEK, String contextAK){
        try {
            printArea("THE INITIALIZATION OF THE AGENT HAS BEGUN TO RUN");
            initmobHelperCloud();
            Agencia.printLog("THE INITIALIZATION OF THE AGENT HAS BEGUN TO RUN",
                    Level.INFO, SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            mobHelperCloudPlatform.doStartCloudAgent(this, CALocation, pubKey, contextEK,  contextAK);
        } catch(ServiceException se) {
            System.out.println("THERE ARE AN ERROR IN THE doInitializeAgent");
            se.printStackTrace();
        }
    }


    /**
     * THIS FUNCTION START A REQUEST MIGRATION, WHEN THE PLATFORM RECEIVES THE CONFIRMATION FROM THE SECURE CA PLATFORM
     * @param destiny THE PLATFORM ID WHERE I NEED TO MIGRATE
     */
    public void doSecureMigration(PlatformID destiny){
        printArea("EXECUTING THE MIGRATION PROCESS");
        System.out.println("EXECUTING THE MIGRATION PROCESS");
        try {
            initmobHelperCloud();
            Agencia.printLog("THE MIGRATION SERVICE HAS BEGUN TO RUN", Level.INFO,
                              SecureAgentTPMHelper.DEBUG,this.getClass().getName());
            mobHelperCloudPlatform.doStartMigration(this, destiny);
        } catch(ServiceException se) {
            System.out.println("THERE ARE AN ERROR IN THE doSecureMigration");
            se.printStackTrace();
        }
    }


    public void doSecureMigration2(PlatformID destiny){
        super.doMove(destiny);
    }

    public void setGUI(JTextArea printer){
        Printer = printer;
    }

    public JTextArea getGUI(){
        return Printer;
    }

    public void printArea(String text){
        Printer.append(text+"\n");
    }


    /**
     * CALL THIS FUNCTION TO INITIALIZE THE CLOUD SERVICE
     * @throws ServiceException
     */
    private void initmobHelperCloud() throws ServiceException {
        if(mobHelperCloudPlatform == null){
            mobHelperCloudPlatform = (SecureAgentTPMHelper) getHelper(SecureAgentTPMHelper.NAME);
        }
    }
}



