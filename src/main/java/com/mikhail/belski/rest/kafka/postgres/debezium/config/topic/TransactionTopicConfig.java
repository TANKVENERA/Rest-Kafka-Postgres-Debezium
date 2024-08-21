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

    @Value(value = "${transaction.change.event.topic}")
    private String transactionChangeEventTopic;

    @Value(value = "${transaction.average.sum.topic}")
    private String transactionAverageSumTopic;

    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder.name(transactionTopic).partitions(3).replicas(1).compact().build();
    }

    @Bean
    public NewTopic transactionChangeEventTopic() {
        return TopicBuilder.name(transactionChangeEventTopic).partitions(1).replicas(1).compact().build();
    }

    @Bean
    public NewTopic transactionAverageSumTopic() {
        return TopicBuilder.name(transactionAverageSumTopic).partitions(1).replicas(1).compact().build();
    }
}
