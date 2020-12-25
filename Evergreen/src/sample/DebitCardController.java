package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebitCardController implements Initializable {

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
    private CategoryAxis monthAxis;

    @FXML
    private NumberAxis balanceAxis;

    @FXML
    private Button changeLimitButton;

    TextField textField = new TextField();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        XYChart.Series series = new XYChart.Series();
        String[] month = new String[7];
        Calendar calendar = Calendar.getInstance();

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

        for (int i = 0, j = 7; i < 7; i++, j--)
            month[i] = new DateFormatSymbols().getMonths()[calendar.get(Calendar.MONTH) - j];

        for (int i = 0; i < 7; i++)
            series.getData().add(new XYChart.Data(month[i], ReadFile.DataStorage.savingsAccount.getBalanceRecorder(i)));

        balanceChart.getData().addAll(series);
    }

    @FXML
    public void changeLimitPushed() {
        TransferController instance = new TransferController();
        new Thread(instance.sendOTPTask).start();

        Label newLimit = new Label("New Limit");
        Label otp = new Label("OTP");
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
        popOver.show(changeLimitButton);

        nextButton.setOnAction(actionEvent -> {
            if (otpField.getText().equals(instance.getOTP()) && ReadFile.DataStorage.debitCard.isValid(textField.getText())) {
                try {
                    Class.forName("oracle.jdbc.OracleDriver");
                    Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "ericcheah575");
                    Statement statement = connect.createStatement();

                    statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_DAILY_LIMIT = " + Double.parseDouble(textField.getText()) +
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

    @FXML
    public void accountButtonPushed() { loadNextScene("accountScene.fxml"); }

    @FXML
    public void transactionHistoryButtonPushed() { loadNextScene("transactionHistoryScene.fxml"); }

    @FXML
    public void transferButtonPushed() { loadNextScene("transferScene.fxml"); }

    @FXML
    public void loanButtonPushed() {
        loadNextScene((ReadFile.DataStorage.loan) ? "loanScene.fxml" : "noLoanScene.fxml");
    }

    @FXML
    public void dashBoardButtonPushed() { loadNextScene("currencyExchangeScene.fxml"); }

    @FXML
    public void aboutUsButtonPushed() { loadNextScene("aboutUsScene.fxml"); }

    private void loadNextScene(String fxml) {
        try {
            Parent secondView;
            secondView = FXMLLoader.load(getClass().getResource(fxml));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
