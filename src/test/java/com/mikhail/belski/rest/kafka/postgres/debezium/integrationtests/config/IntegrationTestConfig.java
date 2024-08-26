package com.mikhail.belski.rest.kafka.postgres.debezium.integrationtests.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    @Primary
    public Clock fakeClock() {
        Instant instant = Instant.parse("2020-01-01T23:59:59.00Z");
        ZoneId zoneId = ZoneId.of("UTC");

        return Clock.fixed(instant, zoneId);
    }
}
