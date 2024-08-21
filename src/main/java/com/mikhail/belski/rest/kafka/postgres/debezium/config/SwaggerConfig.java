package com.mikhail.belski.rest.kafka.postgres.debezium.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${info.group}")
    private String group;

    @Value("${info.title}")
    private String title;

    @Value("${info.description}")
    private String description;

    @Value("${info.version}")
    private String version;

    @Value("${server.servlet.context-path}")
    private String serverServletContextPath;

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group(group)
                .packagesToScan("com.mikhail.belski")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addServersItem(new Server().url(serverServletContextPath))
                .info(new Info().title(title)
                        .description(description)
                        .version(version));
    }
}
