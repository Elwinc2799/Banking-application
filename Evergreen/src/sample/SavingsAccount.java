package sample;

import javafx.scene.control.Alert;

import java.util.Calendar;

public class SavingsAccount extends Account {

    public double interestRate = 1.5;
    public double dailyLimit;

    public double getInterestRate() {
        return interestRate;
    }

    public double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public void savingsPaymentValidation(double amount) {
        if (balance - amount < 10) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("The transaction amount exceed the your account balance.");
            alert.showAndWait();
            return;
        }

        if (dailyLimit - amount < 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You have exceed your daily transaction limit");
            alert.showAndWait();
            return;
        }
    }

    public void updateBalance() {
        Calendar calendar = Calendar.getInstance();
        int month = (calendar.get(Calendar.MONTH) + 1);

        if (month % 3 == 0) {
            balance *= Math.pow((1 + (getInterestRate()) / 400), (4 * 3 / 12));
            balance = (double) Math.round(balance * 100) / 100;
        }
    }

    public void changeLimit(int amount) {
        this.dailyLimit = amount;
    }
}
