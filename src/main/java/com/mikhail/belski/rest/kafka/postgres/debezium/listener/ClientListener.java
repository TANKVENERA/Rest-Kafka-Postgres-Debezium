package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
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

    private static final String CONSUME_CLIENT_LOG_INFO_TEMPLATE =
            "[Client: Client Id={}, Email={}, Offset={}, Partition={} consumed]";

    private ClientRepository clientRepository;
    private ClientTransformer clientTransformer;

    @KafkaListener(topics = "${client.topic}", groupId = "client-group-id", containerFactory = "clientConsumerContainerFactory")
    public void listenClient(@Payload final ClientDto client, @Header(KafkaHeaders.OFFSET) final Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int receivedPartitionId) {
        clientRepository.save(clientTransformer.transform(client));

        log.info(CONSUME_CLIENT_LOG_INFO_TEMPLATE, client.getClientId(), client.getEmail(), offset,
                receivedPartitionId);
    }

}
