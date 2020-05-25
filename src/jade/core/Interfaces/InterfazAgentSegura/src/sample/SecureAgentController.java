package jade.core.Interfaces.InterfazAgentSegura.src.sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import jade.core.Interfaces.InterfazAgentSegura.src.vom.CAAgent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class SecureAgentController extends Application {

    private double xOffset;
    private double yOffset;
    public  static CAAgent myAgent;
    public static boolean ready = false;

    public void setAgentNew(CAAgent caAgent) {
        myAgent = caAgent;
        System.out.println("kowo "+myAgent.getAID());
    }

    public SecureAgentController() {

    }

    public SecureAgentController(CAAgent caAgent) {
        myAgent = caAgent;
        System.out.println("kiwi "+myAgent.getAID());
        launch();
    }

    public void show(){
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                primaryStage.setX(mouseEvent.getScreenX() - xOffset);
                primaryStage.setY(mouseEvent.getScreenY() - yOffset);
            }
        });


        Scene scene = new Scene(root);
        SecureAgentGuiImpl controller = loader.getController();
        //System.out.println("aaaa "+myAgent);


        primaryStage.setScene(scene);
        primaryStage.show();

/*
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("CA");
        Scene sceneNew = new Scene(root);
        sceneNew.setFill(Color.TRANSPARENT);

        SecureCAController controller = loader.getController();
        controller.setMyAgent(myAgent);

        primaryStage.setScene(sceneNew);
        primaryStage.show();

 */
        ready = true;


    }


    public static void main(String[] args) {
        launch(args);
    }

}
