package com.mikhail.belski.rest.kafka.postgres.debezium.config.consumer;

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
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;

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
            final ConsumerFactory<String, TransactionDto> transactionConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionDto> fct =
                new ConcurrentKafkaListenerContainerFactory<>();
        fct.setConsumerFactory(transactionConsumerFactory);

        return fct;
    }
}
