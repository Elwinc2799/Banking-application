package sample;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
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

    @FXML
    private Circle circle;

    TextField emailField = new TextField();
    PasswordField newPasswordField = new PasswordField();
    double x = 0;
    double y = 0;

    Color[] colors = {
            new Color(0.2, 0.5, 0.8, 1.0).saturate().brighter().brighter(),
            new Color(0.3, 0.2, 0.7, 1.0).saturate().brighter().brighter(),
            new Color(0.8, 0.3, 0.9, 1.0).saturate().brighter().brighter(),
            new Color(0.4, 0.3, 0.9, 1.0).saturate().brighter().brighter(),
            new Color(0.2, 0.5, 0.7, 1.0).saturate().brighter().brighter()
    };

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
        PauseTransition pause = new PauseTransition(Duration.seconds(0.2));
        pause.setOnFinished(event -> makeFadeOut());
        pause.play();
    }

    @FXML
    public void forgetPasswordPushed() {
        root.setEffect(new BoxBlur(10, 10, 3));

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
        popOvers.setHeaderAlwaysVisible(true);
        popOvers.setTitle("Please enter your email");
        popOvers.show(forgotPasswordButton);

        ((Parent) popOvers.getSkin().getNode()).getStylesheets().add(getClass().getResource("sample.css").toExternalForm());


        generateOTP.setOnAction(actionEvent -> {
            if (!checkEmail()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You do not have account in this bank.");
                alert.showAndWait();
                alert.setOnShown(dialogEvent -> root.setEffect(new BoxBlur(10, 10, 3)));
                alert.setOnHidden(dialogEvent -> root.setEffect(null));
                return;
            }

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
            popOvers.setHeaderAlwaysVisible(true);
            popOvers.setTitle("Please enter the OTP and your new password");
            popOver.show(forgotPasswordButton);

            nextButton.setOnAction(actionEvent1 -> {
                if (instance.getOTP().equals(otpField.getText()) && newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    try {
                        Class.forName("oracle.jdbc.OracleDriver");
                        Statement statement = ReadFile.connect.createStatement();

                        statement.executeQuery("UPDATE LOGIN SET PASSWORD = '" + newPasswordField.getText() +
                                "' WHERE EMAIL = '" + emailField.getText() + "'");
                    } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("You have successfully change your password. Please try and login with the new password now.");
                    alert.showAndWait();
                }
                else {
                    root.setEffect(new BoxBlur(10, 10, 3));
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Invalid OTP or password does not match.");
                    alert.showAndWait();
                    alert.setOnHidden(dialogEvent -> root.setEffect(null));
                }
            });

        });

        popOvers.setOnHidden(windowEvent -> root.setEffect(null));
    }

    public boolean checkEmail() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Statement statement = ReadFile.connect.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM LOGIN WHERE EXISTS EMAIL = '" + emailField.getText() + "'");
            String test = resultSet.getString(1);
            return test.length() == 0;
        } catch (SQLException | ClassNotFoundException ignored) { }

        return false;
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
            secondView = FXMLLoader.load(getClass().getResource("/sample/Scene/accountScene.fxml"));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setRotate(Circle circle, boolean reverse, int angle, int duration) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(duration), circle);
        rotateTransition.setAutoReverse(reverse);
        rotateTransition.setByAngle(angle);
        rotateTransition.setDelay(Duration.seconds(0));
        rotateTransition.setRate(3);
        rotateTransition.setCycleCount(40);
        rotateTransition.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Pane container = new Pane();
        root.getChildren().add(container);
        container.setStyle("-fx-background-color: rgb(25, 40, 80)");

        for (int i = 0; i < 400; i++)
            spawnNode(container);

        setRotate(circle, true, 360, 10);
        new Thread(ReadFile.passwordValidationTask).start();
    }

    private void spawnNode(Pane container) {
        Circle node = new Circle(0);
        node.setManaged(false);
        node.setFill(colors[(int) (Math.random() * colors.length)]);
        node.setCenterX(Math.random() * 475);
        node.setCenterY(Math.random() * 700);
        container.getChildren().add(node);

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(node.radiusProperty(), 0),
                        new KeyValue(node.centerXProperty(), node.getCenterX()),
                        new KeyValue(node.centerYProperty(), node.getCenterY()),
                        new KeyValue(node.opacityProperty(), 0)
                ),
                new KeyFrame(
                        Duration.seconds(5 + Math.random() * 5),
                        new KeyValue(node.opacityProperty(), Math.random()),
                        new KeyValue(node.radiusProperty(), Math.random() * 5)
                ),
                new KeyFrame(
                        Duration.seconds(10 + Math.random() * 20),
                        new KeyValue(node.radiusProperty(), 0),
                        new KeyValue(node.centerXProperty(), Math.random() * 475),
                        new KeyValue(node.centerYProperty(), Math.random() * 700),
                        new KeyValue(node.opacityProperty(), 0)
                )
        );

        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> {
            container.getChildren().remove(node);
            spawnNode(container);
        });

        timeline.play();
    }
}
