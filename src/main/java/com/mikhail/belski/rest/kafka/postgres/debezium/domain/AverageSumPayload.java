package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AverageSumPayload {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("average_sum")
    private String averageSum;
}
