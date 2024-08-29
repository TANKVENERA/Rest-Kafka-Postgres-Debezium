package com.mikhail.belski.rest.kafka.postgres.debezium.integrationtests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionType.INCOME;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.mikhail.belski.rest.kafka.postgres.debezium.RestKafkaPostgresDebeziumRunner;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.FraudClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.integrationtests.config.IntegrationTestConfig;
import com.mikhail.belski.rest.kafka.postgres.debezium.listener.ClientListener;
import com.mikhail.belski.rest.kafka.postgres.debezium.listener.FraudClientListener;
import com.mikhail.belski.rest.kafka.postgres.debezium.listener.TransactionListener;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = { RestKafkaPostgresDebeziumRunner.class, IntegrationTestConfig.class }, webEnvironment = RANDOM_PORT)
@Testcontainers
@DirtiesContext
public class ProducersIT {

    private static final ClientDto CLIENT_1 = ClientDto.builder().clientId(1L).email("mike.belski.1@gmail.com").firstName("First Mike").build();
    private static final ClientDto CLIENT_2 = ClientDto.builder().clientId(2L).email("mike.belski.2@gmail.com").firstName("Scnd Mike").build();
    private static final ClientDto CLIENT_3 = ClientDto.builder().clientId(3L).email("mike.belski.3@gmail.com").firstName("Third Mike").build();
    private static final ClientDto CLIENT_4 = ClientDto.builder().clientId(4L).email("mike.belski.4@gmail.com").firstName("Fraud Mike").lastName("Cunning Deceiver").build();

    private static final LocalDateTime CREATED_TIME = LocalDateTime.of(2020, 1, 1, 23, 59, 59);

    private static final TransactionDto TRANSACTION_1 = TransactionDto.builder().clientId(3L).bank("PKO").transactionType(INCOME).price(12.345).quantity(1).createdAt(CREATED_TIME).build();
    private static final TransactionDto TRANSACTION_2 = TransactionDto.builder().clientId(3L).bank("PKO").transactionType(INCOME).price(250.00).quantity(1).createdAt(CREATED_TIME).build();
    private static final TransactionDto TRANSACTION_3 = TransactionDto.builder().clientId(3L).bank("PKO").transactionType(INCOME).price(300.50).quantity(1).createdAt(CREATED_TIME).build();

    private static final TransactionDto FRAUD_TRANSACTION_1 = TransactionDto.builder().clientId(4L).bank("PKO").transactionType(INCOME).price(350.0).quantity(1).createdAt(CREATED_TIME).build();
    private static final TransactionDto FRAUD_TRANSACTION_2 = TransactionDto.builder().clientId(4L).bank("PKO").transactionType(INCOME).price(350.0).quantity(1).createdAt(CREATED_TIME).build();
    private static final TransactionDto FRAUD_TRANSACTION_3 = TransactionDto.builder().clientId(4L).bank("PKO").transactionType(INCOME).price(350.0).quantity(1).createdAt(CREATED_TIME).build();

    private static final FraudClientDto EXPECTED_FRAUD_CLIENT = FraudClientDto.builder().clientId(4L).email("mike.belski.4@gmail.com").firstName("Fraud Mike").lastName("Cunning Deceiver").totalAmount(new BigDecimal("1050.000000")).build();
    private static final List<ClientDto> EXPECTED_CLIENTS = List.of(CLIENT_1, CLIENT_2, CLIENT_3);
    private static final List<TransactionDto> EXPECTED_TRANSACTIONS = List.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3);
    private static final List<TransactionDto> EXPECTED_FRAUD_TRANSACTIONS = List.of(FRAUD_TRANSACTION_1, FRAUD_TRANSACTION_2, FRAUD_TRANSACTION_3);

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Container
    private static final KafkaContainer KAFKA_BROKER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"));
    @Container
    private static final JdbcDatabaseContainer POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:13.1-alpine")
            .withInitScript("db/init-db.sql")
            .withDatabaseName("test-db")
            .withUsername("test-mikki-rurk")
            .withPassword("test-54321");

    @SpyBean
    private ClientListener clientListener;
    @SpyBean
    private TransactionListener transactionListener;
    @SpyBean
    private FraudClientListener fraudClientListener;

    @Captor
    ArgumentCaptor<TransactionDto> transactionCaptor;
    @Captor
    ArgumentCaptor<ClientDto> clientCaptor;
    @Captor
    ArgumentCaptor<FraudClientDto> fraudClientCaptor;

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_BROKER::getBootstrapServers);

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
    }

    @AfterAll
    public static void afterAll() {
        KAFKA_BROKER.stop();
        POSTGRES_CONTAINER.stop();
    }

    @Test
    @Sql(statements = "TRUNCATE clients", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldPublishClientsAndConsumeSequentially() {
        EXPECTED_CLIENTS.forEach(cl -> testRestTemplate.postForEntity(buildUri("/client"), cl, Void.class));

        verify(clientListener, timeout(10000).times(3)).listenClient(clientCaptor.capture());
        final List<ClientDto> actualClients = clientCaptor.getAllValues();

        assertThat(actualClients, is(EXPECTED_CLIENTS));

    }

    @Test
    @Sql(statements = "TRUNCATE transactions", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void shouldPublishTransactionsAndConsumeSequentially() {
        EXPECTED_TRANSACTIONS.forEach(tr -> testRestTemplate.postForEntity(buildUri("/transaction"), tr, Void.class));

        verify(transactionListener, timeout(10000).times(3)).listenTransaction(transactionCaptor.capture());
        final List<TransactionDto> actualTransactions = transactionCaptor.getAllValues();

        assertThat(actualTransactions, is(EXPECTED_TRANSACTIONS));
    }

    @Test
    @Sql(statements = "TRUNCATE clients, transactions", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void shouldConsumeFromFraudTopicWhenTotalPriceGreaterThen1000() {
        testRestTemplate.postForEntity(buildUri("/client"), CLIENT_4, Void.class);
        verify(clientListener, timeout(10000).times(1)).listenClient(clientCaptor.capture());

        final ClientDto actualClient = clientCaptor.getValue();
        assertThat(actualClient, is(CLIENT_4));

        EXPECTED_FRAUD_TRANSACTIONS.forEach(tr -> testRestTemplate.postForEntity(buildUri("/transaction"), tr, Void.class));

        verify(transactionListener, timeout(10000).times(3)).listenTransaction(transactionCaptor.capture());
        verify(fraudClientListener, timeout(10000).times(1)).listenFraudClient(fraudClientCaptor.capture());
        final List<TransactionDto> actualTransactions = transactionCaptor.getAllValues();
        final FraudClientDto fraudClient = fraudClientCaptor.getValue();

        assertThat(actualTransactions, is(EXPECTED_FRAUD_TRANSACTIONS));
        assertThat(fraudClient, is(EXPECTED_FRAUD_CLIENT));
    }

    private URI buildUri(final String path) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path(contextPath)
                .path(path)
                .build()
                .toUri();
    }
}
