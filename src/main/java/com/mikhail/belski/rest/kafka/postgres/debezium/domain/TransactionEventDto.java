package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEventDto {
    @JsonProperty("transaction_type")
    private TransactionType transactionType;
    @JsonProperty("transaction_amount")
    private Double transactionAmount;
}
