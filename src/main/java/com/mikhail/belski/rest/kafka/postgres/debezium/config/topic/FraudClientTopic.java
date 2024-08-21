package com.mikhail.belski.rest.kafka.postgres.debezium.config.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class FraudClientTopic {

    @Value(value = "${fraud.topic}")
    private String fraudTopic;

    @Bean
    public NewTopic fraudTopic() {
        return TopicBuilder.name(fraudTopic).partitions(1).replicas(1).compact().build();
    }
}
