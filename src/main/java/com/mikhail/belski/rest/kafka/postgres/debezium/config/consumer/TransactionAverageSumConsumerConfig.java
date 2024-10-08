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
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionAverageSumDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.MessageKeyDto;

@Configuration
public class TransactionAverageSumConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<MessageKeyDto, TransactionAverageSumDto> transactionAverageSumConsumerFactory() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-average-sum-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new JsonDeserializer<>(MessageKeyDto.class),
                new JsonDeserializer<>(TransactionAverageSumDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<MessageKeyDto, TransactionAverageSumDto> transactionAverageSumContainerFactory(
            final ConsumerFactory<MessageKeyDto, TransactionAverageSumDto> transactionAverageSumConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<MessageKeyDto, TransactionAverageSumDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionAverageSumConsumerFactory);

        return factory;
    }
}
