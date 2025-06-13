package com.projectsky.service;

import com.projectsky.model.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private static final Pattern LOG_LINE = Pattern.compile("^\\[(.*?)\\] (\\w+) (.+)$");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Transaction> parse(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Transaction> transactions = new ArrayList<>();

        for(String line : lines) {
            Matcher matcher = LOG_LINE.matcher(line);
            if(!matcher.matches()) {
                System.err.println("Пропущена строка (неправильный формат): " + line);
                continue;
            }

            LocalDateTime timestamp;
            try {
                timestamp = parseTimestamp(matcher.group(1));
            } catch (Exception e) {
                System.err.println("Пропущена строка (неправильная дата): " + matcher.group(1));
                continue;
            }
            String user = matcher.group(2);
            String operationText = matcher.group(3);

            transactions.addAll(parseOperation(timestamp, user, operationText));
        }

        return transactions;
    }

    private List<Transaction> parseOperation(LocalDateTime timestamp, String user, String text) {
        String operationKey = text.split(" ")[0];

        switch(operationKey) {
            case "balance":
                return parseBalance(timestamp, user, text);
            case "transferred":
                return parseTransfer(timestamp, user, text);
            case "withdrew":
                return parseWithdraw(timestamp, user, text);
            default:
                System.err.println("Неизвестная операция: " + text);
                return Collections.emptyList();
        }
    }

    private List<Transaction> parseWithdraw(LocalDateTime timestamp, String user, String text) {
        BigDecimal amount = new BigDecimal(text.split(" ")[1]);
        return Collections.singletonList(Transaction.builder()
                        .timestamp(timestamp)
                        .user(user)
                        .operation(text)
                        .amount(amount)
                        .relatedUser(Optional.empty())
                .build());
    }

    private List<Transaction> parseTransfer(LocalDateTime timestamp, String user, String text) {
        String[] parts = text.split(" ");
        BigDecimal amount = new BigDecimal(parts[1]);
        String toUser = parts[3];

        Transaction original = Transaction.builder()
                .timestamp(timestamp)
                .user(user)
                .operation(text)
                .amount(amount)
                .relatedUser(Optional.of(toUser))
                .build();

        Transaction mirror = Transaction.builder()
                .timestamp(timestamp)
                .user(toUser)
                .operation("received " + amount + " from " + user)
                .amount(amount)
                .relatedUser(Optional.of(user))
                .build();

        return Arrays.asList(original, mirror);
    }

    private List<Transaction> parseBalance(LocalDateTime timestamp, String user, String text) {
        BigDecimal amount = new BigDecimal(text.split(" ")[2]);
        return Collections.singletonList(Transaction.builder()
                        .timestamp(timestamp)
                        .user(user)
                        .operation(text)
                        .amount(amount)
                        .relatedUser(Optional.empty())
                .build());
    }

    private LocalDateTime parseTimestamp(String str) {
        return LocalDateTime.parse(str, FORMATTER);
    }

}
