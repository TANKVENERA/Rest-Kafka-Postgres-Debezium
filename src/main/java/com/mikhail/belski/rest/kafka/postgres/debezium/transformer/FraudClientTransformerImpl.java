package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientTransactionJoint;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;

@Component
public class FraudClientTransformerImpl implements FraudClientTransformer{

    @Override
    public FraudClientDto transform(final ClientTransactionJoint jointData, final FraudClientDto aggr) {

        return FraudClientDto.builder()
                    .clientId(jointData.getClientId())
                    .email(jointData.getEmail())
                    .firstName(jointData.getFirstName())
                    .lastName(jointData.getLastName())
                    .totalAmount(aggr.getTotalAmount().add(jointData.getTransactionAmount()))
                .build();
    }
}
