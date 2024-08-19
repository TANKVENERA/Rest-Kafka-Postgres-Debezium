package com.mikhail.belski.rest.kafka.postgres.debezium.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;

public interface ClientRepository extends CrudRepository<ClientEntity, Long> {

    @Query("select c.id from ClientEntity c where c.clientId=(:clientId)")
    Long getIdByClientId(@Param("clientId") Long clientId);
}
