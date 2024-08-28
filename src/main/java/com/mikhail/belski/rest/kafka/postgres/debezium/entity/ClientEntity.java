package com.mikhail.belski.rest.kafka.postgres.debezium.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "clients")
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {

    @Id
    private Long clientId;

    private String email;

    private String firstName;

    private String lastName;
}
