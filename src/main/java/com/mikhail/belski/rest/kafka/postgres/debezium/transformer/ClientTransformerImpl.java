package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;

@Component
public class ClientTransformerImpl implements ClientTransformer {

    @Override
    public ClientEntity transform(final ClientDto clientDto) {

        return ClientEntity.builder()
                    .clientId(clientDto.getClientId())
                    .email(clientDto.getEmail())
                    .firstName(clientDto.getFirstName())
                    .lastName(clientDto.getLastName())
                .build();
    }
}
