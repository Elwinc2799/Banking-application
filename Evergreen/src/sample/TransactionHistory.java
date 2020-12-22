package sample;

import java.time.LocalDate;

public class TransactionHistory {

    public double amount;

    public String transactionType;
    public String paymentRecipient;
    public String paymentType;

    public LocalDate transactionDate;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getPaymentRecipient() {
        return paymentRecipient;
    }

    public void setPaymentRecipient(String paymentRecipient) {
        this.paymentRecipient = paymentRecipient;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}
