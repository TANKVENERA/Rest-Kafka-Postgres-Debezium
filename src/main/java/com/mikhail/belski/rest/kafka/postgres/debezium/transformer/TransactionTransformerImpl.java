package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.TransactionEntity;

@Component
public class TransactionTransformerImpl implements TransactionTransformer {

    @Override
    public TransactionEntity transform(final TransactionDto transactionDto) {
        final TransactionEntity transactionEntity = new TransactionEntity();
        final Integer quantity = transactionDto.getQuantity();
        final BigDecimal price = valueOf(transactionDto.getPrice()).setScale(6, RoundingMode.HALF_UP);

        transactionEntity.setClientId(transactionDto.getClientId());
        transactionEntity.setBank(transactionDto.getBank());
        transactionEntity.setTransactionType(transactionDto.getTransactionType().name());
        transactionEntity.setQuantity(quantity);
        transactionEntity.setPrice(price);
        transactionEntity.setCreatedAt(transactionDto.getCreatedAt());
        transactionEntity.setTransactionAmount(valueOf(quantity).multiply(price));

        return transactionEntity;
    }
}
