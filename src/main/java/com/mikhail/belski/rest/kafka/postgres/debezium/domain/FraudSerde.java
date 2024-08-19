package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;

public class FraudSerde extends Serdes.WrapperSerde<FraudClientDto> {

    public FraudSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(FraudClientDto.class));
    }
}
