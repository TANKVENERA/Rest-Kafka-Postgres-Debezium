package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KeyPayload {

    @JsonProperty("client_id")
    private String clientId;
}
