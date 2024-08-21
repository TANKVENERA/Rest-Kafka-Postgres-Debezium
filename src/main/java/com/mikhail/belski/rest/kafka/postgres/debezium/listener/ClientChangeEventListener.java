package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.Operation.DELETE;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ChangeEventMessageDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientEventDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.MessageKeyDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ClientChangeEventListener {
    private static final String CREATE_CLIENT_EVENT = "[Client: Client Id={}, Email={}, First Name={}, Last Name={} was created]";
    private static final String UPDATE_CLIENT_EVENT = "[Client: Client Id={}, Email={}, First Name={}, Last Name={} was updated]";
    private static final String DELETE_CLIENT_EVENT = "[Client: Client Id={}, Email={}, First Name={}, Last Name={} was deleted]";
    private static final String ERROR_MESSAGE = "Unsupported operation on Client: Client Id={}";

    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${client.change.event.topic}", groupId = "client-change-event-group-id", containerFactory = "clientChangeEventContainerFactory")
    public void listenClientChangeEvent(
            @Payload(required = false) final ChangeEventMessageDto<ClientEventDto> eventMessage,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final MessageKeyDto key) {

        if (eventMessage == null) {
            return;
        }
        final String clientId = key.getPayload().getClientId();
        final Operation operation = eventMessage.getPayload().getOp();
        final ClientEventDto beforeEvent = objectMapper.convertValue(eventMessage.getPayload().getBefore(), ClientEventDto.class);
        final ClientEventDto afterEvent = objectMapper.convertValue(eventMessage.getPayload().getAfter(), ClientEventDto.class);
        final ClientEventDto event = DELETE == operation ? beforeEvent : afterEvent;
        final String email = event.getEmail();
        final String firstName = event.getFirstName();
        final String lastName = event.getLastName();

        switch (operation) {
        case CREATE:
            log.info(CREATE_CLIENT_EVENT, clientId, email, firstName, lastName);
            break;

        case UPDATE:
            log.info(UPDATE_CLIENT_EVENT, clientId, email, firstName, lastName);
            break;

        case DELETE:
            log.info(DELETE_CLIENT_EVENT, clientId, email, firstName, lastName);
            break;

        default:
            log.error(ERROR_MESSAGE, clientId);
            break;
        }
    }

}
