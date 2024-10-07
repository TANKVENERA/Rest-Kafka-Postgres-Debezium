package com.mikhail.belski.rest.kafka.postgres.debezium.streamprocessor;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;

public interface ClientStreamProcessor {

    KTable<String, ClientDto> process(Serde<String> keySerDe, Serde<ClientDto> valueClientSerDe, KStream<String, ClientDto> clientStream);
}
