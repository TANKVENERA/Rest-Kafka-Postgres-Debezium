package com.mikhail.belski.rest.kafka.postgres.debezium.config.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TransactionTopicConfig {

    @Value(value = "${transaction.topic}")
    private String transactionTopic;

    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder.name(transactionTopic).partitions(3).replicas(1).build();
    }
}
