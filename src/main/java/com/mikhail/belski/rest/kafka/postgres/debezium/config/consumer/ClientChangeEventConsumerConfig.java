package com.mikhail.belski.rest.kafka.postgres.debezium.config.consumer;

import java.util.HashMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ChangeEventMessageDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientEventDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.MessageKeyDto;

@Configuration
public class ClientChangeEventConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<MessageKeyDto, ChangeEventMessageDto<ClientEventDto>> clientChangeEventConsumerFactory(
            final ObjectMapper objectMapper) {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "client-change-event-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new JsonDeserializer<>(MessageKeyDto.class),
                new JsonDeserializer<>(ChangeEventMessageDto.class, objectMapper));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<MessageKeyDto, ChangeEventMessageDto<ClientEventDto>> clientChangeEventContainerFactory(
            final ConsumerFactory<MessageKeyDto, ChangeEventMessageDto<ClientEventDto>> clientChangeEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<MessageKeyDto, ChangeEventMessageDto<ClientEventDto>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(clientChangeEventConsumerFactory);

        return factory;
    }
}
