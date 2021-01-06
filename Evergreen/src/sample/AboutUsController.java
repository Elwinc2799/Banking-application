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

    @FXML
    public void accountButtonPushed() {
        loadNextScene("/sample/Scene/accountScene.fxml");
    }

    @FXML
    public void transactionHistoryButtonPushed() { loadNextScene("/sample/Scene/transactionHistoryScene.fxml"); }

    @FXML
    public void transferButtonPushed() { loadNextScene("/sample/Scene/transferScene.fxml"); }

    @FXML
    public void loanButtonPushed() {
        loadNextScene((ReadFile.DataStorage.loan) ? "/sample/Scene/loanScene.fxml" : "/sample/Scene/noLoanScene.fxml");
    }

    @FXML
    public void dashBoardButtonPushed() { loadNextScene("/sample/Scene/currencyExchangeScene.fxml"); }

    @FXML
    public void ericPushed() {
        eric.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.facebook.com/eric.cheah575/").toURI());
            } catch (IOException | URISyntaxException e) { e.printStackTrace(); }
        });
    }

    @FXML
    public void teikSeanPushed() {
        teikSean.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.facebook.com/teiksean.tan").toURI());
            } catch (IOException | URISyntaxException e) { e.printStackTrace(); }
        });
    }

    @FXML
    public void elwinPushed() {
        elwin.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.facebook.com/elwin.chiong").toURI());
            } catch (IOException | URISyntaxException e) { e.printStackTrace(); }
        });
    }

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
