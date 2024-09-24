package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import static java.math.BigDecimal.valueOf;
import static com.mikhail.belski.rest.kafka.postgres.debezium.util.UtilHelper.setScale;

import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientTransactionJoint;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;

@Component
public class ClientTransactionJointTransformerImpl implements ClientTransactionJointTransformer{

    @Override
    public ClientTransactionJoint transform(final TransactionDto transaction, final ClientDto client) {
        return ClientTransactionJoint.builder()
                .clientId(client.getClientId())
                .email(client.getEmail())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .transactionAmount(setScale(valueOf(transaction.getPrice())).multiply(valueOf(transaction.getQuantity())))
                .build();
    }
}
