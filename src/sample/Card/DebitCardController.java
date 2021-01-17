package sample.Card;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import sample.ReadFile;
import sample.TransactionHistory;
import sample.TransactionHistoryController;
import sample.TransferController;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebitCardController implements Initializable {

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
    private Label cardNumberLabel;

    @FXML
    private Label expiryDateLabel;

    @FXML
    private Label cvvLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private LineChart<?, ?> balanceChart;

    @FXML
    private Button changeLimitButton;

    TextField textField = new TextField();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        XYChart.Series series = new XYChart.Series();
        String[] month = new String[7];
        LocalDate now = LocalDate.now();

        //get and store the last 7 month into month array
        for (int i = 0, j = 8; i < 7; i++, j--) {
            month[i] = new DateFormatSymbols().getMonths()[now.minusMonths(j).getMonthValue()];

            if (month[i].equals("February") && month[i - 2].equals("December"))
                month[i - 1] = "January";
        }

        //set the text to respective label
        cardNumberLabel.setText(ReadFile.DataStorage.debitCard.getCardID());
        expiryDateLabel.setText(ReadFile.DataStorage.debitCard.getExpiryDate());
        cvvLabel.setText(ReadFile.DataStorage.debitCard.getCvv());
        balanceLabel.setText(String.format("%.2f", ReadFile.DataStorage.savingsAccount.getBalance()));

        LocalDate start = LocalDate.now().minusMonths(1);
        LocalDate end = LocalDate.now();

        ArrayList<TransactionHistory> generalList = TransactionHistoryController.searchIn(ReadFile.DataStorage.transactionHistoryArrayList,
                transactionHistory -> (transactionHistory.getTransactionDate().equals(start) || transactionHistory.getTransactionDate().equals(end) ||
                        (transactionHistory.getTransactionDate().isAfter(start)) && transactionHistory.getTransactionDate().isBefore(end)));

        if (generalList.size() > 0) {
            statusLabel.setText("Active");
            statusLabel.setTextFill(Color.web("#21DADA"));
        } else {
            statusLabel.setText("Declined");
            statusLabel.setTextFill(Color.web("#CD5C5C"));
        }

        //create a XYChart to show the balance to the respective month
        for (int i = 0, j = 6; i < 7; i++, j--)
            series.getData().add(new XYChart.Data(month[i], ReadFile.DataStorage.savingsAccount.getBalanceRecorder(j)));

        balanceChart.getData().addAll(series);
    }

    //change transaction limit for debit card
    @FXML
    public void changeLimitPushed() {
        TransferController instance = new TransferController();
        new Thread(instance.sendOTPTask).start();

        Label newLimit = new Label("New Limit");
        Label otp = new Label("OTP(sent to email)");
        newLimit.setTranslateX(10);
        newLimit.setTranslateY(10);
        newLimit.setFont(new Font("Arial", 14));
        otp.setTranslateX(10);
        otp.setTranslateY(40);
        otp.setFont(new Font("Arial", 14));

        TextField otpField = new TextField();
        textField.setPromptText("New Limit");
        textField.setTranslateX(40);
        textField.setTranslateY(10);
        textField.setMaxWidth(100);
        textField.setFont(new Font("Arial", 12));
        textField.setFocusTraversable(false);
        otpField.setPromptText("OTP");
        otpField.setTranslateX(10);
        otpField.setTranslateY(50);
        otpField.setMaxWidth(100);
        otpField.setFont(new Font("Arial", 12));
        otpField.setFocusTraversable(false);

        Label RM = new Label("RM");
        RM.setTranslateX(10);
        RM.setTranslateY(30);
        RM.setFont(new Font("Arial", 14));

        Button nextButton = new Button("Next");
        nextButton.setTranslateX(110);
        nextButton.setTranslateY(75);

        VBox vBox = new VBox(newLimit, RM, textField, otp, otpField, nextButton);
        vBox.setPrefWidth(200);
        vBox.setPrefHeight(200);

        PopOver popOver = new PopOver(vBox);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setTitle("Limit change");
        popOver.show(changeLimitButton);

        nextButton.setOnAction(actionEvent -> {
            if (otpField.getText().equals(instance.getOTP()) && ReadFile.DataStorage.debitCard.isValid(textField.getText())) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Statement statement = ReadFile.connect.createStatement();

                    statement.executeUpdate("UPDATE ACCOUNT SET ACCOUNT_DAILY_LIMIT = " + Double.parseDouble(textField.getText()) +
                            " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You have successfully change your limit.");
                alert.showAndWait();
            }

            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Invalid OTP or limit is not a number");
                alert.showAndWait();
            }
        });
    }

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

    //load to about us scene when about us button pressed
    @FXML
    public void aboutUsButtonPushed() {
        loadNextScene("/sample/Scene/aboutUsScene.fxml");
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
            Logger.getLogger(DebitCardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
