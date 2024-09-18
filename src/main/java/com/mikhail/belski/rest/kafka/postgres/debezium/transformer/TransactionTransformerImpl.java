package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.ClientEntity;
import com.mikhail.belski.rest.kafka.postgres.debezium.entity.TransactionEntity;
import com.mikhail.belski.rest.kafka.postgres.debezium.repository.ClientRepository;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class TransactionTransformerImpl implements TransactionTransformer {

    private ClientRepository clientRepository;

    @Override
    public TransactionEntity transform(final TransactionDto transactionDto) {
        final Integer quantity = transactionDto.getQuantity();
        final BigDecimal price = valueOf(transactionDto.getPrice()).setScale(6, RoundingMode.HALF_UP);

        return TransactionEntity.builder()
                    .bank(transactionDto.getBank())
                    .transactionType(transactionDto.getTransactionType())
                    .quantity(quantity)
                    .price(price)
                    .createdAt(transactionDto.getCreatedAt())
                    .transactionAmount(valueOf(quantity).multiply(price))
                    .client(getOrCreateClient(transactionDto.getClientId()))
                .build();
    }

    private ClientEntity getOrCreateClient(final Long clientId) {
         return clientRepository.findById(clientId).orElseGet(() -> clientRepository.save(getDummyClient(clientId)));
    }

    private ClientEntity getDummyClient (final Long clientId) {
        return ClientEntity.builder()
                        .clientId(clientId)
                        .firstName("dummy")
                        .lastName("dummy")
                        .email("dummy-email@test.com")
                     .build();
    }
}
