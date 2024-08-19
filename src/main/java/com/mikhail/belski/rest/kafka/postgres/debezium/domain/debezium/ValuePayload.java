package com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium;

import java.util.LinkedHashMap;
import lombok.Data;

@Data
public class ValuePayload {

    private LinkedHashMap<String, ?> before;
    private LinkedHashMap<String, ?> after;
    private Operation op;
}
