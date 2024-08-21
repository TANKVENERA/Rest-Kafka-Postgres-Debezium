package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import lombok.Data;

@Data
public class ChangeEventPayload<T> {

    private T before;
    private T after;
    private Operation op;
}
