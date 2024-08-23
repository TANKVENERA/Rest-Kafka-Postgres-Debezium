package com.mikhail.belski.rest.kafka.postgres.debezium.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UtilHelper {

    public static BigDecimal setScale(final BigDecimal price) {
        return price.setScale(6, RoundingMode.HALF_UP);
    }
}