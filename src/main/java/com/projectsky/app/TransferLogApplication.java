package com.projectsky.app;

import com.projectsky.aggregator.UserTransactionAggregator;
import com.projectsky.model.Transaction;
import com.projectsky.service.LogParser;
import com.projectsky.service.FileWriterService;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TransferLogApplication {

    private final LogParser parser;
    private final UserTransactionAggregator aggregator;
    private final FileWriterService writer;

    public TransferLogApplication(LogParser parser, UserTransactionAggregator aggregator, FileWriterService writer) {
        this.parser = parser;
        this.aggregator = aggregator;
        this.writer = writer;
    }

    public void run(String[] args) {
        if (args.length == 0) {
            System.err.println("Укажите путь к директории с логами.");
            return;
        }

        Path inputDir = Paths.get(args[0]);
        if (!Files.isDirectory(inputDir)) {
            System.err.println("Указанный путь не является директорией: " + inputDir);
            return;
        }

        try {
            List<Transaction> allTransactions = readAllTransactions(inputDir);
            Map<String, List<Transaction>> grouped = aggregator.groupByUser(allTransactions);
            writer.writeUserLogs(grouped, inputDir);

            System.out.println("Обработка завершена. Файлы сохранены в: " + inputDir.resolve("transactions_by_users"));

        } catch (IOException e) {
            System.err.println("Ошибка при обработке логов: " + e.getMessage());
        }
    }

    private List<Transaction> readAllTransactions(Path dir) throws IOException {
        List<Transaction> result = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.log")) {
            for (Path file : stream) {
                result.addAll(parser.parse(file));
            }
        }

        return result;
    }
}