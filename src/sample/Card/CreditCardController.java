package sample.Card;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import sample.ReadFile;
import sample.TransferController;
import java.math.BigDecimal;
import java.math.RoundingMode;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreditCardController implements Initializable {

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
    private Label monthlyLimitLabel;

    @FXML
    private Label expenditureLabel;

    @FXML
    private LineChart<?, ?> expenseChart;

    @FXML
    private Button creditCardImage;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] month = new String[7];
        LocalDate now = LocalDate.now();

        //get and store the last 7 month into month array
        for (int i = 0, j = 8; i < 7; i++, j--)
            month[i] = new DateFormatSymbols().getMonths()[now.minusMonths(j).getMonthValue()];

        //set the text to the respective label
        cardNumberLabel.setText(ReadFile.DataStorage.creditCard.getCardID());
        expiryDateLabel.setText(ReadFile.DataStorage.creditCard.getExpiryDate());
        cvvLabel.setText(ReadFile.DataStorage.creditCard.getCvv());
        monthlyLimitLabel.setText("/  MYR  " + String.format("%.2f", ReadFile.DataStorage.creditCard.getFixedMonthlyLimit()));
        expenditureLabel.setText("MYR  " + String.format("%.2f", ReadFile.DataStorage.creditCard.getExpenditure()));

        if (ReadFile.DataStorage.creditCard.getExpenditure() >= ReadFile.DataStorage.creditCard.getFixedMonthlyLimit()) {
            statusLabel.setText("Declined");
            statusLabel.setTextFill(Color.web("#CD5C5C"));
        } else {
            statusLabel.setText("Active");
            statusLabel.setTextFill(Color.web("#21DADA"));
        }

        //create a XYChart to show the balance to the respective month
        try {
            XYChart.Series series = new XYChart.Series();

            for (int i = 0, j = 6; i < 7; i++, j--)
                series.getData().add(new XYChart.Data(month[i], ReadFile.DataStorage.creditCard.getBalanceRecorder(j)));

            expenseChart.getData().addAll(series);
        } catch (Exception ignored) { }
    }

    //update the balance to the account in database
    Task<Void> updateBalanceTask = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeUpdate("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.savingsAccount.getBalance() +
                        " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    //action when credit card icon pressed
    @FXML
    public void creditDetailsPushed() {
        Label creditCardOutstandingBalance = new Label("Outstanding Balance");
        Label outstandingBalance = new Label("RM " + Math.round(ReadFile.DataStorage.creditCard.getOutstandingBalance() * 100)/100);
        creditCardOutstandingBalance.setFont(new Font("Arial", 14));
        creditCardOutstandingBalance.setTranslateX(10);
        creditCardOutstandingBalance.setTranslateY(10);
        outstandingBalance.setFont(new Font("Arial", 14));
        outstandingBalance.setTranslateX(10);
        outstandingBalance.setTranslateY(20);
        Button makePayment = new Button();
        makePayment.setText("Make payment");
        makePayment.setTranslateX(60);
        makePayment.setTranslateY(40);

        final VBox[] vBox = {new VBox(creditCardOutstandingBalance, outstandingBalance, makePayment)};
        vBox[0].setPrefWidth(175);
        vBox[0].setPrefHeight(110);
        PopOver popOver = new PopOver(vBox[0]);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setTitle("Credit details:");

        popOver.show(creditCardImage);

        //send OTP when make payment button pressed
        makePayment.setOnAction(actionEvent -> {
            vBox[0].getChildren().clear();
            TransferController instance = new TransferController();
            new Thread(instance.sendOTPTask).start();

            Label OTP = new Label("OTP(sent to email)");
            Label amountLabel = new Label("Amount");
            Label myr = new Label("MYR");
            OTP.setFont(new Font("Arial", 14));
            OTP.setTranslateX(10);
            OTP.setTranslateY(10);
            amountLabel.setFont(new Font("Arial", 14));
            amountLabel.setTranslateX(10);
            amountLabel.setTranslateY(40);
            myr.setFont(new Font("Arial", 14));
            myr.setTranslateX(10);
            myr.setTranslateY(55);

            PasswordField otpField = new PasswordField();
            TextField amountField = new TextField();
            otpField.setPromptText("OTP");
            otpField.setTranslateX(10);
            otpField.setTranslateY(20);
            otpField.setMaxWidth(150);
            otpField.setFocusTraversable(false);
            amountField.setPromptText("Amount");
            amountField.setTranslateX(50);
            amountField.setTranslateY(35);
            amountField.setMaxWidth(150);
            amountField.setFocusTraversable(false);

            Button proceedPayment = new Button();
            proceedPayment.setText("Next");
            proceedPayment.setTranslateX(160);
            proceedPayment.setTranslateY(65);

            vBox[0] = new VBox(OTP, otpField, amountLabel, myr, amountField, proceedPayment);
            vBox[0].setPrefWidth(230);
            vBox[0].setPrefHeight(200);

            PopOver popOvers = new PopOver(vBox[0]);
            popOvers.setHeaderAlwaysVisible(true);
            popOvers.setTitle("Please enter valid info");
            popOvers.show(creditCardImage);

            //proceed the payment if the OTP entered by user match the OTP sent by the application
            proceedPayment.setOnAction(actionEvent1 -> {
                if (otpField.getText().equals(instance.getOTP()) && ReadFile.DataStorage.creditCard.isValid(amountField.getText())) {
                    if (ReadFile.DataStorage.creditCard.creditRepayment(Double.parseDouble(amountField.getText()))) {
                        ReadFile.DataStorage.savingsAccount.savingsAccountPayment(Double.parseDouble(amountField.getText()));
                        new Thread(updateBalanceTask).start();

                        //update the database in credit card
                        ReadFile.DataStorage.creditCard.setOutstandingBalanceStatusUpdated(false);
                        try {
                            Class.forName("com.mysql.jdbc.Driver");
                            Statement statement = ReadFile.connect.createStatement();

                            statement.executeUpdate("UPDATE CREDITCARD SET CARD_OUTSTANDING_BALANCE = " + ReadFile.DataStorage.creditCard.getOutstandingBalance() +
                                    ", CARD_BALANCE_PAID='N' WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                        } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("You have successfully made your payment.");
                        alert.showAndWait();
                    }
                }

                else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Invalid OTP or limit is not a number");
                    alert.showAndWait();
                }
            });
        });
    }

    //action when refresh button pushed
    @FXML
    public void refreshButtonPushed() {
        String[] month = new String[7];
        LocalDate now = LocalDate.now();

        //get and store the last 7 month into month array
        for (int i = 0, j = 8; i < 7; i++, j--)
            month[i] = new DateFormatSymbols().getMonths()[now.minusMonths(j).getMonthValue()];
        try {
            XYChart.Series series = new XYChart.Series();

            for (int i = 0, j = 6; i < 7; i++, j--)
                series.getData().add(new XYChart.Data(month[i], ReadFile.DataStorage.creditCard.getBalanceRecorder(j)));

            expenseChart.getData().addAll(series);
        } catch (Exception ignored) { }
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
            Logger.getLogger(CreditCardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
