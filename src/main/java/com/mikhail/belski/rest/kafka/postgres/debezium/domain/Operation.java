package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Operation {

    @JsonProperty("c") CREATE(),
    @JsonProperty("u") UPDATE(),
    @JsonProperty("d") DELETE()
}

