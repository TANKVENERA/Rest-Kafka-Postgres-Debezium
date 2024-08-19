package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FraudListener {

    @KafkaListener(topics = "${fraud.topic}", groupId = "fraud-group-id", containerFactory = "fraudConsumerContainerFactory")
    public void fraudListener(@Payload final FraudClientDto fraudInfo, @Header(KafkaHeaders.OFFSET) final Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int receivedPartitionId,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) final String key) {


        log.info("Recived Fraud Customer info: " + fraudInfo.toString());
    }
}
