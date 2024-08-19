package com.mikhail.belski.rest.kafka.postgres.debezium.repository;

import org.springframework.stereotype.Repository;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.TransactionEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class CustomTransactionRepositoryImpl implements CustomTransactionRepository {

    private ClientRepository clientRepository;
    private TransactionRepository transactionRepository;

    @Override
    public void saveIfClientExist(final TransactionEntity transactionEntity) {
        final Long id = clientRepository.getIdByClientId(transactionEntity.getClientId());

        if(id != null) {
            transactionRepository.save(transactionEntity);
        }
        else {
            log.error(" Transaction: has no corresponding client for: client id= " + transactionEntity.getClientId());
        }
    }
}
