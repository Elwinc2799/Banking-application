package sample;

import java.util.Calendar;

public class DepositAccount extends Account {

    public double[][] interestRate = { { 1.85, 1.9, 2.1, 2.2 },
                                        { 1.9, 1.95, 2.15, 2.25 },
                                        { 2.0, 2.1, 2.2, 2.3 }
    };

    public String depositType;

    public double getInterestRate(int depositMonth, int depositType) { return interestRate[depositMonth][depositType]; }

    public String getDepositType() { return depositType; }

    public void setDepositType(String depositType) { this.depositType = depositType; }

    public void updateBalance() {
        int depositType = 0;

        if (getDepositType().equals("Intermediate"))
            depositType = 1;
        else if (getDepositType().equals("Advance"))
            depositType = 2;

        Calendar calendar = Calendar.getInstance();
        int month = (calendar.get(Calendar.MONTH) + 1);

        if (month % 3 == 0) {
            switch (month - getDateOpen()) {
                case 3: balance *= Math.pow((1 + (getInterestRate(0, depositType) / 400)), (4 * 3 / 12));
                    break;

                case 6: balance *= Math.pow((1 + (getInterestRate(1, depositType) / 400)), (4 * 6 / 12));
                    break;

                case 9: balance *= Math.pow((1 + (getInterestRate(2, depositType) / 400)), (4 * 9 / 12));
                    break;

                case 12: balance *= Math.pow((1 + (getInterestRate(3, depositType) / 400)), (4 * 12 / 12));
                    break;

                default:
                    break;
            }

            balance = (double) Math.round(balance * 100) / 100;
        }

        // write output to file / database
    }

    public void renewal() {
        Calendar calendar = Calendar.getInstance();
        int month = (calendar.get(Calendar.MONTH) + 1);

        if (getDateOpen() % 12 == 0) {
            // prompt user for renewal, send notification98
        }
    }
}
