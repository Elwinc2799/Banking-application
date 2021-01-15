package sample.Compare;

import sample.TransactionHistory;

import java.util.Comparator;

public class CustomComparatorHistoryLatest implements Comparator<TransactionHistory> {

    @Override
    public int compare(TransactionHistory transactionHistory, TransactionHistory history) {
        return history.getTransactionDate().compareTo(transactionHistory.getTransactionDate());
    }
}
