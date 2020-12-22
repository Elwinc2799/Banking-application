package sample;

public class BusinessLoan extends Loan {

    public double loanLimit;
    public double[][] interestRate = { { 8.45, 9.35, 10.15 },
                                        { 9.55, 10.65, 12.45 }
    };

    public BusinessCollateral businessCollateral;

    public double[][] getInterestRate() { return interestRate; }

    public double getLoanLimit() { return loanLimit; }

    public void setLoanLimit(double loanLimit) { this.loanLimit = loanLimit; }
}
