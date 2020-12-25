package sample;

import javafx.concurrent.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;


public class ReadFile {
    public static String accPassword = "2799!!epokemon";
    public static HashMap<String, String> passwordMap = new HashMap<>();
    static Semaphore semConsumer = new Semaphore(0);
    static Semaphore semProducer = new Semaphore(1);
    static Connection connect;
    static Statement statement;

    public static void get() {
        try {
            semConsumer.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }

        semProducer.release();
    }

    public static void put() {
        try {
            semProducer.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }

        new Thread(savingsAccountInfoTask).start();
        new Thread(depositsAccountInfoTask).start();
        new Thread(creditCardInfoTask).start();
        new Thread(loanInfoTask).start();
        new Thread(transactionHistoryInfoTask).start();

        semConsumer.release();
    }

    static Task<Void> task = new Task<>() {
        @Override
        protected Void call() {
            CurrencyWebScrapping.webScrapping();
            return null;
        }
    };

    static Task<Void> passwordValidationTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "SYSTEM", accPassword);
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM LOGIN");
                while (resultSet.next()) {
                    String username = resultSet.getString(1);
                    String userPassword = resultSet.getString(2);

                    passwordMap.put(username, userPassword);
                }
            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    static Task<Void> savingsAccountInfoTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM ACCOUNT WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() +"'");
                while (resultSet.next()) {
                    DataStorage.savingsAccount.setAccountNum(Long.toString(resultSet.getLong(2)));
                    DataStorage.savingsAccount.setName(resultSet.getString(3));
                    DataStorage.savingsAccount.setEmail(resultSet.getString(4));
                    DataStorage.savingsAccount.setAccountDateOpen(resultSet.getDate(6).toLocalDate());
                    DataStorage.savingsAccount.setBalance(resultSet.getDouble(7));
                    DataStorage.savingsAccount.setBalanceUpdateStatus(resultSet.getString(8).equals("Y"));
                    DataStorage.savingsAccount.setDailyLimit(resultSet.getDouble(9));
                    DataStorage.savingsAccount.setMonthExpenditure(resultSet.getDouble(10));
                }

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            try {
                Class.forName("oracle.jdbc.OracleDriver");
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM SAVINGS_EXPENSE WHERE ACCOUNT_ID = '" + DataStorage.savingsAccount.getAccountNum() + "'");
                while (resultSet.next()) {
                    double[] tempBalanceRecorder = new double[7];

                    for (int i = 0, j = 6; i < 7; i++, j--)
                        tempBalanceRecorder[j] = resultSet.getDouble(i + 2);

                    DataStorage.savingsAccount.setBalanceRecorder(tempBalanceRecorder);
                }

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            new Thread(DataStorage.savingsAccount.updateExpenditureRecorderTask).start();
            DataStorage.savingsAccount.updateBalance();

            return null;
        }
    };

    static Task<Void> depositsAccountInfoTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM DEPOSITS WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                while (resultSet.next()) {
                    DataStorage.depositAccount.setAccountNum(Long.toString(resultSet.getLong(2)));
                    DataStorage.depositAccount.setName(resultSet.getString(3));
                    DataStorage.depositAccount.setEmail(resultSet.getString(4));
                    DataStorage.depositAccount.setAccountDateOpen(resultSet.getDate(6).toLocalDate());
                    DataStorage.depositAccount.setBalance(resultSet.getDouble(7));
                    DataStorage.depositAccount.setBalanceUpdateStatus(resultSet.getString(8).equals("Y"));
                }

                if (DataStorage.depositAccount.renewal())
                    DataStorage.depositAccount.updateBalance();

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    static Task<Void> creditCardInfoTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM CREDITCARD  WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                while (resultSet.next()) {
                    DataStorage.creditCard.setCardID(resultSet.getString(2));
                    DataStorage.creditCard.setCvv(resultSet.getString(3));
                    LocalDate expDate = resultSet.getDate(4).toLocalDate();
                    DataStorage.creditCard.setExpiryDate(expDate.getMonthValue() + "/" + Integer.toString(expDate.getYear()).substring(2));
                    DataStorage.creditCard.setFixedMonthlyLimit(resultSet.getDouble(5));
                    DataStorage.creditCard.setOutstandingBalance(resultSet.getDouble(6));
                    DataStorage.creditCard.setExpenditure(resultSet.getDouble(7));
                    DataStorage.creditCard.setOutstandingBalanceStatusUpdated(resultSet.getString(8).equals("Y"));
                }

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            DataStorage.creditCard.updateOutstandingBalance();

            return null;
        }
    };

    static Task<Void> loanInfoTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM LOAN WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                while (resultSet.next()) {
                    DataStorage.isPersonalLoan = resultSet.getString(3).equals("Personal");
                    DataStorage.loan = true;

                    if (DataStorage.isPersonalLoan) {
                        DataStorage.loanID = resultSet.getString(2);
                        DataStorage.personalLoan.setLoanID(resultSet.getString(2));
                        DataStorage.personalLoan.setLoanType(resultSet.getString(3));
                        DataStorage.personalLoan.setInitialLoanAmount(resultSet.getDouble(4));
                        DataStorage.personalLoan.setMonthlyRepayment(resultSet.getDouble(5));
                        DataStorage.personalLoan.setOutstandingBalance(resultSet.getDouble(6));
                        DataStorage.personalLoan.setLastDatePaid(resultSet.getDate(7).toLocalDate());
                        DataStorage.personalLoan.setLoanOutstandingBalanceUpdated(resultSet.getString(8).equals("Y"));
                        DataStorage.personalLoan.updateOutstandingBalance(ReadFile.DataStorage.personalLoan.getInterestRate());
                        DataStorage.personalLoan.setLoanDuration(DataStorage.personalLoan.approximatedDate(ReadFile.DataStorage.personalLoan.getInterestRate()));
                    } else {
                        DataStorage.loanID = resultSet.getString(2);
                        DataStorage.businessLoan.setLoanID(resultSet.getString(2));
                        DataStorage.businessLoan.setLoanType(resultSet.getString(3));
                        DataStorage.businessLoan.setInitialLoanAmount(resultSet.getDouble(4));
                        DataStorage.businessLoan.setMonthlyRepayment(resultSet.getDouble(5));
                        DataStorage.businessLoan.setOutstandingBalance(resultSet.getDouble(6));
                        DataStorage.businessLoan.setLastDatePaid(resultSet.getDate(7).toLocalDate());
                        DataStorage.businessLoan.setLoanOutstandingBalanceUpdated(resultSet.getString(8).equals("Y"));
                        DataStorage.businessLoan.updateOutstandingBalance(ReadFile.DataStorage.businessLoan.getInterestRate());
                        DataStorage.businessLoan.setLoanDuration(DataStorage.businessLoan.approximatedDate(ReadFile.DataStorage.businessLoan.getInterestRate()));
                    }
                }

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            new Thread(loanRepaymentHistoryTask).start();

            if (!DataStorage.isPersonalLoan) {
                try {
                    Class.forName("oracle.jdbc.OracleDriver");
                    statement = connect.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM BUSINESS_COLLATERAL WHERE LOAN_ID = '" + DataStorage.businessLoan.loanID + "'");

                    while (resultSet.next()) {
                        DataStorage.businessLoan.setCollateralType(resultSet.getString(2));
                        DataStorage.businessLoan.setCollateralAmount(resultSet.getDouble(3));
                    }

                } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }
            }

            return null;
        }
    };

    static Task<Void> transactionHistoryInfoTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM TRANSACTION_HISTORY WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
                while (resultSet.next()) {
                    TransactionHistory transactionHistory = new TransactionHistory();

                    transactionHistory.setTransactionDate(resultSet.getDate(2).toLocalDate());
                    transactionHistory.setAmount(resultSet.getDouble(3));
                    transactionHistory.setPaymentRecipient(resultSet.getString(4));
                    transactionHistory.setTransactionType(resultSet.getString(5));
                    transactionHistory.setPaymentType(resultSet.getString(6));

                    DataStorage.transactionHistoryArrayList.add(transactionHistory);
                }

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    static Task<Void> loanRepaymentHistoryTask = new Task<>() {
        @Override
        protected Void call() {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                statement = connect.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM LOAN_REPAYMENT_HISTORY WHERE LOAN_ID = '" + DataStorage.loanID + "'");
                while (resultSet.next()) {
                    TransactionHistory transactionHistory = new TransactionHistory();

                    transactionHistory.setTransactionDate(resultSet.getDate(2).toLocalDate());
                    transactionHistory.setAmount(resultSet.getDouble(3));

                    DataStorage.loanRepaymentHistoryArrayList.add(transactionHistory);
                }

            } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

            return null;
        }
    };

    public static void importData() {
        new Consumer();
        new Producer();
        new Thread(task).start();

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            statement = connect.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM DEBITCARD WHERE USERNAME = '" + ReadFile.DataStorage.getUsername() + "'");
            while (resultSet.next()) {
                DataStorage.debitCard.setCardID(resultSet.getString(2));
                DataStorage.debitCard.setCvv(resultSet.getString(3));
                LocalDate expDate = resultSet.getDate(4).toLocalDate();
                DataStorage.debitCard.setExpiryDate(expDate.getMonthValue() + "/" + Integer.toString(expDate.getYear()).substring(2));
            }

        } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            statement = connect.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM CREDITCARD_EXPENSES WHERE CARD_ID = '" + DataStorage.creditCard.getCardID() + "'");
            while (resultSet.next()) {
                double[] tempBalanceRecorder = new double[7];

                for (int i = 0, j = 6; i < 7; i++, j--)
                    tempBalanceRecorder[j] = resultSet.getDouble(i + 2);

                DataStorage.creditCard.setBalanceRecorder(tempBalanceRecorder);
            }
        } catch (SQLException | ClassNotFoundException e) { e.printStackTrace(); }

        new Thread(DataStorage.creditCard.updateExpenditureRecorderTask).start();
    }

    public static class DataStorage {

        public static String username;

        public static SavingsAccount savingsAccount = new SavingsAccount();
        public static DepositAccount depositAccount = new DepositAccount();
        public static CreditCard creditCard = new CreditCard();
        public static DebitCard debitCard = new DebitCard();
        public static PersonalLoan personalLoan = new PersonalLoan();
        public static BusinessLoan businessLoan = new BusinessLoan();
        public static ArrayList<TransactionHistory> transactionHistoryArrayList = new ArrayList<>();
        public static ArrayList<TransactionHistory> loanRepaymentHistoryArrayList = new ArrayList<>();
        public static HashMap<String, ArrayList<Double>> currencyMap = new HashMap<>();
        public static boolean loan;
        public static boolean isPersonalLoan;
        public static String loanID;

        public static String getUsername() {
            return username;
        }

        public static void setUsername(String username) {
            DataStorage.username = username;
        }
    }
}
