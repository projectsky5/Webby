package com.projectsky.app;

import com.projectsky.aggregator.UserTransactionAggregator;
import com.projectsky.model.Transaction;
import com.projectsky.service.FileWriterService;
import com.projectsky.service.LogParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferLogApplicationTest {

    @Mock
    LogParser parser;

    @Mock
    UserTransactionAggregator aggregator;

    @Mock
    FileWriterService writer;

    @InjectMocks
    TransferLogApplication app;

    @Test
    void shouldCallAllDependencies(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("log1.log");
        Files.write(file, List.of("[2025-05-10 09:00:00] user001 balance inquiry 1000.00"));

        List<Transaction> parsed = List.of();
        Map<String, List<Transaction>> grouped = Map.of();

        when(parser.parse(file)).thenReturn(parsed);
        when(aggregator.groupByUser(parsed)).thenReturn(grouped);

        app.run(new String[]{tempDir.toString()});

        verify(parser).parse(file);
        verify(aggregator).groupByUser(parsed);
        verify(writer).writeUserLogs(grouped, tempDir);
    }

}