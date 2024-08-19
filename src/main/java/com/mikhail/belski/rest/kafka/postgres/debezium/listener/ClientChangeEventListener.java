package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation.CREATE;
import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation.DELETE;
import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation.UPDATE;

import java.util.LinkedHashMap;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.DebeziumMessageDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.MessageKeyDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ClientChangeEventListener {
    private static final String EMAIL_FIELD = "email";
    private static final String CREATE_CLIENT_EVENT = "[Client: Client Id={}, Email={} was created]";
    private static final String UPDATE_CLIENT_EVENT = "[Client: Client Id={}, Email={} was updated]";
    private static final String DELETE_CLIENT_EVENT = "[Client: Client Id={}, Email={} was deleted]";
    private static final String ERROR_MESSAGE = "Unsupported operation on Client: Client Id={}";

    @KafkaListener(topics = "${client.change.event.topic}", groupId = "client-change-event-group-id", containerFactory = "clientChangeEventContainerFactory")
    public void clientChangeEventListener(@Payload(required = false) final DebeziumMessageDto eventMessage,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final MessageKeyDto key) {

        if (eventMessage == null) {
            return;
        }
        final String clientId = key.getPayload().getClientId();
        final Operation operation = eventMessage.getPayload().getOp();

        if (DELETE == operation) {
            final LinkedHashMap<String, ?> beforeFields = eventMessage.getPayload().getBefore();
            final String email = (String) beforeFields.get(EMAIL_FIELD);

            log.info(DELETE_CLIENT_EVENT, clientId, email);
        }
        else if (CREATE == operation || UPDATE == operation) {
            final LinkedHashMap<String, ?> afterFields = eventMessage.getPayload().getAfter();
            final String email = (String) afterFields.get(EMAIL_FIELD);

            if (CREATE == operation) {
                log.info(CREATE_CLIENT_EVENT, clientId, email);
            }
            else {
                log.info(UPDATE_CLIENT_EVENT, clientId, email);
            }
        } else {
            log.error(ERROR_MESSAGE, clientId);
        }
    }

}
