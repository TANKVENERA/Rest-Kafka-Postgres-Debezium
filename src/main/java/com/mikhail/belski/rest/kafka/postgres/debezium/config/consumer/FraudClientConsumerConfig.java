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
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;

@Configuration
public class FraudClientConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, FraudClientDto> fraudClientConsumerFactory() {
        final HashMap<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fraud-client-group-id");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(FraudClientDto.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FraudClientDto> fraudClientConsumerContainerFactory(
            final ConsumerFactory<String, FraudClientDto> fraudConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, FraudClientDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(fraudConsumerFactory);

        return factory;
    }
}
