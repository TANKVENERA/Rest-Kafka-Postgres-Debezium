package com.mikhail.belski.rest.kafka.postgres.debezium.repository;

import org.springframework.stereotype.Repository;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class CustomClientRepositoryImpl implements CustomClientRepository {

    private ClientRepository clientRepository;

    @Override
    public void saveOrUpdate(final ClientEntity clientEntity) {
        Long id = clientRepository.getIdByClientId(clientEntity.getClientId());

        if (id != null) {
            clientEntity.setId(id);
            clientRepository.save(clientEntity);
        }

        clientRepository.save(clientEntity);
    }

}
