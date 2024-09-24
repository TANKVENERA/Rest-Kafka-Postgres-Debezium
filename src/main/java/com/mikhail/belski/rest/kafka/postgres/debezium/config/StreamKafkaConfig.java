package com.mikhail.belski.rest.kafka.postgres.debezium.config;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.common.serialization.Serdes.serdeFrom;
import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Windowed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.streamprocessor.ClientStreamProcessor;
import com.mikhail.belski.rest.kafka.postgres.debezium.streamprocessor.FraudClientStreamProcessor;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class StreamKafkaConfig {
    private final Serde<String> keySerDe = String();
    private final Serde<ClientDto> valueClientSerDe = serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(ClientDto.class));
    private final Serde<TransactionDto> valueTransactionSerDe = serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(TransactionDto.class));
    private final Serde<FraudClientDto> valueFraudSerDe = serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(
            FraudClientDto.class));

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${client.topic}")
    private String clientTopic;

    @Value(value = "${fraud.topic}")
    private String fraudTopic;

    @Value(value = "${transaction.topic}")
    private String transactionTopic;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "fraud-client-streams-app");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KTable<Windowed<String>, FraudClientDto> buildFraudClientInfoPipeline(final StreamsBuilder streamsBuilder,
            final ClientStreamProcessor clientStreamProcessor,
            final FraudClientStreamProcessor fraudClientStreamProcessor) {
        final KStream<String, ClientDto> clientStream = streamsBuilder.stream(clientTopic, Consumed.with(keySerDe, valueClientSerDe));
        final KTable<String, ClientDto> clientTable = clientStreamProcessor.process(keySerDe, valueClientSerDe, clientStream);

        final KStream<String, TransactionDto> transactionStream = streamsBuilder.stream(transactionTopic, Consumed.with(keySerDe, valueTransactionSerDe));
        final KTable<Windowed<String>, FraudClientDto> fraudClientWindowedTable = fraudClientStreamProcessor.process(keySerDe, valueFraudSerDe, transactionStream, clientTable);

        fraudClientWindowedTable.toStream().to(fraudTopic);

        return fraudClientWindowedTable;
    }
}
