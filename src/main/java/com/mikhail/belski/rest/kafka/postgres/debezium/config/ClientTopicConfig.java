package com.mikhail.belski.rest.kafka.postgres.debezium.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ClientTopicConfig {

    @Value(value = "${client.topic}")
    private String clientTopic;

    @Value(value = "${fraud.topic}")
    private String fraudTopic;

    @Value(value = "${client.change.event.topic}")
    private String clientChangeEventTopic;


    @Bean
    public NewTopic clientTopic() {
        return TopicBuilder.name(clientTopic).partitions(1).replicas(1).compact().build();
    }

    @Bean
    public NewTopic fraudTopic() {
        return TopicBuilder.name(fraudTopic).partitions(1).replicas(1).compact().build();
    }

    @Bean
    public NewTopic changeClientEventTopic() {
        return TopicBuilder.name(clientChangeEventTopic).partitions(1).replicas(1).compact().build();
    }
}
