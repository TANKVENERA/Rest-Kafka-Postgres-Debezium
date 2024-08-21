package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FraudClientListener {

    private static final String FRAUD_CLIENT_EVENT = "[Fraud Client: Client Id={}, Email={}, Total Amount={} detected]";

    @KafkaListener(topics = "${fraud.topic}", groupId = "fraud-client-group-id", containerFactory = "fraudClientConsumerContainerFactory")
    public void listenFraudClient(@Payload final FraudClientDto fraudClient, @Header(KafkaHeaders.OFFSET) final Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int receivedPartitionId,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final String key) {

        log.warn(FRAUD_CLIENT_EVENT, fraudClient.getClientId(), fraudClient.getEmail(), fraudClient.getTotalAmount());
    }
}
