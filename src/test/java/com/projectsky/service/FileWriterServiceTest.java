package com.projectsky.service;

import com.projectsky.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FileWriterServiceTest {

    private final FileWriterService writerService = new FileWriterService();

    @Test
    @DisplayName("Должен записать отсортированные транзакции и финальный баланс в файл")
    void shouldWriteLogFileCorrectly(@TempDir Path tempDir) throws IOException {
        String user = "user001";
        List<Transaction> transactions = List.of(
                tx("2025-05-10T09:00:00", user, "balance inquiry 1000.00", "1000.00", null),
                tx("2025-05-10T10:00:00", user, "transferred 200.00 to user002", "200.00", "user002"),
                tx("2025-05-10T11:00:00", user, "received 300.00 from user003", "300.00", "user003")
        );

        writerService.writeUserLogs(Map.of(user, transactions), tempDir);

        Path logFile = tempDir.resolve("transactions_by_users").resolve(user + ".log");

        assertThat(Files.exists(logFile)).isTrue();

        List<String> lines = Files.readAllLines(logFile);
        assertThat(lines).hasSize(4);

        assertThat(lines.get(0)).contains("balance inquiry 1000.00");
        assertThat(lines.get(1)).contains("transferred 200.00 to user002");
        assertThat(lines.get(2)).contains("received 300.00 from user003");
        assertThat(lines.get(3)).matches("\\[.*\\] user001 final balance 1100\\.00");
    }

    @Test
    @DisplayName("Должен обрабатывать пустую мапу без ошибок")
    void shouldHandleEmptyMap(@TempDir Path tempDir) throws IOException {
        writerService.writeUserLogs(Map.of(), tempDir);
        Path outputDir = tempDir.resolve("transactions_by_users");

        assertThat(Files.exists(outputDir)).isTrue();
        assertThat(Files.list(outputDir)).isEmpty();
    }

    private Transaction tx(String timestamp, String user, String operation, String amount, String relatedUser) {
        return Transaction.builder()
                .timestamp(LocalDateTime.parse(timestamp))
                .user(user)
                .operation(operation)
                .amount(new BigDecimal(amount))
                .relatedUser(Optional.ofNullable(relatedUser))
                .build();
    }
}