package jade.core.Interfaces.InterfazAgentSegura.src.sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import jade.core.AID;
import jade.core.Interfaces.InterfazAgentSegura.src.sample.SecureAgentController;
import jade.core.Interfaces.InterfazAgentSegura.src.vom.CAAgent;
import jade.core.Location;
import jade.core.PlatformID;
import jade.core.SecureCloud.SecureCAPlatform;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;



import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;


public class SecureAgentGuiImpl  extends SecureAgentController implements Initializable {

    @FXML private ImageView userArrow;
    @FXML private ImageView printerArrow;
    @FXML private ImageView crudArrow;


    @FXML private AnchorPane userPanel;
    @FXML private AnchorPane printerPanel;


    @FXML private JFXTextField userAID;
    @FXML private JFXTextField userAdd;
    @FXML private JFXTextField AIDTextHop;
    @FXML private JFXTextField AIDTextHopADD;
    @FXML private JFXTextField EKText;
    @FXML private JFXTextField AIKText;



    @FXML private JFXButton startButton;
    @FXML private JFXButton AddButton;
    @FXML private JFXButton migrateButton;




    @FXML private JFXTextArea AreaStart;
    @FXML private JFXTextArea selectedList;
    @FXML private JFXTextArea AreaList;

    private List<PlatformID> hops = new ArrayList<PlatformID>();


    //private static CAPlatform myAgentA;


    public void onstartButton(ActionEvent event){
        if(userAID.getText().isEmpty() || userAdd.getText().isEmpty()){
            AreaStart.appendText("PLEASE INSERT AID AND ADDRESS OF THE SECURE CA");
        }else if(EKText.getText().isEmpty() || AIKText.getText().isEmpty()) {
            AreaStart.appendText("PLEASE INSERT AIK AND EK CONTEXT");
        }else{
            try {
                String publicKeyGen = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxWn5INyIwdQlCFnw+45a91O3LrmRtWkj9mEXcXUViblgTrNEYpuY2HuU7wDn6tIs3WrZcxfNbw8vYKnYGmsCvyua2eqhYQ7AW31Itj+fsOy/XrX5a02aNrqwVOs+Evcx9d9Ap5gWU1XJ2Vl47wsCShxFFhadR2ILZNj5XhTeqMwEalsXcQ+D8DIIyy5eKrgZ1KP79s8Kf2UrVFMADsjt39hM4ajB2F9Pge5m5/tQmt3sBKnGFMf+kaIiHd6INZYJB+5+UdcFzBYzF2PMJpU54kywpIyjZ+xo6RLzMnmlP4lEJnPwKai94mUUV4K9V/fe17DPpEey1SKk7I2DMokNSwIDAQAB";
                byte[] publicBytes = Base64.getDecoder().decode(publicKeyGen);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                KeyFactory keyFactory;
                PublicKey pubKey = null;
                try {
                    keyFactory = KeyFactory.getInstance("RSA");
                    pubKey = keyFactory.generatePublic(keySpec);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                AID remoteAMS = new AID(userAID.getText(), AID.ISGUID);
                remoteAMS.addAddresses(userAdd.getText());
                PlatformID destination = new PlatformID(remoteAMS);
                myAgent.doInitializeAgent(destination,pubKey,EKText.getText(),AIKText.getText(),AreaStart,selectedList,AreaList);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public void onAddButton(ActionEvent event){

        if(AIDTextHop.getText().isEmpty() || AIDTextHopADD.getText().isEmpty()){
            AreaList.appendText("PLEASE INSERT AN AID AND AN ADDRESS");
        }else{
            AID remoteAMSDestiny = new AID(AIDTextHop.getText(), AID.ISGUID);
            remoteAMSDestiny.addAddresses(AIDTextHopADD.getText());
            PlatformID destination2 = new PlatformID(remoteAMSDestiny);
            hops.add(destination2);
            selectedList.appendText(destination2.getID()+"\n");
            selectedList.appendText("*********************\n");
        }
    }

    public void onMigrateButton(ActionEvent event){
        if(hops.size()==0){
            selectedList.appendText("PLEASE INSERT ONE PLATFORM");
        }else{
            PlatformID destiny = hops.get(0);
            hops.remove(0);
            myAgent.setHops(hops);
            myAgent.doSecureMigration(destiny);
        }
    }










    public void onExitButtonClicked(MouseEvent event){
        Platform.exit();
        System.exit(0);
    }

    public void onUserButtonClicked(MouseEvent event){
        userPanel.setVisible(true);
        userArrow.setVisible(true);

        printerPanel.setVisible(false);
        printerArrow.setVisible(false);
        crudArrow.setVisible(false);
    }

    public void onPrinterButtonClicked(MouseEvent event){
        printerPanel.setVisible(true);
        printerArrow.setVisible(true);

        userPanel.setVisible(false);
        userArrow.setVisible(false);
        crudArrow.setVisible(false);
    }


    public void onCLEARButton(MouseEvent event){
        AreaStart.selectAll();
        AreaStart.replaceSelection("");
        AreaStart.setText("");
        selectedList.selectAll();
        selectedList.replaceSelection("");
        selectedList.setText("");
        AreaList.selectAll();
        AreaList.replaceSelection("");
        AreaList.setText("");
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // myAgentA = new CAPlatform();
        // System.out.println("hi "+myAgentA);
    }

    public void setMyAgent(CAAgent myAgent) {
        // myAgentA = myAgent;
    }
}

