package com.mikhail.belski.rest.kafka.postgres.debezium.service;

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private static final String PUBLISH_CLIENT_LOG_INFO_TEMPLATE = "[Client={} was published to partition={}]";

    private StreamsBuilderFactoryBean factoryBean;
    private KafkaTemplate<String, ClientDto> clientProducerTemplate;
    private NewTopic clientTopic;

    @Override
    public void publishClient(final ClientDto clientDto) {
        final ListenableFuture<SendResult<String, ClientDto>> sendResultFuture =
                clientProducerTemplate.send(clientTopic.name(), String.valueOf(clientDto.getClientId()), clientDto);

        try {
            final RecordMetadata recordMetadata = sendResultFuture.get().getRecordMetadata();

            log.info(PUBLISH_CLIENT_LOG_INFO_TEMPLATE, clientDto.getClientId(), recordMetadata.partition());
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
        }

    }
}
