package com.projectsky;

import com.projectsky.aggregator.UserTransactionAggregator;
import com.projectsky.app.TransferLogApplication;
import com.projectsky.service.FileWriterService;
import com.projectsky.service.LogParser;
import lombok.extern.java.Log;

public class ApplicationRunner {

    public static void main(String[] args) {
        TransferLogApplication app = new TransferLogApplication(
                new LogParser(),
                new UserTransactionAggregator(),
                new FileWriterService()
        );

        app.run(args);
    }
}
