package com.mikhail.belski.rest.kafka.postgres.debezium.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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

    @OneToMany(mappedBy = "client")
    private List<TransactionEntity> transactions;
}
