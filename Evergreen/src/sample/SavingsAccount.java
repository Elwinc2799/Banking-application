package sample;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.sql.*;
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
        Calendar calendar = Calendar.getInstance();
        int month = (calendar.get(Calendar.MONTH));

        if (month % 3 == 0 && calendar.get(Calendar.DAY_OF_MONTH) == 1 && !isBalanceUpdateStatus()) {
            balance *= Math.pow((1 + (getInterestRate()) / 400), (4 * 3 / 12));

            balance = (double) Math.round(balance * 100) / 100;
            setBalanceUpdateStatus(true);

            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "ericcheah575");
                Statement statement = connect.createStatement();

                statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_BALANCE = " + ReadFile.DataStorage.savingsAccount.getBalance() +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");
                new Thread(updateSavingsStatus).start();
                new Thread(updateExpenditureTask).start();
                new Thread(updateExpenditureRecorderTask).start();

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }

        if (calendar.get(Calendar.DAY_OF_MONTH) > 27) {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "ericcheah575");
                Statement statement = connect.createStatement();

                statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_UPDATE_STATUS = 'N'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }
    }

    Task<Void> updateSavingsStatus = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "ericcheah575");
                Statement statement = connect.createStatement();

                statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_UPDATE_STATUS = 'Y'" +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    Task<Void> updateExpenditureTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "ericcheah575");
                Statement statement = connect.createStatement();

                statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_EXPENDITURE = " + 0 +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    Task<Void> updateExpenditureRecorderTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "ericcheah575");
                Statement statement = connect.createStatement();
                double[] tempBalance = ReadFile.DataStorage.savingsAccount.balanceRecorder.clone();

                System.arraycopy(tempBalance, 0, tempBalance, 1, 5);

                tempBalance[0] = getMonthExpenditure();

                statement.executeQuery("UPDATE SAVINGS_EXPENSE SET " +
                        "ONE_MONTH_AGO = " + tempBalance[0] +
                        "TWO_MONTH_AGO = " + tempBalance[1] +
                        "THREE_MONTH_AGO = " + tempBalance[2] +
                        "FOUR_MONTH_AGO = " + tempBalance[3] +
                        "FIVE_MONTH_AGO = " + tempBalance[4] +
                        "SIX_MONTH_AGO = " + tempBalance[5] +
                        "SEVEN_MONTH_AGO = " + tempBalance[6] +
                        " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    // bug fixes
    public void changeLimit(double amount) {
        this.dailyLimit = amount;

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", "ericcheah575");
            Statement statement = connect.createStatement();

            statement.executeQuery("UPDATE ACCOUNT SET ACCOUNT_DAILY_LIMIT = " + amount +
                    " WHERE ACCOUNT_ID = '" + ReadFile.DataStorage.savingsAccount.getAccountNum() + "'");

        } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
    }

    public void savingsAccountPayment(double amount) {
        savingsPaymentValidation(amount);
        balance -= amount;
        setMonthExpenditure(getMonthExpenditure() + amount);
    }
}
