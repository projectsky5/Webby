package com.projectsky.aggregator;

import com.projectsky.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTransactionAggregator {

    public Map<String, List<Transaction>> groupByUser(List<Transaction> transactions) {
        Map<String, List<Transaction>> result = new HashMap<>();

        for(Transaction transaction : transactions) {
            result.computeIfAbsent(transaction.getUser(), k -> new ArrayList<>()).add(transaction);
        }

        return result;
    }
}
