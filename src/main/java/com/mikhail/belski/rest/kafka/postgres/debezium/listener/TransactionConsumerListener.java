package com.mikhail.belski.rest.kafka.postgres.debezium.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.repository.CustomTransactionRepository;
import com.mikhail.belski.rest.kafka.postgres.debezium.transformer.TransactionTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionConsumerListener {

    private static final String CONSUME_TRANSACTION_LOG_INFO_TEMPLATE =
            "[Transaction: Client Id={}, Type={}, Price={}, Offset={}, Partition={} consumed]";

    private CustomTransactionRepository customTransactionRepository;
    private TransactionTransformer transactionTransformer;

    @KafkaListener(topics = "${transaction.topic}", groupId = "transaction-group-id", containerFactory = "transactionConsumerContainerFactory")
    public void clientListener(@Payload final TransactionDto transactionDto,
            @Header(KafkaHeaders.OFFSET) final Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int receivedPartitionId) {

        customTransactionRepository.saveIfClientExist(transactionTransformer.transform(transactionDto));

        log.info(CONSUME_TRANSACTION_LOG_INFO_TEMPLATE, transactionDto.getClientId(),
                transactionDto.getTransactionType(), transactionDto.getPrice(), offset, receivedPartitionId);
    }
}
