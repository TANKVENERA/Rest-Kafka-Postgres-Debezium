package com.mikhail.belski.rest.kafka.postgres.debezium.service;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private static final String SUCCESS_MESSAGE_TEMPLATE =
            "[Client: Client Id={}, Email={}, First Name={}, Last Name={}, Partition={} published]";
    private static final String FAILURE_MESSAGE_TEMPLATE = "[Failed to send Client message for Client Id={}. Error={}]";

    private KafkaTemplate<Long, Object> kafkaTemplate;
    private NewTopic clientTopic;

    @Override
    public void publishClient(final ClientDto client) {
        final Long clientId = client.getClientId();
        final ListenableFuture<SendResult<Long, Object>> sendResultFuture =
                kafkaTemplate.send(clientTopic.name(), 2, clientId, client);

        sendResultFuture.addCallback(
                success -> log.info(SUCCESS_MESSAGE_TEMPLATE, clientId, client.getEmail(),
                        client.getFirstName(), client.getLastName(), success == null ? null : success.getRecordMetadata().partition()),
                failure -> log.error(FAILURE_MESSAGE_TEMPLATE, clientId, failure.getMessage()));
    }
}
