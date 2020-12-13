package sample;

public class DebitCard extends Card {

    public double cashbackRate = 1.5;
    public String savingsID;

    public String getSavingsID() { return savingsID; }

    public void setSavingsID(String savingsID) { this.savingsID = savingsID; }

    public double debitPayment(int amount, SavingsAccount savingsAccount) {
        return savingsAccount.getBalance() - amount * (1 + 1.5 / 100);
    }

    public void debitPaymentValidation(int amount, SavingsAccount savingsAccount) {
        savingsAccount.savingsPaymentValidation(amount);
    }
}
