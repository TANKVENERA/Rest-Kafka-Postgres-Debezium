package com.mikhail.belski.rest.kafka.postgres.debezium.config;

import static java.time.Clock.systemDefaultZone;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    @Bean
    Clock defaultClock() {
        return systemDefaultZone().withZone(ZoneId.of("UTC"));
    }
}
