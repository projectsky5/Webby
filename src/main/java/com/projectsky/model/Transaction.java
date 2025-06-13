package com.projectsky.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Value
@Builder
public class Transaction implements Comparable<Transaction> {

    LocalDateTime timestamp;
    String user;
    String operation;
    BigDecimal amount;
    Optional<String> relatedUser;

    @Override
    public int compareTo(Transaction o) {
        return this.timestamp.compareTo(o.timestamp);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s", timestamp, user, operation);
    }
}
