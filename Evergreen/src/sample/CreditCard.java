package sample;

import java.util.Calendar;

public class CreditCard extends Card {

    public int fixedMonthlyLimit;
    public int monthlyLimit = fixedMonthlyLimit;
    public int overDraftCounter;
    public int latePaymentCounter;
    public int lastPaidMonth;

    public double outstandingBalance;
    public double annualRates = 1.2;
    public double[][] additionalRates = { { 12, 13, 15 },
                                            { 5, 7, 10 }
    };

    public String creditScore;

    public int getFixedMonthlyLimit() { return fixedMonthlyLimit; }

    public void setFixedMonthlyLimit(int fixedMonthlyLimit) { this.fixedMonthlyLimit = fixedMonthlyLimit; }

    public int getMonthlyLimit() { return monthlyLimit; }

    public int getLatePaymentCounter() { return latePaymentCounter; }

    public void setLatePaymentCounter(int latePaymentCounter) { this.latePaymentCounter = latePaymentCounter; }

    public int getOverDraftCounter() { return overDraftCounter; }

    public void setOverDraftCounter(int exceedLimitCounter) { this.overDraftCounter = exceedLimitCounter; }

    public double getOutstandingBalance() { return outstandingBalance; }

    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }

    public double getAnnualRates() { return annualRates; }

    public int getLastPaidMonth() { return lastPaidMonth; }

    public void setLastPaidMonth(int lastPaidMonth) { this.lastPaidMonth = lastPaidMonth; }

    public String getCreditScore() { return creditScore; }

    public void updateOutstandingBalance() {
        int lateCategory = 0;
        int overdraftCategory;
        int counter = getLatePaymentCounter();

        switch (counter) {
            case 1: case 2:
            case 3: lateCategory = 0;
                break;

            case 4: case 5: case 6:
            case 7: lateCategory = 1;
                break;

            case 8: case 9: case 10:
            case 11: lateCategory = 2;
                break;

            default: break;
        }

        if (getOutstandingBalance() - monthlyLimit > 50000)
            overdraftCategory = 2;
        else if (getOutstandingBalance() - monthlyLimit >= 25000 && getOutstandingBalance() - monthlyLimit < 50000)
            overdraftCategory = 1;
        else
            overdraftCategory = 0;

        outstandingBalance += outstandingBalance * (getAnnualRates() / 12);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);

        outstandingBalance += (counter > 0) ? (outstandingBalance * (day * (additionalRates[0][lateCategory] / 365))) : 0;
        outstandingBalance += (getOutstandingBalance() > 0) ? (outstandingBalance * (day * (additionalRates[1][overdraftCategory] / 365))) : 0;

        outstandingBalance = (double) Math.round(outstandingBalance * 100) / 100;
    }

    public void creditPayment(double amount) {
        Calendar calendar = Calendar.getInstance();
        int month = (calendar.get(Calendar.MONTH) + 1);

        outstandingBalance -= amount;
        setLastPaidMonth(month);
    }

    public void updateLatePaymentCounter() {
        Calendar calendar = Calendar.getInstance();
        int month = (calendar.get(Calendar.MONTH) + 1);

        latePaymentCounter = getLastPaidMonth() - month;
    }

    public void creditUsage(int amount) {
        outstandingBalance += amount;
        monthlyLimit -= amount;

        updateOverdraftCounter();
    }

    public void updateOverdraftCounter() {
        overDraftCounter += (getMonthlyLimit() < 0) ? 1 : 0;
    }

    public double updateCreditScore() {
        double creditScoreValue = ((30 * (overDraftCounter + latePaymentCounter) / 12) + (0.7 * outstandingBalance / getFixedMonthlyLimit())) * 10;

        if (creditScoreValue > 80)
            creditScore = "Bad";
        else if (creditScoreValue >= 50 && creditScoreValue < 80)
            creditScore = "Fair";
        else if (creditScoreValue >= 15 && creditScoreValue < 50)
            creditScore = "Good";
        else
            creditScore = "Exceptional";

        return (double) Math.round(creditScoreValue * 100) / 100;
    }
}
