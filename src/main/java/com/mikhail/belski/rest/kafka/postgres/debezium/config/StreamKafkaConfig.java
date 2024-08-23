package com.mikhail.belski.rest.kafka.postgres.debezium.config;

import static java.math.BigDecimal.valueOf;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.common.serialization.Serdes.String;
import static org.apache.kafka.common.serialization.Serdes.serdeFrom;
import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static com.mikhail.belski.rest.kafka.postgres.debezium.util.UtilHelper.setScale;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
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
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientTransactionJoint;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;

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
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(APPLICATION_ID_CONFIG, "streams-app");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KTable<Windowed<String>, FraudClientDto> buildFraudClientInfoPipeline(final StreamsBuilder streamsBuilder) {
        final KStream<String, ClientDto> clientStream = streamsBuilder.stream(clientTopic, Consumed.with(keySerDe, valueClientSerDe));
        final KTable<String, ClientDto> clientTable = clientStream
                .groupByKey()
                .aggregate(ClientDto::new, (key, client, aggr) -> populateClient(client, aggr),
                        Materialized.<String, ClientDto, KeyValueStore<Bytes, byte[]>>with(keySerDe, valueClientSerDe)
                                .withCachingDisabled());

        final KStream<String, TransactionDto> transactionStream = streamsBuilder.stream(transactionTopic, Consumed.with(keySerDe, valueTransactionSerDe));
        final KTable<Windowed<String>, FraudClientDto> fraudClientWindowedTable =  transactionStream
                .join(clientTable, this::populateJointData)
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(2)))
                .aggregate(FraudClientDto::new, (key, joint, aggr) ->  populateFraudClient(joint, aggr),
                        Materialized.<String, FraudClientDto, WindowStore<Bytes, byte[]>>with(keySerDe, valueFraudSerDe).withCachingDisabled())
                .filter((window, fraud) -> isCustomerFraud(fraud));

        fraudClientWindowedTable.toStream().to(fraudTopic);

        return fraudClientWindowedTable;
    }

    private ClientDto populateClient(final ClientDto client, final ClientDto aggr) {
        aggr.setClientId(client.getClientId());
        aggr.setEmail(client.getEmail());
        aggr.setFirstName(client.getFirstName());
        aggr.setLastName(client.getLastName());

        return aggr;
    }

    private FraudClientDto populateFraudClient(final ClientTransactionJoint jointData, final FraudClientDto aggr) {
        aggr.setClientId(jointData.getClientId());
        aggr.setEmail(jointData.getEmail());
        aggr.setFirstName(jointData.getFirstName());
        aggr.setLastName(jointData.getLastName());

        aggr.setTotalAmount(aggr.getTotalAmount().add(jointData.getTransactionAmount()));

        return aggr;
    }

    private ClientTransactionJoint populateJointData(final TransactionDto transaction, final ClientDto client) {
        return ClientTransactionJoint.builder()
                .clientId(client.getClientId())
                .email(client.getEmail())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .transactionAmount(setScale(valueOf(transaction.getPrice())).multiply(valueOf(transaction.getQuantity())))
                .build();
    }

    private boolean isCustomerFraud(final FraudClientDto fraudClient) {

        return 0 < fraudClient.getTotalAmount().compareTo(valueOf(1000)) && fraudClient.getLastName().length() > 8;
    }
}
