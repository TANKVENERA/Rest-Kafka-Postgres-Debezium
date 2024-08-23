package com.mikhail.belski.rest.kafka.postgres.debezium.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "client")
@NoArgsConstructor
@AllArgsConstructor
public class ClientEntity {

    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long clientId;

    private String email;

    private String firstName;

    private String lastName;
}
