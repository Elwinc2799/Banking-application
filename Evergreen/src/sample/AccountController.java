package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountController implements Initializable {

    @FXML
    private Label nameLabel, emailLabel, savingsAccountLabel, depositAccountLabel, dateLabel, savingsAmountLabel, depositAmountLabel;

    @FXML
    private Label creditNumLabel, debitNumLabel, creditDateLabel, debitDateLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SavingsAccount savingsAccount = new SavingsAccount();

        savingsAccount.setName(ReadFile.DataStorage.getName());
        savingsAccount.setEmail(ReadFile.DataStorage.getUserEmail());
        savingsAccount.setAccountNum(ReadFile.DataStorage.getAccountID());
        savingsAccount.setBalance(ReadFile.DataStorage.getBalance());
        savingsAccount.setDateOpen(ReadFile.DataStorage.getMonthOpen());
        savingsAccount.setDailyLimit(1000);

        nameLabel.setText(savingsAccount.getName());
        emailLabel.setText(savingsAccount.getEmail());
        savingsAccountLabel.setText(savingsAccount.getAccountNum());
        savingsAmountLabel.setText(Double.toString(savingsAccount.getBalance()));

        savingsAccount.updateBalance();
        System.out.println(savingsAccount.getBalance());

        CreditCard creditCard = new CreditCard();
        creditCard.setCardID(ReadFile.DataStorage.getCreditCardID());
        creditCard.setExpiryDate(ReadFile.DataStorage.getCardExpiryDate());
        creditCard.setFixedMonthlyLimit(ReadFile.DataStorage.getCardMonthlyLimit());
        creditCard.setOutstandingBalance(ReadFile.DataStorage.getCardOutstandingBalance());

        creditNumLabel.setText("**** **** **** " + creditCard.getCardID().substring(12));
        creditDateLabel.setText(creditCard.getExpiryDate());


        creditCard.updateOutstandingBalance();

        System.out.println(creditCard.getOutstandingBalance());
        System.out.println(creditCard.updateCreditScore());
        System.out.println(creditCard.getCreditScore());
    }
}
