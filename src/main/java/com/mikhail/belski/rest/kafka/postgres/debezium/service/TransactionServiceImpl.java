package com.mikhail.belski.rest.kafka.postgres.debezium.service;

import static java.time.LocalDateTime.now;

import java.time.Clock;
import org.apache.kafka.clients.admin.NewTopic;
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
    private static final String SUCCESS_MESSAGE_TEMPLATE = "[Transaction: Client Id={}, Price={}, Created={}, Partition={} published]";
    private static final String FAILURE_MESSAGE_TEMPLATE = "[Failed to send Transaction message for Client Id={}. Error={}]";

    private KafkaTemplate<Long, Object> kafkaTemplate;
    private NewTopic transactionTopic;
    private Clock clock;

    @Override
    public void publishTransaction(final TransactionDto transaction) {
        transaction.setCreatedAt(now(clock));

        final Long clientId = transaction.getClientId();
        final ListenableFuture<SendResult<Long, Object>> sendResultFuture =
                kafkaTemplate.send(transactionTopic.name(), 2, clientId, transaction);

        sendResultFuture.addCallback(success -> log.info(SUCCESS_MESSAGE_TEMPLATE, clientId, transaction.getPrice(),
                        transaction.getCreatedAt(), success == null ? null : success.getRecordMetadata().partition()),
                failure -> log.error(FAILURE_MESSAGE_TEMPLATE, clientId, failure.getMessage()));
    }
}
