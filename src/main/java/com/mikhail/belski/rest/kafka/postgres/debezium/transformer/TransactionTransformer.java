package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.TransactionEntity;

public interface TransactionTransformer{

    TransactionEntity transform(TransactionDto transactionDto);
}
