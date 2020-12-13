package sample;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void login() throws FileNotFoundException {
        ReadFile.importData();
        makeFadeOut();
    }

    public void validation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Invalid email or password");
        alert.showAndWait();
    }

    private void makeFadeOut() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration((Duration.millis(1000)));
        fadeTransition.setNode(root);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        fadeTransition.setOnFinished((ActionEvent event) -> loadNextScene());

        fadeTransition.play();
    }

    private void loadNextScene() {
        try {
            Parent secondView;
            secondView = FXMLLoader.load(getClass().getResource("accountScene.fxml"));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
