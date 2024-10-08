package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FraudClientListener {

    private static final String FRAUD_CLIENT_EVENT_TEMPLATE = "[Fraud Client: Client Id={}, Email={}, Total Amount={} detected]";

    @KafkaListener(topics = "${fraud.topic}", groupId = "fraud-client-group-id", containerFactory = "fraudClientConsumerContainerFactory")
    public void listenFraudClient(@Payload final FraudClientDto fraudClient) {
        log.warn(FRAUD_CLIENT_EVENT_TEMPLATE, fraudClient.getClientId(), fraudClient.getEmail(), fraudClient.getTotalAmount());
    }
}
