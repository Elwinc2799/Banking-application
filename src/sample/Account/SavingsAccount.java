package sample.Account;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import sample.ReadFile;

import java.sql.*;
import java.time.LocalDate;

public class SavingsAccount extends Account {

    public double interestRate = 1.5;
    public double dailyLimit;
    public double monthExpenditure;
    public double[] balanceRecorder;

    public double getInterestRate() {
        return interestRate;
    }

    public double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public double getMonthExpenditure() { return monthExpenditure; }

    public void setMonthExpenditure(double monthExpenditure) { this.monthExpenditure = monthExpenditure; }

    public double getBalanceRecorder(int index) { return balanceRecorder[index]; }

    public void setBalanceRecorder(double[] balanceRecorder) { this.balanceRecorder = balanceRecorder; }

    //boolean function to ensure that the transaction amount is not exceed the user's account balance
    public boolean savingsPaymentValidation(double amount) {
        if (balance - amount < 10 || dailyLimit - amount < 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText((balance - amount < 10) ? "The transaction amount exceed the your account balance." : "You have exceed your daily transaction limit");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    //update account balance if the month is divisible by 3, the day of month is 1 and the balance update status is not true
    public void updateBalance() {
        if (LocalDate.now().getMonthValue() % 3 == 0 && LocalDate.now().getDayOfMonth() == 1 && !isBalanceUpdateStatus()) {
            balance *= Math.pow((1 + (getInterestRate()) / 400), (4 * 3 / 12));

            balance = (double) Math.round(balance * 100) / 100;
            setBalanceUpdateStatus(true);

            //update information to the database
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeUpdate("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.savingsAccount.getBalance() +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");
                new Thread(updateSavingsStatus).start();

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        //if the day of month is exceed 28, set the account update status to 'N'
        if (LocalDate.now().getDayOfMonth() > 28) {
            //update information to the database
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeUpdate("UPDATE ACCOUNT SET ACCOUNT_UPDATE_STATUS = 'N'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }
    }

    //task to update account status to the database
    Task<Void> updateSavingsStatus = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeUpdate("UPDATE ACCOUNT SET ACCOUNT_UPDATE_STATUS = 'Y'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    //update the balance after payment had made
    public void savingsAccountPayment(double amount) {
        savingsPaymentValidation(amount);
        balance -= amount;
        setMonthExpenditure(getMonthExpenditure() + amount);
    }
}
