package com.mikhail.belski.rest.kafka.postgres.debezium.service;

import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;

public interface ClientService {

    void publishClient(ClientDto clientDto);

}
