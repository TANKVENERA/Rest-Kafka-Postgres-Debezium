package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageSumDto {

    private AverageSumPayload payload;
}
