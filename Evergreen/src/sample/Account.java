package sample;

public abstract class Account {

    public int dateOpen;

    public String accountNum;
    public String name;
    public String email;

    public double balance;

    public int getDateOpen() { return dateOpen; }

    public void setDateOpen(int dateOpen) { this.dateOpen = dateOpen; }

    public String getAccountNum() { return accountNum; }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public double getBalance() { return balance; }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public abstract void updateBalance();
}
