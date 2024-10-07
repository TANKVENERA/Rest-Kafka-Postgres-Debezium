package com.mikhail.belski.rest.kafka.postgres.debezium.transformer;

import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientTransactionJoint;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;

public interface ClientTransactionJointTransformer {

    ClientTransactionJoint transform(TransactionDto transaction, ClientDto client);
}
