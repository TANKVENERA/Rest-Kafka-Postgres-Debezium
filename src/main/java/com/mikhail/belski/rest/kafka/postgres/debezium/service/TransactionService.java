package com.mikhail.belski.rest.kafka.postgres.debezium.service;

import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;

public interface TransactionService {

    void publishTransaction(TransactionDto transaction);
}
