package com.projectsky.service;

import com.projectsky.model.Transaction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileWriterService {

    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final BalanceCalculator calculator = new BalanceCalculator();

    public void writeUserLogs(Map<String, List<Transaction>> groupedTransactions, Path rootDir) throws IOException {
        Path outputDir = rootDir.resolve("transactions_by_users");

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        for (Map.Entry<String, List<Transaction>> entry : groupedTransactions.entrySet()) {
            String user = entry.getKey();
            List<Transaction> transactions = new ArrayList<>(entry.getValue());
            transactions.sort(Comparator.naturalOrder());

            Path userFile = outputDir.resolve(user + ".log");

            try (BufferedWriter writer = Files.newBufferedWriter(userFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Transaction transaction : transactions) {
                    writer.write(transaction.toString());
                    writer.newLine();
                }

                BigDecimal finalBalance = calculator.calculateFinalBalance(transactions);

                String finalLine = String.format(Locale.US, "[%s] %s final balance %.2f",
                        OUTPUT_FORMAT.format(LocalDateTime.now()),
                        user,
                        finalBalance);

                writer.write(finalLine);
                writer.newLine();
            }
        }
    }
}