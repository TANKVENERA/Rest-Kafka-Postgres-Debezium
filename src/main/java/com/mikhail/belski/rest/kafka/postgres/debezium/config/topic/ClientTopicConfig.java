package com.mikhail.belski.rest.kafka.postgres.debezium.config.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class ClientTopicConfig {

    @Value(value = "${client.topic}")
    private String clientTopic;

    @Value(value = "${client.change.event.topic}")
    private String clientChangeEventTopic;


    @Bean
    public NewTopic clientTopic() {
        return TopicBuilder.name(clientTopic).partitions(3).replicas(1).compact().build();
    }

    @Bean
    public NewTopic clientChangeEventTopic() {
        return TopicBuilder.name(clientChangeEventTopic).partitions(1).replicas(1).compact().build();
    }
}
