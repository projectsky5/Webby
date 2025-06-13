package com.projectsky.service;

import com.projectsky.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LogParserTest {

    private final LogParser parser = new LogParser();

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("prepareLogLines")
    @DisplayName("Должен спарсить правильно")
    void shouldParseTransactionCorrectly(String rawLine, Transaction expected) throws IOException {
        Path file = Files.createTempFile("log", ".log");
        Files.write(file, List.of(rawLine));

        List<Transaction> result = parser.parse(file);

        assertThat(result).containsExactly(expected);
    }

    static Stream<Arguments> prepareLogLines() {
        return Stream.of(
                Arguments.of(
                        "[2025-05-10 09:00:22] user001 balance inquiry 1000.00",
                        Transaction.builder()
                                .timestamp(LocalDateTime.of(2025, 5, 10, 9, 0, 22))
                                .user("user001")
                                .operation("balance inquiry 1000.00")
                                .amount(new BigDecimal("1000.00"))
                                .relatedUser(Optional.empty())
                                .build()
                ),
                Arguments.of(
                        "[2025-05-10 10:00:31] user002 withdrew 50.00",
                        Transaction.builder()
                                .timestamp(LocalDateTime.of(2025, 5, 10, 10, 0, 31))
                                .user("user002")
                                .operation("withdrew 50.00")
                                .amount(new BigDecimal("50.00"))
                                .relatedUser(Optional.empty())
                                .build()
                )
        );
    }

    @Test
    @DisplayName("Должен пропустить неправильные строки")
    void shouldSkipInvalidLines() throws IOException {
        Path file = Files.createTempFile("invalid-log", ".log");
        Files.write(file, List.of(
                "invalid log line without brackets",
                "[2025-05-10 09:00:22] user001 something invalid",
                "[invalid timestamp] user001 balance inquiry 1000.00"
        ));

        List<Transaction> result = parser.parse(file);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should Handle Minimal Log File")
    void shouldHandleMinimalLogFile() throws IOException {
        Path file = Files.createTempFile("minimal-log", ".log");
        Files.write(file, List.of(
                "[2025-01-01 00:00:00] user999 balance inquiry 1.00"
        ));

        List<Transaction> result = parser.parse(file);

        assertThat(result).hasSize(1);
        var transaction = result.getFirst();
        assertThat(transaction.getUser()).isEqualTo("user999");
        assertThat(transaction.getAmount()).isEqualByComparingTo("1.00");
        assertThat(transaction.getRelatedUser()).isEmpty();
    }
}