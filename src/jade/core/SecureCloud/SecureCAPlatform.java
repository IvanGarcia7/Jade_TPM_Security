package jade.core.SecureCloud;

import jade.core.*;
import jade.core.SecureInterTPM.SecureInterTPMHelper;
import jade.core.SecureTPM.Agencia;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;

public class SecureCAPlatform extends Agent{
    private static final long serialVersionUID = 9058618378207435615L;

    /**
     * SERVICE CREATION, INTRA E INTER PLATFORM. SERVICES ARE INSTALLED WHEN THE PROGRAM REQUIRES IT
     */
    private transient SecureCloudTPMHelper mobHelperCloud;

    /**
     * THIS SERVICE, ACCEPT REQUEST FROM ANOTHER PLATFORMS, AND PUT THEM INTO A LIST
     * THAT IN THE FUTURE, ONE PERSONAL DESIGNED TO ID WILL PROCEED TO ACCEPT
     * SOME OF THESE PLATFORMS.
     */
    public void doInitializeCA(PrivateKey priv, PublicKey pub){
        System.out.println("INITIALIZING THE SECURE CLOUD PLATFORM");
        StringBuilder sb = new StringBuilder();
        System.out.println(sb.toString());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                    Level.INFO,SecureInterTPMHelper.DEBUG,this.getClass().getName());
            System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
            mobHelperCloud.doStartCloud(this,priv,pub);
        }
        catch(ServiceException se) {
            System.out.println("THE SECURE PLATFORM HAS NOT BEEN ABLE TO START");
            se.printStackTrace();
            return;
        }
    }

    public void doListCA(){
        System.out.println("REQUEST THE SECURE CLOUD PLATFORM DEVICES REGISTER");
        StringBuilder sb = new StringBuilder();
        System.out.println(sb.toString());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE SERVICE HAS BEGUN TO RUN",
                    Level.INFO,SecureInterTPMHelper.DEBUG,this.getClass().getName());
            System.out.println("THE SERVICE HAS STARTED WITHOUT ERRORS, PROCEEDING TO ITS IMPLEMENTATION");
            mobHelperCloud.listPlatforms(this);
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
        if(mobHelperCloud == null){
            mobHelperCloud = (SecureCloudTPMHelper) getHelper(SecureCloudTPMHelper.NAME);
            System.out.println(mobHelperCloud);
        }
    }

}



