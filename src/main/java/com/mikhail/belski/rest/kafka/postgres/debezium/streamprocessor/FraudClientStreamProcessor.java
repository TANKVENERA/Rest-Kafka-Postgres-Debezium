package com.mikhail.belski.rest.kafka.postgres.debezium.streamprocessor;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Windowed;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;

public interface FraudClientStreamProcessor {

    KTable<Windowed<String>, FraudClientDto> process(Serde<String> keySerDe, Serde<FraudClientDto> valueFraudSerDe,
            KStream<String, TransactionDto> transactionStream, KTable<String, ClientDto> clientTable);
}
