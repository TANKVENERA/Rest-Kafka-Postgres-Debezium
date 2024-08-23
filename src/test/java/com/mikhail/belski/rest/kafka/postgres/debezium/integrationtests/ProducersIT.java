package com.mikhail.belski.rest.kafka.postgres.debezium.integrationtests;

import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionType.INCOME;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.mikhail.belski.rest.kafka.postgres.debezium.RestKafkaPostgresDebeziumRunner;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.ClientDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.domain.TransactionDto;
import com.mikhail.belski.rest.kafka.postgres.debezium.integrationtests.config.IntegrationTestConfig;
import com.mikhail.belski.rest.kafka.postgres.debezium.listener.ClientListener;
import com.mikhail.belski.rest.kafka.postgres.debezium.listener.TransactionListener;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = { RestKafkaPostgresDebeziumRunner.class, IntegrationTestConfig.class }, webEnvironment = RANDOM_PORT)
@Testcontainers
@DirtiesContext
public class ProducersIT {

    private static final ClientDto CLIENT_1 = ClientDto.builder().clientId(1L).email("mike.belski.1@gmail.com").firstName("First Mike").build();
    private static final ClientDto CLIENT_2 = ClientDto.builder().clientId(2L).email("mike.belski.2@gmail.com").firstName("Scnd Mike").build();
    private static final ClientDto CLIENT_3 = ClientDto.builder().clientId(3L).email("mike.belski.3@gmail.com").firstName("Third Mike").build();
    private static final LocalDateTime CREATED_TIME = LocalDateTime.of(2020, 1, 1, 23, 59, 59);

    private static final TransactionDto TRANSACTION_1 = TransactionDto.builder().clientId(3L).bank("PKO").transactionType(INCOME).price(12.345).quantity(1).createdAt(CREATED_TIME).build();
    private static final TransactionDto TRANSACTION_2 = TransactionDto.builder().clientId(3L).bank("PKO").transactionType(INCOME).price(300.00).quantity(1).createdAt(CREATED_TIME).build();
    private static final TransactionDto TRANSACTION_3 = TransactionDto.builder().clientId(3L).bank("PKO").transactionType(INCOME).price(25.50).quantity(1).createdAt(CREATED_TIME).build();

    private static final List<ClientDto> CLIENTS = List.of(CLIENT_1, CLIENT_2, CLIENT_3);
    private static final List<TransactionDto> TRANSACTIONS = List.of(TRANSACTION_1, TRANSACTION_2, TRANSACTION_3);

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private Clock fakeClock;

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

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_BROKER::getBootstrapServers);

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @BeforeAll
    public static void beforeAll() {
        KAFKA_BROKER.start();
        POSTGRES_CONTAINER.start();
    }
    
    @AfterAll
    public static void afterAll() {
        KAFKA_BROKER.stop();
        POSTGRES_CONTAINER.stop();
    }

    @Test
    public void shouldPublishClientsToOnePartitionAndConsumeSequentially() {
        CLIENTS.forEach(client -> testRestTemplate.postForEntity(buildUri("/client"), client, Void.class));

        verify(clientListener, Mockito.timeout(5000).times(1)).listenClient(CLIENT_1, 0L, 2);
        verify(clientListener, Mockito.timeout(5000).times(1)).listenClient(CLIENT_2, 1L, 2);
        verify(clientListener, Mockito.timeout(5000).times(1)).listenClient(CLIENT_3, 2L, 2);
    }

    @Test
    public void shouldPublishTransactionsToOnePartitionAndConsumeSequentially() {
        testRestTemplate.postForEntity(buildUri("/client"), CLIENT_3, Void.class);
        verify(clientListener, Mockito.timeout(5000).times(1)).listenClient(CLIENT_3, 0L, 2);

        TRANSACTIONS.forEach(tran -> testRestTemplate.postForEntity(buildUri("/transaction"), tran, Void.class));

        verify(transactionListener, Mockito.timeout(5000).times(1)).listenTransaction(TRANSACTION_1, 0L, 2);
        verify(transactionListener, Mockito.timeout(5000).times(1)).listenTransaction(TRANSACTION_2, 1L, 2);
        verify(transactionListener, Mockito.timeout(5000).times(1)).listenTransaction(TRANSACTION_3, 2L, 2);
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
