package com.mikhail.belski.rest.kafka.postgres.debezium.domain.debezium;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageKeyDto {

    private KeyPayload payload;
}
