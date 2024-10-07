package com.mikhail.belski.rest.kafka.postgres.debezium.streamprocessor;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.transformer.ClientTransformer;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ClientStreamProcessorImpl implements ClientStreamProcessor{

    private ClientTransformer clientTransformer;

    @Override
    public KTable<String, ClientDto> process(final Serde<String> keySerDe, final Serde<ClientDto> valueClientSerDe,
            final KStream<String, ClientDto> clientStream) {
        return clientStream.groupByKey()
                .aggregate(ClientDto::new, (key, client, aggr) -> clientTransformer.transformToClientDto(client, aggr),
                        Materialized.<String, ClientDto, KeyValueStore<Bytes, byte[]>>with(keySerDe, valueClientSerDe)
                                .withCachingDisabled());
    }
}
