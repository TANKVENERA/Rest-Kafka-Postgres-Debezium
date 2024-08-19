package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionType.valueOf;
import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation.CREATE;
import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation.DELETE;
import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation.UPDATE;

import java.util.LinkedHashMap;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionType;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.DebeziumMessageDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.MessageKeyDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionChangeEventListener {
    private static final String TRANSACTION_TYPE_FIELD = "transaction_type";
    private static final String TRANSACTION_AMOUNT_FIELD = "transaction_amount";

    private static final String CREATE_TRANSACTION_EVENT = "[Transaction: Client Id={}, Type={}, Amount={} was created]";
    private static final String UPDATE_TRANSACTION_EVENT = "[Transaction: Client Id={}, Type={}, Amount={} was updated]";
    private static final String DELETE_TRANSACTION_EVENT = "[Transaction: Client Id={}, Type={}, Amount Price={} was deleted]";
    private static final String ERROR_MESSAGE = "Unsupported operation on Transaction: Client Id={}";

    @KafkaListener(topics = "${transaction.change.event.topic}", groupId = "transaction-change-event-group-id", containerFactory = "transactionChangeEventContainerFactory")
    public void transactionChangeEventListener(@Payload(required = false) final DebeziumMessageDto eventMessage,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final MessageKeyDto key) {

        if (eventMessage == null) {
            return;
        }
        final String clientId = key.getPayload().getClientId();
        final Operation operation = eventMessage.getPayload().getOp();

        if (DELETE == operation) {
            final LinkedHashMap<String, ?> beforeFields = eventMessage.getPayload().getBefore();
            final TransactionType beforeType = valueOf((String) beforeFields.get(TRANSACTION_TYPE_FIELD));
            final Double beforeAmount = (Double) beforeFields.get(TRANSACTION_AMOUNT_FIELD);

            log.info(DELETE_TRANSACTION_EVENT, clientId, beforeType, beforeAmount);
        }
        else if (CREATE == operation || UPDATE == operation) {
            final LinkedHashMap<String, ?> afterFields = eventMessage.getPayload().getAfter();
            final TransactionType afterType = valueOf((String) afterFields.get(TRANSACTION_TYPE_FIELD));
            final Double afterAmount = (Double) afterFields.get(TRANSACTION_AMOUNT_FIELD);

            if (CREATE == operation) {
                log.info(CREATE_TRANSACTION_EVENT, clientId, afterType, afterAmount);
            }
            else {
                log.info(UPDATE_TRANSACTION_EVENT, clientId, afterType, afterAmount);
            }
        }
        else {
            log.error(ERROR_MESSAGE, clientId);
        }
    }

}
