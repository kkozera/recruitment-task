package pl.kkozera.recruitment_task;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import pl.kkozera.recruitment_task.model.Complaint;
import pl.kkozera.recruitment_task.model.Customer;
import pl.kkozera.recruitment_task.persistence.ComplaintRepository;
import pl.kkozera.recruitment_task.persistence.CustomerRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    protected int port;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected ComplaintRepository complaintRepository;

    static WireMockServer wireMockServer = new WireMockServer(9561);

    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"));

    @BeforeAll
    static void setup() {
        wireMockServer.start();
    }

    @AfterAll
    static void cleanup() {
        wireMockServer.stop();
        kafka.stop();
    }

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        kafka.start();
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    protected void createComplaintInDb(Long productId, Customer customer) {
        Complaint complaint = new Complaint();
        complaint.setProductId(productId);
        complaint.setContent("Initial complaint");
        complaint.setCountry("USA");
        complaint.setCustomer(customer);

        complaintRepository.save(complaint);
    }

    protected @NotNull String getComplaintsBaseUrl() {
        return "http://localhost:" + port + "/api/complaints";
    }

    protected @NotNull String getCustomersBaseUrl() {
        return "http://localhost:" + port + "/api/customers";
    }

    protected HttpHeaders createHeaders(String ipAddress) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Forwarded-For", ipAddress);
        return headers;
    }
}

