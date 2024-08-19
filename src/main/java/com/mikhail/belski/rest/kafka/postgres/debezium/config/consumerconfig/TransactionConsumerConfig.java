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
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.DebeziumMessageDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium.MessageKeyDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.TransactionEntity;

@Configuration
public class TransactionConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, TransactionDto> transactionConsumerFactory() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(TransactionDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionDto> transactionConsumerContainerFactory(
            final ConsumerFactory<String, TransactionDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> fct =
                new ConcurrentKafkaListenerContainerFactory<>();
        fct.setConsumerFactory(consumerFactory);

        return fct;
    }

    @Bean
    public ConsumerFactory<MessageKeyDto, DebeziumMessageDto> transactionChangeEventConsumerFactory(
            final ObjectMapper objectMapper) {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-change-event-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new JsonDeserializer<>(MessageKeyDto.class),
                new JsonDeserializer<>(DebeziumMessageDto.class, objectMapper));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<MessageKeyDto, DebeziumMessageDto> transactionChangeEventContainerFactory(
            final ConsumerFactory<MessageKeyDto, DebeziumMessageDto> transactionChangeEventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<MessageKeyDto, DebeziumMessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionChangeEventConsumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> transactionAverageSumConsumerFactory() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-average-sum-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new StringDeserializer());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> transactionAverageSumContainerFactory(
            final ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
