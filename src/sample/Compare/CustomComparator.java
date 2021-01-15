package sample.Compare;

import sample.TransactionHistory;

import java.util.Comparator;

public class CustomComparator implements Comparator<TransactionHistory> {

    @Override
    public int compare(TransactionHistory transactionHistory, TransactionHistory history) {
        return transactionHistory.getTransactionDate().compareTo(history.getTransactionDate());
    }
}
