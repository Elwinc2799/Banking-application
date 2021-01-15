package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutUsController {

    double x = 0;
    double y = 0;

    //allow user to drag and move the application
    @FXML
    void dragged(MouseEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    private StackPane root;

    @FXML
    private Button teikSean;

    @FXML
    private Button elwin;

    @FXML
    private Button eric;

    //load to account scene when account button pressed
    @FXML
    public void accountButtonPushed() { loadNextScene("/sample/Scene/accountScene.fxml"); }

    //load to transaction history scene when transaction history button button pressed
    @FXML
    public void transactionHistoryButtonPushed() {
        loadNextScene("/sample/Scene/transactionHistoryScene.fxml");
    }

    //load to transfer scene when transfer button pressed
    @FXML
    public void transferButtonPushed() { loadNextScene("/sample/Scene/transferScene.fxml"); }

    //load to loan scene if the users' account has loan taken or load to no loan scene if the user does not have a loan when loan button pressed
    @FXML
    public void loanButtonPushed() {
        loadNextScene((ReadFile.DataStorage.loan) ? "/sample/Scene/loanScene.fxml" : "/sample/Scene/noLoanScene.fxml");
    }

    //load to currency exchange scene when dashboard button pressed
    @FXML
    public void dashBoardButtonPushed() { loadNextScene("/sample/Scene/currencyExchangeScene.fxml"); }

    //when the Facebook button beside eric's photo pushed
    @FXML
    public void ericPushed() {
        eric.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.facebook.com/eric.cheah575/").toURI());
            } catch (IOException | URISyntaxException e) { e.printStackTrace(); }
        });
    }

    //when the Facebook button beside teiksean's photo pushed
    @FXML
    public void teikSeanPushed() {
        teikSean.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.facebook.com/teiksean.tan").toURI());
            } catch (IOException | URISyntaxException e) { e.printStackTrace(); }
        });
    }

    //when the Facebook button beside elwin's photo pushed
    @FXML
    public void elwinPushed() {
        elwin.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.facebook.com/elwin.chiong").toURI());
            } catch (IOException | URISyntaxException e) { e.printStackTrace(); }
        });
    }

    //the function to allow the application to change from one scene to another scene
    private void loadNextScene(String fxml) {
        try {
            Parent secondView;
            secondView = FXMLLoader.load(getClass().getResource(fxml));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(AboutUsController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
