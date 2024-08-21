package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FraudClientDto {

    public FraudClientDto(){
        this.lastName = "";
        this.totalAmount = valueOf(0);
    }

    private Long clientId;
    private String email;
    private String firstName;
    private String lastName;
    private BigDecimal totalAmount;
}
