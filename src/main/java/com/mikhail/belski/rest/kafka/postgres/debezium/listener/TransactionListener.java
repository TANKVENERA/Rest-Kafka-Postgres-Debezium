package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import static org.springframework.kafka.retrytopic.DltStrategy.FAIL_ON_ERROR;
import static org.springframework.kafka.support.KafkaHeaders.EXCEPTION_MESSAGE;
import static org.springframework.kafka.support.KafkaHeaders.ORIGINAL_TOPIC;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Header;
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

    @RetryableTopic(attempts = "1", kafkaTemplate = "producerTemplate", dltStrategy = FAIL_ON_ERROR, dltTopicSuffix = "${dead.letter.queue.suffix}")
    @KafkaListener(topics = "${transaction.topic}", groupId = "transaction-group-id", containerFactory = "transactionConsumerContainerFactory")
    public void listenTransaction(@Payload final TransactionDto transaction) {
        transactionRepository.save(transactionTransformer.transform(transaction));

        log.info(CONSUME_TRANSACTION_LOG_INFO_TEMPLATE, transaction.getClientId(), transaction.getTransactionType(),
                transaction.getPrice());
    }

    @DltHandler
    public void handleTransactionFailure(@Payload TransactionDto transaction, @Header(ORIGINAL_TOPIC) String originalTopic,
            @Header(EXCEPTION_MESSAGE) String exceptionMessage) {
        log.warn("Transaction: payload={} failed to be consumed from topic: topic={}\n"
                + "Error message={}", transaction, originalTopic, exceptionMessage);
    }
}
