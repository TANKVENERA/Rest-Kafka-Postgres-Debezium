package com.mikhail.belski.rest.kafka.postgres.debezium.service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final String PUBLISH_TRANSACTION_LOG_INFO_TEMPLATE =
            "[Transaction for client={} was published to partition={}]";

    private KafkaTemplate<String, TransactionDto> transactionProducerTemplate;
    private NewTopic transactionTopic;

    @Override
    public void publishTransaction(final TransactionDto transaction) {
        final LocalDateTime now = LocalDateTime.now();
        transaction.setCreatedAt(now);

        final Long clientId = transaction.getClientId();
        final ListenableFuture<SendResult<String, TransactionDto>> sendResultFuture =
                transactionProducerTemplate.send(transactionTopic.name(), String.valueOf(clientId), transaction);

        try {
            final RecordMetadata recordMetadata = sendResultFuture.get().getRecordMetadata();

            log.info(PUBLISH_TRANSACTION_LOG_INFO_TEMPLATE, clientId, recordMetadata.partition());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }
    }
}
