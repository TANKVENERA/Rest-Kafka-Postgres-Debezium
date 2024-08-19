package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private Long clientId;
    private String email;
    private String firstName;
    private String lastName;
}
