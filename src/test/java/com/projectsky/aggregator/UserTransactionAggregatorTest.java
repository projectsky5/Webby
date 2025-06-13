package com.projectsky.aggregator;

import com.projectsky.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class UserTransactionAggregatorTest {

    private final UserTransactionAggregator aggregator = new UserTransactionAggregator();

    @Test
    @DisplayName("Должен сгруппировать транзакции для юзера")
    void shouldGroupTransactionsByUser() {
        List<Transaction> allTransactions = List.of(
                transaction("user001", "balance inquiry 1000.00", "1000.00", null),
                transaction("user001", "transferred 100.00 to user002", "100.00", "user002"),
                transaction("user002", "received 100.00 from user001", "100.00", "user001"),
                transaction("user002", "withdrew 50.00", "50.00", null)
        );

        Map<String, List<Transaction>> grouped = aggregator.groupByUser(allTransactions);

        assertThat(grouped).hasSize(2);

        assertThat(grouped.get("user001"))
                .hasSize(2)
                .extracting(Transaction::getOperation)
                .containsExactly("balance inquiry 1000.00", "transferred 100.00 to user002");

        assertThat(grouped.get("user002"))
                .hasSize(2)
                .extracting(Transaction::getOperation)
                .containsExactly("received 100.00 from user001", "withdrew 50.00");
    }

    private Transaction transaction(String user, String operation, String amount, String relatedUser) {
        return Transaction.builder()
                .timestamp(LocalDateTime.of(2025, 5, 10, 12, 0))
                .user(user)
                .operation(operation)
                .amount(new BigDecimal(amount))
                .relatedUser(Optional.ofNullable(relatedUser))
                .build();
    }
}