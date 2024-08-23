package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AverageSumPayload {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("average_sum")
    private BigDecimal averageSum;
}
