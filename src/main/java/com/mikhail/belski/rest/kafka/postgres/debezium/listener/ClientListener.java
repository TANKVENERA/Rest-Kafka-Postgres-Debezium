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
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.repository.ClientRepository;
import com.mikhail.belski.rest.kafka.postgres.debezium.transformer.ClientTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ClientListener {

    private static final String CONSUME_CLIENT_LOG_INFO_TEMPLATE = "[Client: Client Id={}, Email={} consumed]";

    private ClientRepository clientRepository;
    private ClientTransformer clientTransformer;

    @RetryableTopic(attempts = "1", kafkaTemplate = "producerTemplate", dltStrategy = FAIL_ON_ERROR, dltTopicSuffix = "${dead.letter.queue.suffix}")
    @KafkaListener(topics = "${client.topic}", groupId = "client-group-id", containerFactory = "clientConsumerContainerFactory")
    public void listenClient(@Payload final ClientDto client) {
        clientRepository.save(clientTransformer.transformToClientEntity(client));

        log.info(CONSUME_CLIENT_LOG_INFO_TEMPLATE, client.getClientId(), client.getEmail());
    }

    @DltHandler
    public void handleClientFailure(@Payload ClientDto client, @Header(ORIGINAL_TOPIC) String originalTopic,
            @Header(EXCEPTION_MESSAGE) String exceptionMessage) {

        log.warn("Client: payload={} failed to be consumed from topic: topic={}\n"
                + "Error message={}", client, originalTopic, exceptionMessage);
    }

}
