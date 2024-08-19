package com.mikhail.belski.rest.kafka.postgres.debezium.repository;

import com.mikhail.belski.rest.kafka.postgres.debezium.entity.TransactionEntity;

public interface CustomTransactionRepository {

    void saveIfClientExist(TransactionEntity transactionEntity);
}
