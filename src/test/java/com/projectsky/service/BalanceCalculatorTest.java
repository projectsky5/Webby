package com.projectsky.service;

import com.projectsky.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class BalanceCalculatorTest {

    private final BalanceCalculator calculator = new BalanceCalculator();

    @Test
    @DisplayName("Должен сосчитать итоговый баланс на основе последнего balance inquiry")
    void shouldCalculateFinalBalance() {
        List<Transaction> transactions = List.of(
                tx("2025-05-10T09:00:00", "user001", "balance inquiry 1000.00", "1000.00", null),
                tx("2025-05-10T10:00:00", "user001", "transferred 100.00 to user002", "100.00", "user002"),
                tx("2025-05-10T11:00:00", "user001", "withdrew 50.00", "50.00", null),
                tx("2025-05-10T12:00:00", "user001", "received 300.00 from user003", "300.00", "user003")
        );

        BigDecimal result = calculator.calculateFinalBalance(transactions);

        assertThat(result).isEqualByComparingTo("1150.00");
    }

    @Test
    @DisplayName("Должен вернуть 0 если транзакций нет")
    void shouldReturnZeroForEmptyList() {
        BigDecimal result = calculator.calculateFinalBalance(List.of());
        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Должен пробросить исключение если balance inquiry не существует")
    void shouldThrowIfNoBalanceInquiry() {
        List<Transaction> transactions = List.of(
                tx("2025-05-10T10:00:00", "user001", "transferred 100.00 to user002", "100.00", "user002")
        );

        assertThatThrownBy(() -> calculator.calculateFinalBalance(transactions))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Необходим balance inquiry для подсчета баланса.");
    }

    private Transaction tx(String timestamp, String user, String operation,
                           String amount, String relatedUser) {
        return Transaction.builder()
                .timestamp(LocalDateTime.parse(timestamp))
                .user(user)
                .operation(operation)
                .amount(new BigDecimal(amount))
                .relatedUser(Optional.ofNullable(relatedUser))
                .build();
    }
}