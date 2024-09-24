package com.mikhail.belski.rest.kafka.postgres.debezium.streamprocessor;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.time.Duration;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.stereotype.Component;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.transformer.ClientTransactionJointTransformer;
import com.mikhail.belski.rest.kafka.postgres.debezium.transformer.FraudClientTransformer;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FraudClientStreamProcessorImpl implements FraudClientStreamProcessor {

    private ClientTransactionJointTransformer transactionJointTransformer;
    private FraudClientTransformer fraudClientTransformer;

    @Override
    public KTable<Windowed<String>, FraudClientDto> process(final Serde<String> keySerDe,
            final Serde<FraudClientDto> valueFraudSerDe, final KStream<String, TransactionDto> transactionStream,
            final KTable<String, ClientDto> clientTable) {

        return transactionStream.join(clientTable, (tr, cl) -> transactionJointTransformer.transform(tr, cl))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(2)))
                .aggregate(FraudClientDto::new, (key, joint, aggr) -> fraudClientTransformer.transform(joint, aggr),
                        Materialized.<String, FraudClientDto, WindowStore<Bytes, byte[]>>with(keySerDe, valueFraudSerDe)
                                .withCachingDisabled())
                .filter((window, fraud) -> isCustomerFraud(fraud));
    }

    private boolean isCustomerFraud(final FraudClientDto fraudClient) {
        final BigDecimal total = fraudClient.getTotalAmount();
        final String lastName = fraudClient.getLastName();

        return 0 < total.compareTo(valueOf(1000)) && (lastName == null || lastName.length() > 8);
    }
}
