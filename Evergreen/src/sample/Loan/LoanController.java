package sample.Loan;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import sample.Compare.CustomComparatorHistoryLatest;
import sample.ReadFile;
import sample.TransactionHistory;
import sample.TransferController;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanController implements Initializable {

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
    private Label loanTypeLabel;

    @FXML
    private Label outstandingBalanceLabel;

    @FXML
    private Label currentPaymentLabel;

    @FXML
    private Label overdueLabel;

    @FXML
    private Label nextPaymentLabel;

    @FXML
    private Label initialBalanceLabel;

    @FXML
    private Label finalPaymentLabel;

    @FXML
    private Label monthlyRepaymentLabel;

    @FXML
    private Button onlineBankingButton;

    @FXML
    private Button creditCardButton;

    @FXML
    private TableView<TransactionHistory> tableView;

    @FXML
    private TableColumn<TransactionHistory, LocalDate> transactionDate;

    @FXML
    private TableColumn<TransactionHistory, Double> transactionAmount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadFile.DataStorage.loanRepaymentHistoryArrayList.sort(new CustomComparatorHistoryLatest());
        transactionDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        transactionAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tableView.setItems(getTransactionHistory(ReadFile.DataStorage.loanRepaymentHistoryArrayList));

        if (ReadFile.DataStorage.isPersonalLoan) {
            loanTypeLabel.setText(ReadFile.DataStorage.personalLoan.getLoanType());
            outstandingBalanceLabel.setText(String.format("%.2f", ReadFile.DataStorage.personalLoan.getOutstandingBalance()));
            initialBalanceLabel.setText(String.format("%.2f", ReadFile.DataStorage.personalLoan.getInitialLoanAmount()));
            monthlyRepaymentLabel.setText(String.format("%.2f", ReadFile.DataStorage.personalLoan.getMonthlyRepayment()));

            ReadFile.DataStorage.personalLoan.updateOverdueDays();
            if (ReadFile.DataStorage.personalLoan.getOverdueDays() > 0)
                overdueLabel.setText("Overdue for " + (ReadFile.DataStorage.personalLoan.getOverdueDays()) + " day(s)");
            else {
                overdueLabel.setText("Payment is paid");
                overdueLabel.setTextFill(Color.web("#21dada"));
            }

            currentPaymentLabel.setText(LocalDate.now().getMonth() + ", 1");
            nextPaymentLabel.setText((ReadFile.DataStorage.personalLoan.getOutstandingBalance() - ReadFile.DataStorage.personalLoan.getMonthlyRepayment() > 0) ?
                    LocalDate.now().plusMonths(1).getMonth() + ", 1" : "-");

            finalPaymentLabel.setText(LocalDate.now().plusMonths(ReadFile.DataStorage.personalLoan.getLoanDuration()).getMonth() + " " + LocalDate.now().plusMonths(ReadFile.DataStorage.personalLoan.getLoanDuration()).getYear() + ", 1");
        } else {
            loanTypeLabel.setText(ReadFile.DataStorage.businessLoan.getLoanType());
            outstandingBalanceLabel.setText(String.format("%.2f", ReadFile.DataStorage.businessLoan.getOutstandingBalance()));
            initialBalanceLabel.setText(String.format("%.2f", ReadFile.DataStorage.businessLoan.getInitialLoanAmount()));
            monthlyRepaymentLabel.setText(String.format("%.2f", ReadFile.DataStorage.businessLoan.getMonthlyRepayment()));

            ReadFile.DataStorage.businessLoan.updateOverdueDays();
            if (ReadFile.DataStorage.businessLoan.getOverdueDays() > 0)
                overdueLabel.setText("Overdue for " + (ReadFile.DataStorage.businessLoan.getOverdueDays()) + " day(s)");
            else {
                overdueLabel.setText("Payment is paid");
                overdueLabel.setTextFill(Color.web("#21dada"));
            }

            currentPaymentLabel.setText(LocalDate.now().getMonth() + ", 1");

            if ((ReadFile.DataStorage.businessLoan.getOutstandingBalance() - ReadFile.DataStorage.businessLoan.getMonthlyRepayment() > 0)) {
                nextPaymentLabel.setText(LocalDate.now().plusMonths(1).getMonth() + ", 1");
            } else {
                nextPaymentLabel.setText("-");
            }

            finalPaymentLabel.setText(LocalDate.now().plusMonths(ReadFile.DataStorage.businessLoan.getLoanDuration()).getMonth() + " " + LocalDate.now().plusMonths(ReadFile.DataStorage.businessLoan.getLoanDuration()).getYear() + ", 1");

            Label collateralTypeLabel = new Label("Collateral Type");
            Label collateralType = new Label(ReadFile.DataStorage.businessLoan.getCollateralType());
            Label collateralAmountLabel = new Label("Collateral Estimated Amount");
            Label collateralAmount = new Label("MYR " + String.format("%.2f", ReadFile.DataStorage.businessLoan.getCollateralAmount()));
            collateralTypeLabel.setFont(new Font("Arial", 14));
            collateralTypeLabel.setTranslateX(10);
            collateralTypeLabel.setTranslateY(10);
            collateralType.setFont(new Font("Arial", 14));
            collateralType.setTranslateX(15);
            collateralType.setTranslateY(15);
            collateralAmountLabel.setFont(new Font("Arial", 14));
            collateralAmountLabel.setTranslateX(10);
            collateralAmountLabel.setTranslateY(20);
            collateralAmount.setFont(new Font("Arial", 14));
            collateralAmount.setTranslateX(15);
            collateralAmount.setTranslateY(25);

            VBox vBox = new VBox(collateralTypeLabel, collateralType, collateralAmountLabel, collateralAmount);
            vBox.setPrefHeight(100);
            vBox.setPrefWidth(250);
            PopOver popOver = new PopOver(vBox);
            popOver.setHeaderAlwaysVisible(true);
            popOver.setTitle("Business Loan");

            loanTypeLabel.setOnMouseEntered(mouseEvent -> popOver.show(loanTypeLabel));

            loanTypeLabel.setOnMouseExited(mouseEvent -> popOver.hide());
        }
    }

    @FXML
    public void accountButtonPushed() { loadNextScene("/sample/Scene/accountScene.fxml"); }

    @FXML
    public void transactionHistoryButtonPushed() { loadNextScene("/sample/Scene/transactionHistoryScene.fxml"); }

    @FXML
    public void transferButtonPushed() { loadNextScene("/sample/Scene/transferScene.fxml"); }

    @FXML
    public void dashBoardButtonPushed() { loadNextScene("/sample/Scene/currencyExchangeScene.fxml"); }

    @FXML
    public void aboutUsButtonPushed() {
        loadNextScene("/sample/Scene/aboutUsScene.fxml");
    }

    private void loadNextScene(String fxml) {
        try {
            Parent secondView;
            secondView = FXMLLoader.load(getClass().getResource(fxml));
            Scene newScene = new Scene(secondView);
            Stage curStage = (Stage) root.getScene().getWindow();
            curStage.setScene(newScene);
        } catch (IOException ex) {
            Logger.getLogger(LoanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<TransactionHistory> getTransactionHistory(ArrayList<TransactionHistory> arrayList) {
        ObservableList<TransactionHistory> transactionHistories = FXCollections.observableArrayList();

        transactionHistories.addAll(arrayList);

        return transactionHistories;
    }

    @FXML
    public void onlineBankingPushed() {
        if (!ReadFile.DataStorage.savingsAccount.savingsPaymentValidation(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
            ReadFile.DataStorage.businessLoan.getMonthlyRepayment()))
            return;

        if (isMonthlyDebtPaid())
            return;

        TransferController instance = new TransferController();
        new Thread(instance.sendOTPTask).start();

        Label otp = new Label("OTP");
        otp.setTranslateX(10);
        otp.setTranslateY(10);
        otp.setFont(new Font("Arial", 14));

        PasswordField otpField = new PasswordField();
        otpField.setPromptText("OTP");
        otpField.setTranslateX(10);
        otpField.setTranslateY(20);
        otpField.setMaxWidth(150);
        otpField.setFocusTraversable(false);

        Button nextButton = new Button();
        nextButton.setText("Next");
        nextButton.setTranslateX(125);
        nextButton.setTranslateY(30);

        VBox vBox = new VBox(otp, otpField, nextButton);
        vBox.setPrefHeight(100);
        vBox.setPrefWidth(200);

        PopOver popOver = new PopOver(vBox);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setTitle("Loan Payment");
        popOver.show(onlineBankingButton);

        nextButton.setOnAction(actionEvent -> {
            if (!paidAlready())
                return;

            if (otpField.getText().equals(instance.getOTP())) {
                ReadFile.DataStorage.savingsAccount.savingsAccountPayment(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment());
                new Thread(updateHistoryTask).start();
                addInList(LocalDate.now(), (ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment()));

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Statement statement = ReadFile.connect.createStatement();

                    statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.savingsAccount.getBalance() +
                            " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You have successfully paid your monthly debt.");
                alert.showAndWait();

                loadNextScene("/sample/Scene/loanScene.fxml");
            }

        });
    }

    static Task<Void> updateHistoryTask = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                PreparedStatement preparedStatement = ReadFile.connect.prepareStatement
                        ("UPDATE LOAN_REPAYMENT_HISTORY SET LOAN_REPAYMENT_DATE = ?, LOAN_REPAYMENT_AMOUNT = ? WHERE LOAN_ID = ?" );
                preparedStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                preparedStatement.setDouble(2, ReadFile.DataStorage.isPersonalLoan ?
                        ReadFile.DataStorage.personalLoan.getMonthlyRepayment() : ReadFile.DataStorage.businessLoan.getMonthlyRepayment());
                preparedStatement.setString(3, ReadFile.DataStorage.loanID);
                preparedStatement.execute();

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    @FXML
    public void creditCardPushed() {
        if (!ReadFile.DataStorage.creditCard.creditCardUsageValidation(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                ReadFile.DataStorage.businessLoan.getMonthlyRepayment()))
            return;

        if (isMonthlyDebtPaid())
            return;

        TransferController instance = new TransferController();
        new Thread(instance.sendOTPTask).start();

        Label cardNumLabel = new Label("Card No.");
        Label expDateLabel = new Label("Expiry Date");
        Label cvvLabel = new Label("CVV");
        Label otp = new Label("OTP");
        cardNumLabel.setTranslateX(10);
        cardNumLabel.setTranslateY(10);
        cardNumLabel.setFont(new Font("Arial", 14));
        expDateLabel.setTranslateX(10);
        expDateLabel.setTranslateY(40);
        expDateLabel.setFont(new Font("Arial", 14));
        cvvLabel.setTranslateX(10);
        cvvLabel.setTranslateY(70);
        cvvLabel.setFont(new Font("Arial", 14));
        otp.setTranslateX(10);
        otp.setTranslateY(100);
        otp.setFont(new Font("Arial", 14));

        TextField cardNumField = new TextField();
        TextField expDateField = new TextField();
        TextField cvvField = new TextField();
        PasswordField otpField = new PasswordField();
        cardNumField.setPromptText("Card No.");
        cardNumField.setTranslateX(10);
        cardNumField.setTranslateY(20);
        cardNumField.setMaxWidth(150);
        cardNumField.setFocusTraversable(false);
        expDateField.setPromptText("Expiry Date");
        expDateField.setTranslateX(10);
        expDateField.setTranslateY(50);
        expDateField.setMaxWidth(150);
        expDateField.setFocusTraversable(false);
        cvvField.setPromptText("CVV");
        cvvField.setTranslateX(10);
        cvvField.setTranslateY(80);
        cvvField.setMaxWidth(150);
        cvvField.setFocusTraversable(false);
        otpField.setPromptText("OTP");
        otpField.setTranslateX(10);
        otpField.setTranslateY(110);
        otpField.setMaxWidth(150);
        otpField.setFocusTraversable(false);

        Button nextButton = new Button();
        nextButton.setText("Next");
        nextButton.setTranslateX(125);
        nextButton.setTranslateY(150);

        VBox vBox = new VBox(cardNumLabel, cardNumField, expDateLabel, expDateField, cvvLabel, cvvField, otp, otpField, nextButton);
        vBox.setPrefHeight(350);
        vBox.setPrefWidth(200);

        PopOver popOver = new PopOver(vBox);
        popOver.setHeaderAlwaysVisible(true);
        popOver.setTitle("Loan Payment");
        popOver.show(creditCardButton);

        nextButton.setOnAction(actionEvent -> {
            if (!paidAlready())
                return;

            if (cardNumField.getText().equals(ReadFile.DataStorage.creditCard.getCardID()) && expDateField.getText().equals(ReadFile.DataStorage.creditCard.getExpiryDate()) &&
            cvvField.getText().equals(ReadFile.DataStorage.creditCard.getCvv()) && otpField.getText().equals(instance.getOTP())) {
                ReadFile.DataStorage.creditCard.creditUsage(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment());
                new Thread(updateHistoryTask).start();
                addInList(LocalDate.now(), (ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment()));

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Statement statement = ReadFile.connect.createStatement();

                    statement.executeQuery("UPDATE CREDITCARD SET CARD_OUTSTANDING_BALANCE = " + ReadFile.DataStorage.creditCard.getOutstandingBalance() +
                            " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You have successfully paid your monthly debt.");
                alert.showAndWait();

                loadNextScene("/sample/Scene/loanScene.fxml");
            }
        });
    }

    public boolean isMonthlyDebtPaid() {
        if (ReadFile.DataStorage.isPersonalLoan ?
                ReadFile.DataStorage.personalLoan.getLastDatePaid().getMonth().equals(LocalDate.now().getMonth()) :
                ReadFile.DataStorage.businessLoan.getLastDatePaid().getMonth().equals(LocalDate.now().getMonth())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You have already paid your monthly debt.");
            alert.showAndWait();
            return true;
        }

        return false;
    }

    public void addInList(LocalDate date, double amount) {
        TransactionHistory instance = new TransactionHistory();
        instance.setTransactionDate(date);
        instance.setAmount(amount);

        ReadFile.DataStorage.loanRepaymentHistoryArrayList.add(instance);
    }

    public boolean paidAlready() {
        if (ReadFile.DataStorage.isPersonalLoan)
            return ReadFile.DataStorage.personalLoan.loanPayment(ReadFile.DataStorage.personalLoan.getMonthlyRepayment());
        else
            return ReadFile.DataStorage.businessLoan.loanPayment(ReadFile.DataStorage.businessLoan.getMonthlyRepayment());
    }
}
