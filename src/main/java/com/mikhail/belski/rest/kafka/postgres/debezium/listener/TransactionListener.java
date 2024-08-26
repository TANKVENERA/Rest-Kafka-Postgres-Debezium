package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.repository.TransactionRepository;
import com.mikhail.belski.rest.kafka.postgres.debezium.transformer.TransactionTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionListener {

    private static final String CONSUME_TRANSACTION_LOG_INFO_TEMPLATE =
            "[Transaction: Client Id={}, Type={}, Price={} consumed]";

    private TransactionRepository transactionRepository;
    private TransactionTransformer transactionTransformer;

    @KafkaListener(topics = "${transaction.topic}", groupId = "transaction-group-id", containerFactory = "transactionConsumerContainerFactory")
    public void listenTransaction(@Payload final TransactionDto transaction) {

        transactionRepository.save(transactionTransformer.transform(transaction));

        log.info(CONSUME_TRANSACTION_LOG_INFO_TEMPLATE, transaction.getClientId(), transaction.getTransactionType(),
                transaction.getPrice());
    }
}
