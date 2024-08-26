package com.mikhail.belski.rest.kafka.postgres.debezium.repository;

import org.springframework.data.repository.CrudRepository;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;

public interface ClientRepository extends CrudRepository<ClientEntity, Long> {

}
