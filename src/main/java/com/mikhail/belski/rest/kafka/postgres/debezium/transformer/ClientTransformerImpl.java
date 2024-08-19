package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;

@Component
public class ClientTransformerImpl implements ClientTransformer {

    @Override
    public ClientEntity transform(final ClientDto clientDto) {
        final ClientEntity clientEntity = new ClientEntity();
        clientEntity.setClientId(clientDto.getClientId());
        clientEntity.setEmail(clientDto.getEmail());
        clientEntity.setFirstName(clientDto.getFirstName());
        clientEntity.setLastName(clientDto.getLastName());

        return clientEntity;
    }
}
