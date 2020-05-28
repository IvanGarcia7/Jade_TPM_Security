package jade.core.SecureCloud;



import jade.core.*;
import jade.core.SecureTPM.Agencia;

import javax.swing.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;


public class SecureCAPlatform extends Agent{

    private static final long serialVersionUID = 9058618378207435615L;
    private transient SecureCloudTPMHelper mobHelperCloud;



    private JTextArea PrinterList;
    private JTextArea PrinterCRUD;
    private JTextArea PrinterStart;
    private JTextArea PrinterInformation;



    public PrivateKey getPrivateKey(){
        return mobHelperCloud.getPrivKey();
    }


    public void setPrinterStart(JTextArea PrinterA){
        PrinterList = PrinterA;
    }

    public void setCRUDPrinterList(JTextArea PrinterB){
        PrinterCRUD = PrinterB;
    }

    public void setStartPrinterCRUD(JTextArea PrinterB){
        PrinterStart = PrinterB;
    }

    public void setPrinterInformation(JTextArea pListInformation) {
        PrinterInformation = pListInformation;
    }



    /**
     * THIS FUNCTION, ACCEPTS REQUESTS FROM OTHER PLATFORMS, AND PUT THEM INTO A LIST
     * THAT IN THE FUTURE, ONE PERSONAL DESIGNED TO IT, WILL PROCEED TO ACCEPT SOME
     * OF THESE PLATFORM.
     * @param priv
     * @param pub
     */

    public void doInitializeCA(PrivateKey priv, PublicKey pub, String username, String password,
                               JTextArea STARTAREA, JTextArea PRINTERAREA, JTextArea CRUDAREA,
                               JTextArea INFORMATIONAREA){
        Agencia.printLog("THE INITIALIZATION OF THE CA HAS BEGUN TO RUN",
                        Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE INITIALIZATION OF THE CA HAS BEGUN TO RUN",
                            Level.INFO,SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            mobHelperCloud.doStartCloud(this,priv,pub,username,password,STARTAREA,
                    PRINTERAREA, CRUDAREA, INFORMATIONAREA);
        } catch(ServiceException se) {
            System.out.println("THERE ARE AN ERROR IN THE doInitializeCA");
            se.printStackTrace();
        }
    }


    /**
     * THIS FUNCTION PRINTS THE LIST OF HOTSPOTS TO BE CONFIRMED.
     */
    public void doListCA(){
        Agencia.printLog("REQUEST THE SECURE CLOUD PLATFORM DEVICES REGISTER",
                        Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE REQUEST LIST SERVICE HAS BEGUN TO RUN", Level.INFO,
                            SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            mobHelperCloud.listPlatforms(this);
        } catch(ServiceException se) {
            System.out.println("THE SECURE PLATFORM HAS NOT BEEN ABLE TO START");
            se.printStackTrace();
        }
    }


    /**
     * THIS FUNCTION PRINTS THE LIST OF CONFIRMED HOTSPOTS.
     */
    public void doListACA(){
        Agencia.printLog("REQUEST THE SECURE CLOUD PLATFORM DEVICES REGISTER",
                Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE REQUEST LIST SERVICE HAS BEGUN TO RUN", Level.INFO,
                    SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            mobHelperCloud.listAcceptedPlatforms(this);
        } catch(ServiceException se) {
            System.out.println("THE SECURE PLATFORM HAS NOT BEEN ABLE TO START");
            se.printStackTrace();
        }
    }


    /**
     * THIS FUNCTION TAKES AN ADDRESS AND INSERT IN WITHIN RELIABLE PLATFORMS IF IT HAS BEEN
     * RECORDED PREVIOUSLY IN THE LIST OF PENDING.
     * @param index
     */
    public void doAcceptListCA(String index){
        System.out.println("THE STRING AID IS THE FOLLOWING "+index);
        Agencia.printLog("REQUEST THE SECURE CLOUD PLATFORM DEVICES REGISTER",
                        Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE ACCEPT LIST SERVICE HAS BEGUN TO RUN", Level.INFO,
                            SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            mobHelperCloud.doAcceptCloud(this,index);
        } catch(ServiceException se) {
            System.out.println("THE SECURE PLATFORM HAS NOT BEEN ABLE TO START");
            se.printStackTrace();
        }
    }



    /**
     * THIS FUNCTION TAKES AN ADDRESS AND DELETE IN WITHIN RELIABLE PLATFORMS IF IT HAS BEEN
     * RECORDED PREVIOUSLY IN THE LIST OF PENDING.
     * @param index
     */
    public void doDeleteListCA(String index){
        System.out.println("THE STRING AID IS THE FOLLOWING "+index);
        Agencia.printLog("REQUEST THE SECURE CLOUD PLATFORM DEVICES REGISTER",
                Level.INFO, SecureCloudTPMHelper.DEBUG,this.getClass().getName());
        try {
            initmobHelperCloud();
            Agencia.printLog("THE ACCEPT LIST SERVICE HAS BEGUN TO RUN", Level.INFO,
                    SecureCloudTPMHelper.DEBUG,this.getClass().getName());
            mobHelperCloud.doDeleteCloud(this,index);
        } catch(ServiceException se) {
            System.out.println("THE SECURE PLATFORM HAS NOT BEEN ABLE TO START");
            se.printStackTrace();
        }
    }

    /**
     * CALL THIS FUNCTION TO INITIALIZE THE CLOUD SERVICE
     * @throws ServiceException
     */
    private void initmobHelperCloud() throws ServiceException {
        if(mobHelperCloud == null){
            mobHelperCloud = (SecureCloudTPMHelper) getHelper(SecureCloudTPMHelper.NAME);
        }
    }


}



