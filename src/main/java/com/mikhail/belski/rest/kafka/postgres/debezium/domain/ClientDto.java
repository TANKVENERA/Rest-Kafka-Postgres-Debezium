package com.mikhail.belski.rest.kafka.postgres.debezium.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    @NotNull
    private Long clientId;
    @NotNull
    private String email;
    private String firstName;
    private String lastName;
}
