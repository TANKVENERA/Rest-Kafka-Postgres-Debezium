package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

public enum TransactionType {
    INCOME,
    OUTCOME;

    public static TransactionType getType(String value) {
        for(TransactionType type : values()) {
            if(type.name().equalsIgnoreCase(value)) return type;
        }

        return null;
    }
}
