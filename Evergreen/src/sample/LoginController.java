package sample;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField userName;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button forgotPasswordButton;

    TextField emailField = new TextField();
    PasswordField newPasswordField = new PasswordField();

    @FXML
    public void login() {
        /*boolean validated = false;

        for (Map.Entry<String, String> row : ReadFile.passwordMap.entrySet()) {
            String key = row.getKey();

            if (key.equals(userName.getText().toLowerCase(Locale.ROOT))) && row.getValue().equals(passwordField.getText())) {
                ReadFile.importData();
                makeFadeOut();
                ReadFile.passwordMap.clear();
                validated = true;
            }
        }

        if (!validated)
            validation(); */

        ReadFile.DataStorage.setUsername(userName.getText().toLowerCase(Locale.ROOT));
        ReadFile.importData();
        makeFadeOut();
    }

    @FXML
    public void forgetPasswordPushed() {
        Label emailLabel = new Label("Email");
        emailLabel.setTranslateX(10);
        emailLabel.setTranslateY(10);
        emailLabel.setFont(new Font("Arial", 14));

        emailField.setPromptText("Email");
        emailField.setTranslateX(10);
        emailField.setTranslateY(20);
        emailField.setMaxWidth(200);
        emailField.setFocusTraversable(false);
        emailField.setFont(new Font("Arial", 12));

        Button generateOTP = new Button();
        generateOTP.setText("Generate OTP");
        generateOTP.setTranslateX(125);
        generateOTP.setTranslateY(70);

        final VBox[] vBox = {new VBox(emailLabel, emailField, generateOTP)};
        vBox[0].setPrefHeight(150);
        vBox[0].setPrefWidth(250);

        PopOver popOvers = new PopOver(vBox[0]);
        popOvers.show(forgotPasswordButton);

        generateOTP.setOnAction(actionEvent -> {
            vBox[0].getChildren().clear();
            TransferController instance = new TransferController();
            ReadFile.DataStorage.savingsAccount.setEmail(emailField.getText());
            new Thread(instance.sendOTPTask).start();

            Label OTP = new Label("OTP");
            Label newPassword = new Label("New password");
            Label confirmPassword = new Label("Confirm password");
            OTP.setTranslateX(10);
            OTP.setTranslateY(10);
            OTP.setFont(new Font("Arial", 14));
            newPassword.setTranslateX(10);
            newPassword.setTranslateY(50);
            newPassword.setFont(new Font("Arial", 14));
            confirmPassword.setTranslateX(10);
            confirmPassword.setTranslateY(70);
            confirmPassword.setFont(new Font("Arial", 14));

            PasswordField otpField = new PasswordField();
            PasswordField confirmPasswordField = new PasswordField();
            otpField.setPromptText("OTP");
            otpField.setTranslateX(10);
            otpField.setTranslateY(0);
            otpField.setMaxWidth(180);
            otpField.setFocusTraversable(false);
            newPasswordField.setTranslateX(10);
            newPasswordField.setTranslateY(20);
            newPasswordField.setMaxWidth(180);
            newPasswordField.setFocusTraversable(false);
            newPasswordField.setPromptText("New password");
            confirmPasswordField.setTranslateX(10);
            confirmPasswordField.setTranslateY(55);
            confirmPasswordField.setMaxWidth(180);
            confirmPasswordField.setFocusTraversable(false);
            confirmPasswordField.setPromptText("Confirm Password");

            Button nextButton = new Button();
            nextButton.setText("Next");
            nextButton.setTranslateX(155);
            nextButton.setTranslateY(100);

            vBox[0] = new VBox(OTP, newPassword, otpField, confirmPassword, newPasswordField, confirmPasswordField, nextButton);
            vBox[0].setPrefHeight(250);
            vBox[0].setPrefWidth(250);

            PopOver popOver = new PopOver(vBox[0]);
            popOver.show(forgotPasswordButton);

            nextButton.setOnAction(actionEvent1 -> {
                if (instance.getOTP().equals(otpField.getText()) && newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    try {
                        Class.forName("oracle.jdbc.OracleDriver");
                        Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", ReadFile.accPassword);
                        Statement statement = connect.createStatement();

                        statement.executeQuery("UPDATE LOGIN SET PASSWORD = '" + newPasswordField.getText() +
                                "' WHERE EMAIL = '" + emailField.getText() + "'");
                    } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("You have successfully change your password. Please try and login with the new password now.");
                    alert.showAndWait();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Invalid OTP or password does not match");
                    alert.showAndWait();
                }
            });

        });
    }

    public void validation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Invalid email or password");
        alert.showAndWait();
    }

    private void makeFadeOut() {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration((Duration.millis(1500)));
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(ReadFile.passwordValidationTask).start();
    }
}
