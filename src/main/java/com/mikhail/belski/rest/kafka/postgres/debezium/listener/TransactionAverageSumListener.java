package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import static com.mikhail.belski.rest.kafka.postgres.debezium.util.UtilHelper.setScale;

import java.math.BigDecimal;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.MessageKeyDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionAverageSumDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionAverageSumListener {

    private static final String AVERAGE_SUM_EVENT_TEMPLATE = "[Client: Client Id={}, Average Sum={} for last 24 hrs]";

    @KafkaListener(topics = "${transaction.average.sum.topic}", groupId = "transaction-average-sum-group-id", containerFactory = "transactionAverageSumContainerFactory")
    public void listenTransactionAverageSumEvent(
            @Payload final TransactionAverageSumDto transactionAverageSum,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final MessageKeyDto key) {

        final BigDecimal averageSum = setScale(transactionAverageSum.getPayload().getAverageSum());

        log.info(AVERAGE_SUM_EVENT_TEMPLATE, key.getPayload().getClientId(), averageSum);
    }
}
