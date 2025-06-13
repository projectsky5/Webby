package com.projectsky.service;

import com.projectsky.model.Transaction;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class BalanceCalculator {

    public BigDecimal calculateFinalBalance(List<Transaction> transactions) {
        if (transactions.isEmpty()) return BigDecimal.ZERO;

        // Находим последний balance inquiry по дате
        Transaction lastInquiry = transactions.stream()
                .filter(transaction -> transaction.getOperation().startsWith("balance inquiry"))
                .max(Comparator.comparing(Transaction::getTimestamp))
                .orElse(null);

        if (lastInquiry == null) {
            throw new IllegalStateException("Необходим balance inquiry для подсчета баланса.");
        }

        return getBigDecimal(transactions, lastInquiry);
    }

    private static BigDecimal getBigDecimal(List<Transaction> transactions, Transaction lastInquiry) {
        BigDecimal balance = lastInquiry.getAmount();

        // Применяем все операции после balance inquiry
        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp().isAfter(lastInquiry.getTimestamp())) {
                String operation = transaction.getOperation();

                if (operation.startsWith("transferred")) {
                    balance = balance.subtract(transaction.getAmount());
                } else if (operation.startsWith("withdrew")) {
                    balance = balance.subtract(transaction.getAmount());
                } else if (operation.startsWith("received")) {
                    balance = balance.add(transaction.getAmount());
                }
            }
        }
        return balance;
    }
}