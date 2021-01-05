package sample.Account;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import sample.ReadFile;

import java.sql.*;
import java.time.LocalDate;
import java.util.Calendar;

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

    public boolean savingsPaymentValidation(double amount) {
        if (balance - amount < 10 || dailyLimit - amount < 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText((balance - amount < 10) ? "The transaction amount exceed the your account balance." : "You have exceed your daily transaction limit");
            alert.showAndWait();
            return false;
        }

        return true;
    }

    public void updateBalance() {
        if (LocalDate.now().getMonthValue() % 3 == 0 && LocalDate.now().getDayOfMonth() == 1 && !isBalanceUpdateStatus()) {
            balance *= Math.pow((1 + (getInterestRate()) / 400), (4 * 3 / 12));

            balance = (double) Math.round(balance * 100) / 100;
            setBalanceUpdateStatus(true);

            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.savingsAccount.getBalance() +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");
                new Thread(updateSavingsStatus).start();

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        if (LocalDate.now().getDayOfMonth() > 28) {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_UPDATE_STATUS = 'N'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }
    }

    Task<Void> updateSavingsStatus = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_UPDATE_STATUS = 'Y'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    public void savingsAccountPayment(double amount) {
        savingsPaymentValidation(amount);
        balance -= amount;
        setMonthExpenditure(getMonthExpenditure() + amount);
    }
}
