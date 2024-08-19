package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionAverageSumListener {

    @KafkaListener(topics = "${transaction.average.sum.topic}", groupId = "transaction-average-sum-group-id", containerFactory = "transactionAverageSumContainerFactory")
    public void transactionAverageSumEventListener(@Payload(required = false) final String batchData,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final String key) {

        log.info("KEY " + key);
        log.info("Received average sum event " + batchData);
    }
}
