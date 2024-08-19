package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private String bank;
    private Long clientId;
    private TransactionType transactionType;
    private Integer quantity;
    private Double price;

    @JsonProperty
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "Transaction{" + "type=" + transactionType + ", price=" + price + '}';
    }
}
