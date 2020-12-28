package sample.Loan;

public class BusinessLoan extends Loan {

    public String collateralType;

    public double collateralAmount;
    public double[][] interestRate = { { 8.45, 9.35, 10.15 },
                                        { 9.55, 10.65, 12.45 }
    };

    public double[][] getInterestRate() { return interestRate; }

    public String getCollateralType() { return collateralType; }

    public void setCollateralType(String collateralType) { this.collateralType = collateralType; }

    public double getCollateralAmount() { return collateralAmount; }

    public void setCollateralAmount(double collateralAmount) { this.collateralAmount = collateralAmount; }
}
