package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanController implements Initializable {

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

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadFile.DataStorage.loanRepaymentHistoryArrayList.sort(new CustomComparatorHistoryLatest());
        transactionDate.setCellValueFactory(new PropertyValueFactory<TransactionHistory, LocalDate>("transactionDate"));
        transactionAmount.setCellValueFactory(new PropertyValueFactory<TransactionHistory, Double>("amount"));
        tableView.setItems(getTransactionHistory(ReadFile.DataStorage.loanRepaymentHistoryArrayList));

        if (ReadFile.DataStorage.isPersonalLoan) {
            loanTypeLabel.setText(ReadFile.DataStorage.personalLoan.getLoanType());
            outstandingBalanceLabel.setText(String.format("%.2f", ReadFile.DataStorage.personalLoan.getOutstandingBalance()));
            initialBalanceLabel.setText(String.format("%.2f", ReadFile.DataStorage.personalLoan.getInitialLoanAmount()));
            monthlyRepaymentLabel.setText(String.format("%.2f", ReadFile.DataStorage.personalLoan.getMonthlyRepayment()));

            ReadFile.DataStorage.personalLoan.updateOverdueDays();
            if (ReadFile.DataStorage.personalLoan.getOverdueDays() > 0)
                overdueLabel.setText("Overdue for " + (ReadFile.DataStorage.personalLoan.getOverdueDays()) + " days");
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
                overdueLabel.setText("Overdue for " + (ReadFile.DataStorage.businessLoan.getOverdueDays()) + " days");
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

            //displayTransaction();

            VBox vBox = new VBox(collateralTypeLabel, collateralType, collateralAmountLabel, collateralAmount);
            vBox.setPrefHeight(100);
            vBox.setPrefWidth(250);
            PopOver popOver = new PopOver(vBox);

            loanTypeLabel.setOnMouseEntered(mouseEvent -> {
                popOver.show(loanTypeLabel);
            });

            loanTypeLabel.setOnMouseExited(mouseEvent -> {
                popOver.hide();
            });
        }
    }

    @FXML
    public void accountButtonPushed() { loadNextScene("accountScene.fxml"); }

    @FXML
    public void transactionHistoryButtonPushed() { loadNextScene("transactionHistoryScene.fxml"); }

    @FXML
    public void transferButtonPushed() { loadNextScene("transferScene.fxml"); }

    @FXML
    public void dashBoardButtonPushed() { loadNextScene("currencyExchangeScene.fxml"); }

    @FXML
    public void aboutUsButtonPushed() {
        loadNextScene("aboutUsScene.fxml");
    }

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

    public ObservableList<TransactionHistory> getTransactionHistory(ArrayList<TransactionHistory> arrayList) {
        ObservableList<TransactionHistory> transactionHistories = FXCollections.observableArrayList();

        for (int i = 0; i < arrayList.size(); i++)
            transactionHistories.add(arrayList.get(i));

        return transactionHistories;
    }

    /*public void displayTransaction() {

        for (int row = 0, yPosition = 5; row < 7; row++, yPosition += 35) {
            if (row == ReadFile.DataStorage.loanRepaymentHistoryArrayList.size())
                break;

            Label dateLabel = new Label();
            Font font = Font.font("Arial Rounded MT Bold",13);
            dateLabel.setText(ReadFile.DataStorage.loanRepaymentHistoryArrayList.get(row).getTransactionDate().format(formatter));
            dateLabel.setTextFill(Color.web("#FFFFFF"));
            dateLabel.setFont(font);
            dateLabel.setTranslateX(10);
            dateLabel.setTranslateY(yPosition);
            pane.getChildren().add(dateLabel);

            Label label = new Label();
            label.setText("MYR " + String.format("%.2f", ReadFile.DataStorage.loanRepaymentHistoryArrayList.get(row).getAmount()));
            label.setTextFill(Color.web("#21DADA"));
            label.setFont(font);
            label.setTranslateX(300);
            label.setTranslateY(yPosition);
            pane.getChildren().add(label);
            addLine(yPosition);
        }
    }

    public void addLine(int yPosition) {
        Line line = new Line();
        line.setStartX(0);
        line.setEndX(400);
        line.setStartY(yPosition + 24);
        line.setEndY(yPosition + 24);
        line.setStyle("-fx-stroke: white");
        pane.getChildren().add(line);
    } */

    @FXML
    public void onlineBankingPushed() {
        if (!ReadFile.DataStorage.savingsAccount.savingsPaymentValidation(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
            ReadFile.DataStorage.businessLoan.getMonthlyRepayment()))
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
        popOver.show(onlineBankingButton);

        nextButton.setOnAction(actionEvent -> {
            if (!paidAlready())
                return;

            if (otpField.getText().equals(instance.getOTP())) {
                ReadFile.DataStorage.savingsAccount.savingsAccountPayment(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment());
                new Thread(Loan.updateHistoryTask).start();
                addInList(LocalDate.now(), (ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment()));

                try {
                    Class.forName("oracle.jdbc.OracleDriver");
                    Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", ReadFile.accPassword);
                    Statement statement = connect.createStatement();

                    statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.savingsAccount.getBalance() +
                            " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You have successfully paid your monthly debt.");
                alert.showAndWait();

                loadNextScene("loanScene.fxml");
            }

        });
    }

    @FXML
    public void creditCardPushed() {
        if (!ReadFile.DataStorage.creditCard.creditCardUsageValidation(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                ReadFile.DataStorage.businessLoan.getMonthlyRepayment()))
            return;

        System.out.println(ReadFile.DataStorage.creditCard.getExpiryDate());

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
        popOver.show(creditCardButton);

        nextButton.setOnAction(actionEvent -> {
            if (!paidAlready())
                return;

            if (cardNumField.getText().equals(ReadFile.DataStorage.creditCard.getCardID()) && expDateField.getText().equals(ReadFile.DataStorage.creditCard.getExpiryDate()) &&
            cvvField.getText().equals(ReadFile.DataStorage.creditCard.getCvv()) && otpField.getText().equals(instance.getOTP())) {
                ReadFile.DataStorage.creditCard.creditUsage(ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment());
                new Thread(Loan.updateHistoryTask).start();
                addInList(LocalDate.now(), (ReadFile.DataStorage.isPersonalLoan ? ReadFile.DataStorage.personalLoan.getMonthlyRepayment() :
                        ReadFile.DataStorage.businessLoan.getMonthlyRepayment()));

                try {
                    Class.forName("oracle.jdbc.OracleDriver");
                    Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", ReadFile.accPassword);
                    Statement statement = connect.createStatement();

                    statement.executeQuery("UPDATE CREDITCARD SET CARD_OUTSTANDING_BALANCE = " + ReadFile.DataStorage.creditCard.getOutstandingBalance() +
                            " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You have successfully paid your monthly debt.");
                alert.showAndWait();

                loadNextScene("loanScene.fxml");
            }
        });
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
