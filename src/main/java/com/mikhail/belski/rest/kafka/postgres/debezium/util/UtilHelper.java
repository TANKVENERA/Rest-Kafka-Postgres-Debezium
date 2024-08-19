package com.mikhail.belski.rest.kafka.postgres.debezium.util;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionType;

public class UtilHelper {

    public static Double countTransaction(final TransactionDto transaction) {
        if (TransactionType.INCOME == transaction.getTransactionType()) {
            return transaction.getPrice() * transaction.getQuantity();
        }
        return -(transaction.getPrice() * transaction.getQuantity());
    }

    public static BigDecimal getPriceWithScale(final Double price) {
        return valueOf(price).setScale(6, RoundingMode.HALF_UP);
    }
}
