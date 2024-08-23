package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.Operation.DELETE;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ChangeEventMessageDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.MessageKeyDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.Operation;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionEventDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionChangeEventListener {
    private static final String CREATE_TRANSACTION_EVENT_TEMPLATE = "[Transaction: Client Id={}, Type={}, Transaction Amount={} was created]";
    private static final String UPDATE_TRANSACTION_EVENT_TEMPLATE = "[Transaction: Client Id={}, Type={}, Transaction Amount={} was updated]";
    private static final String DELETE_TRANSACTION_EVENT_TEMPLATE = "[Transaction: Client Id={}, Type={}, Transaction Amount Price={} was deleted]";
    private static final String ERROR_MESSAGE_TEMPLATE = "Unsupported operation on Transaction: Client Id={}";

    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${transaction.change.event.topic}", groupId = "transaction-change-event-group-id", containerFactory = "transactionChangeEventContainerFactory")
    public void listenTransactionChangeEvent(@Payload(required = false) final ChangeEventMessageDto<TransactionEventDto> eventMessage,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final MessageKeyDto key) {

        if (eventMessage == null) {
            return;
        }
        final String clientId = key.getPayload().getClientId();
        final Operation operation = eventMessage.getPayload().getOp();
        final TransactionEventDto beforeEvent = objectMapper.convertValue(eventMessage.getPayload().getBefore(), TransactionEventDto.class);
        final TransactionEventDto afterEvent = objectMapper.convertValue(eventMessage.getPayload().getAfter(), TransactionEventDto.class);
        final TransactionEventDto event = DELETE == operation ? beforeEvent : afterEvent;
        final TransactionType transactionType = event.getTransactionType();
        final Double transactionAmount = event.getTransactionAmount();

        switch (operation) {
            case CREATE:
                log.info(CREATE_TRANSACTION_EVENT_TEMPLATE, clientId, transactionType, transactionAmount);
                break;

            case UPDATE:
                log.info(UPDATE_TRANSACTION_EVENT_TEMPLATE, clientId, transactionType, transactionAmount);
                break;

            case DELETE:
                log.info(DELETE_TRANSACTION_EVENT_TEMPLATE, clientId, transactionType, transactionAmount);
                break;

            default:
                log.error(ERROR_MESSAGE_TEMPLATE, clientId);
                break;
        }
    }

}
