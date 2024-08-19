package com.mikhail.belski.rest.kafka.postgres.debezium.config.consumerconfig;

import java.util.HashMap;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.DebeziumMessageDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.MessageKeyDto;

@Configuration
public class ClientConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, ClientDto> clientConsumerFactory() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "client-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(ClientDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClientDto> clientConsumerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ClientDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(clientConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<String, FraudClientDto> fraudConsumerFactory() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fraud-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(FraudClientDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudClientDto> fraudConsumerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FraudClientDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(fraudConsumerFactory());

        return factory;
    }

    @Bean
    public ConsumerFactory<MessageKeyDto, DebeziumMessageDto> clientChangeEventConsumerFactory(
            final ObjectMapper objectMapper) {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "client-change-event-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new JsonDeserializer<>(MessageKeyDto.class),
                new JsonDeserializer<>(DebeziumMessageDto.class, objectMapper));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<MessageKeyDto, DebeziumMessageDto> clientChangeEventContainerFactory(
            final ConsumerFactory<MessageKeyDto, DebeziumMessageDto> clientChangeEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<MessageKeyDto, DebeziumMessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(clientChangeEventConsumerFactory);

        return factory;
    }
}
