package com.mikhail.belski.rest.kafka.postgres.debezium.repository;

import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;

public interface CustomClientRepository {

    void saveOrUpdate(ClientEntity clientEntity);
}
