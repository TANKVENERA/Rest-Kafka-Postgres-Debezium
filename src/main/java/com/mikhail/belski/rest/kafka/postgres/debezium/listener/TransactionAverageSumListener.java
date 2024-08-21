package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.AverageSumDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.KeyPayload;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.MessageKeyDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionAverageSumListener {

    private static final String AVERAGE_SUM_EVENT = "[Client: Client Id={}, has Average Sum={} for the last one minute]";

    @KafkaListener(topics = "${transaction.average.sum.topic}", groupId = "transaction-average-sum-group-id", containerFactory = "transactionAverageSumContainerFactory")
    public void listenTransactionAverageSumEvent(@Payload(required = false) final AverageSumDto averageSum,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final MessageKeyDto key) {

        log.info(AVERAGE_SUM_EVENT, key.getPayload().getClientId(), averageSum.getPayload().getAverageSum());
    }
}
