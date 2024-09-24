package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientTransactionJoint;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;

public interface FraudClientTransformer {

    FraudClientDto transform(final ClientTransactionJoint jointData, final FraudClientDto aggr);
}
