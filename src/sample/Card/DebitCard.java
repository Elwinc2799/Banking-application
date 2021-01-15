package sample.Card;

import sample.Account.SavingsAccount;
import sample.ReadFile;

public class DebitCard extends Card {

    public double cashbackRate = 1.5;

    public double getCashbackRate() { return cashbackRate; }

    public void debitPayment(int amount) {
        amount += amount * (getCashbackRate() / 100);
        ReadFile.DataStorage.savingsAccount.savingsAccountPayment(amount);
    }

    public void debitPaymentValidation(int amount, SavingsAccount savingsAccount) {
        savingsAccount.savingsPaymentValidation(amount);
    }
}
