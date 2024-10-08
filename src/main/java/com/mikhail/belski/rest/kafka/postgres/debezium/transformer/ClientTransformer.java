package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;

public interface ClientTransformer {

    ClientEntity transformToClientEntity(ClientDto clientDto);

    ClientDto transformToClientDto(final ClientDto client, final ClientDto aggr);
}
