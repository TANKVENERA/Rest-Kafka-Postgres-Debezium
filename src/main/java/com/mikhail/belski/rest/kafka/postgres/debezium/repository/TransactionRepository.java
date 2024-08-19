package com.mikhail.belski.rest.kafka.postgres.debezium.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.TransactionEntity;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

}
