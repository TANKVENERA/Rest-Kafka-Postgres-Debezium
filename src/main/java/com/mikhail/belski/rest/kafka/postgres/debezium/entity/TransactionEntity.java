package com.mikhail.belski.rest.kafka.postgres.debezium.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long clientId;

    private String bank;

    private String transactionType;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal transactionAmount;

    private LocalDateTime createdAt;
}
