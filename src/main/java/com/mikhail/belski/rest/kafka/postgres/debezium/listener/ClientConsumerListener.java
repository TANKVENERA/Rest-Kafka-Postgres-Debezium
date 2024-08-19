package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.repository.CustomClientRepository;
import com.mikhail.belski.rest.kafka.postgres.debezium.transformer.ClientTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ClientConsumerListener {

    private static final String CONSUME_CLIENT_LOG_INFO_TEMPLATE =
            "[Client: Client Id={}, Email={}, Offset={}, Partition={} consumed]";

    private CustomClientRepository clientRepository;
    private ClientTransformer clientTransformer;

    @KafkaListener(topics = "${client.topic}", groupId = "client-group-id", containerFactory = "clientConsumerContainerFactory")
    public void clientConsumerListener(@Payload final ClientDto clientDto,
            @Header(KafkaHeaders.OFFSET) final Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int receivedPartitionId) {

        clientRepository.saveOrUpdate(clientTransformer.transform(clientDto));

        log.info(CONSUME_CLIENT_LOG_INFO_TEMPLATE, clientDto.getClientId(), clientDto.getEmail(), offset,
                receivedPartitionId);
    }

}
