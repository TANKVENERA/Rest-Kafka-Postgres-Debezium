package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @NotNull
    private Long clientId;
    private TransactionType transactionType;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double price;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
