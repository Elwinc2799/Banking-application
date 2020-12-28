package sample.Loan;

public class PersonalLoan extends Loan {

    public double[][] interestRate = { { 3.75, 4.35, 4.95 },
                                        { 9, 10, 11 }
    };

    public double[][] getInterestRate() { return interestRate; }
}
