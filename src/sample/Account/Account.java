package sample.Account;

import java.time.LocalDate;

public abstract class Account {

    public String accountNum;
    public String name;
    public String email;

    public double balance;

    public boolean balanceUpdateStatus;

    public LocalDate accountDateOpen;

    public String getAccountNum() { return accountNum; }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }

    public LocalDate getAccountDateOpen() { return accountDateOpen; }

    public void setAccountDateOpen(LocalDate accountDateOpen) { this.accountDateOpen = accountDateOpen; }

    public boolean isBalanceUpdateStatus() { return balanceUpdateStatus; }

    public void setBalanceUpdateStatus(boolean balanceUpdateStatus) { this.balanceUpdateStatus = balanceUpdateStatus; }

    public abstract void updateBalance();
}
