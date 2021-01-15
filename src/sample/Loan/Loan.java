package sample.Loan;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import sample.ReadFile;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

public class Loan {

    public int overdueCounter;
    public int overdueDays;
    public int loanDuration;

    public String loanID;
    public String loanType;

    public double initialLoanAmount;
    public double outstandingBalance;
    public double monthlyRepayment;

    public LocalDate lastDatePaid;
    public boolean loanOutstandingBalanceUpdated;

    public int getOverdueCounter() { return overdueCounter; }

    public void setOverdueCounter(int overdueCounter) { this.overdueCounter = overdueCounter; }

    public int getOverdueDays() { return overdueDays; }

    public int getLoanDuration() { return loanDuration; }

    public void setLoanDuration(int loanDuration) { this.loanDuration = loanDuration; }

    public String getLoanID() { return loanID; }

    public void setLoanID(String loanID) { this.loanID = loanID; }

    public String getLoanType() { return loanType; }

    public void setLoanType(String loanType) { this.loanType = loanType; }

    public double getInitialLoanAmount() { return initialLoanAmount; }

    public void setInitialLoanAmount(double initialLoanAmount) { this.initialLoanAmount = initialLoanAmount; }

    public double getOutstandingBalance() { return outstandingBalance; }

    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }

    public double getMonthlyRepayment() { return monthlyRepayment; }

    public void setMonthlyRepayment(double monthlyRepayment) { this.monthlyRepayment = monthlyRepayment; }

    public LocalDate getLastDatePaid() { return lastDatePaid; }

    public void setLastDatePaid(LocalDate lastDatePaid) { this.lastDatePaid = lastDatePaid; }

    public boolean isLoanOutstandingBalanceUpdated() { return loanOutstandingBalanceUpdated; }

    public void setLoanOutstandingBalanceUpdated(boolean loanOutstandingBalanceUpdated) { this.loanOutstandingBalanceUpdated = loanOutstandingBalanceUpdated; }

    //boolean function to determine whether the user had paid for the loan for this month or not
    public boolean loanPayment(double amount) {
        if (getLastDatePaid().getMonth().equals(LocalDate.now().getMonth())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You have already paid for this month.");
            alert.showAndWait();
            return false;
        }

        LocalDate newDate = LocalDate.now();

        outstandingBalance -= amount;
        setLastDatePaid(newDate);

        //update information to the database
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Statement statement = ReadFile.connect.createStatement();

            statement.executeUpdate("UPDATE LOAN SET LOAN_OUTSTANDING_BALANCE = " + getOutstandingBalance() +
                    " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");

            new Thread(updateDateTask).start();

        } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

        return true;
    }

    //task to update the date to database
    Task<Void> updateDateTask = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                PreparedStatement preparedStatement = ReadFile.connect.prepareStatement
                        ("UPDATE LOAN SET LOAN_LAST_PAID_DATE = ? WHERE USERNAME = ?" );
                preparedStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                preparedStatement.setString(2, ReadFile.DataStorage.getUsername());
                preparedStatement.execute();

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    //task to update the status to the database
    Task<Void> updateStatusTask = new Task<Void>() {
        @Override
        protected Void call() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeUpdate("UPDATE LOAN SET LOAN_UPDATE_STATUS = 'Y'" +
                        " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    //function to update the outstanding balance in loan
    public void updateOutstandingBalance(double[][] interestRate) {
        updateOverdueDays();
        int loanInterestCategory = getLoanInterestCategory((int) getInitialLoanAmount() / 25000);
        int overdueCategory = getOverdueCategory();
        int counter = getOverdueCounter();

        LocalDate dateBefore = lastDatePaid.plusMonths(1);
        LocalDate dateAfter = LocalDate.now();
        int months = (int) ChronoUnit.MONTHS.between(dateBefore, dateAfter);
        setLoanOutstandingBalanceUpdated(true);

        new Thread(updateStatusTask).start();

        //if the loan outstanding balance status is not updated and the month is more than 1
        if (!isLoanOutstandingBalanceUpdated() && months > 0) {
            outstandingBalance += outstandingBalance * (interestRate[0][loanInterestCategory] / 1200);
            if (months > 0)
                outstandingBalance += (counter > 0) ? (outstandingBalance * (months * (interestRate[1][overdueCategory]) / 1200)) : 0;

            //update information to the database
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Statement statement = ReadFile.connect.createStatement();

                statement.executeUpdate("UPDATE LOAN SET LOAN_OUTSTANDING_BALANCE = " + getOutstandingBalance() +
                        " WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
        }
    }

    //function to get the loan interest category
    public int getLoanInterestCategory(double loanLimit) {
        int loanInterestCategory;

        if ((getInitialLoanAmount() / loanLimit) * 10 > 8)
            loanInterestCategory = 2;
        else if (((getInitialLoanAmount() / loanLimit) * 10 > 4) && ((getInitialLoanAmount() / loanLimit) * 10 < 8))
            loanInterestCategory = 1;
        else
            loanInterestCategory = 0;

        return loanInterestCategory;
    }

    //function to determine the loan overdue category
    public int getOverdueCategory() {
        int overdueCategory = 0;
        int counter = getOverdueCounter();

        switch (counter) {
            case 1: case 2:
            case 3: overdueCategory = 0;
                break;

            case 4: case 5: case 6:
            case 7: overdueCategory = 1;
                break;

            case 8: case 9: case 10:
            case 11: overdueCategory = 2;
                break;

            default: break;
        }

        return overdueCategory;
    }

    //function to update the overdue days
    public void updateOverdueDays() {
        LocalDate dateBefore = lastDatePaid.with(lastDayOfMonth());
        LocalDate dateAfter = LocalDate.now();

        setOverdueCounter((int) ChronoUnit.MONTHS.between(dateBefore, dateAfter));
        overdueDays = (int) ChronoUnit.DAYS.between(dateBefore, dateAfter);
    }

    //function to calculate and show the approximated date when the user will finish their loan payment
    public int approximatedDate(double[][] interestRate) {
        int monthCounter;
        int counter = getOverdueCounter();
        double balance = getOutstandingBalance();
        double monthlyRepayment = getMonthlyRepayment();
        double interest;

        for (monthCounter = 0; balance > getMonthlyRepayment(); monthCounter++) {
            interest = balance * (interestRate[0][getLoanInterestCategory(getInitialLoanAmount())] / 1200);
            if (counter > 0)
                interest += balance * (interestRate[1][getOverdueCategory()] / 1200);

            balance -= (monthlyRepayment - interest);
        }

        return monthCounter;
    }
}
