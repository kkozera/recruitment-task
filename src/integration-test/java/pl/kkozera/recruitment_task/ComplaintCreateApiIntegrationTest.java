package pl.kkozera.recruitment_task;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.kkozera.recruitment_task.configuration.TestKafkaConsumer;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintRequestDTO;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.model.Customer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@DirtiesContext
public class ComplaintCreateApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestKafkaConsumer testKafkaConsumer;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("external.geoip.url", () -> "http://localhost:9561/");
    }

    private Customer savedCustomer;

    @BeforeEach
    void setupEach() {
        wireMockServer.resetAll();
        wireMockServer.stubFor(get(urlPathMatching("/.*"))
                .willReturn(ok("Germany")));

        complaintRepository.deleteAll();
        customerRepository.deleteAll();

        savedCustomer = customerRepository.save(new Customer("John Doe", "john@example.com"));
    }

    @Test
    void shouldCreateComplaintSuccessfully() throws InterruptedException {
        //given
        ComplaintRequestDTO request = new ComplaintRequestDTO();
        request.setCustomerId(savedCustomer.getId());
        request.setProductId(1L);
        request.setContent("Product is defective");

        int expectedMessages = 1;
        testKafkaConsumer.resetLatch(expectedMessages);

        HttpEntity<ComplaintRequestDTO> httpEntity = new HttpEntity<>(request, createHeaders("203.0.113.1"));

        //when
        ResponseEntity<ComplaintResponseDTO> response = restTemplate.postForEntity(
                getComplaintsBaseUrl(), httpEntity, ComplaintResponseDTO.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().submissionCount()).isEqualTo(1);
        assertThat(response.getBody().content()).isEqualTo("Product is defective");
        assertThat(response.getBody().productId()).isEqualTo(1);

        boolean success = testKafkaConsumer.awaitMessages(10, TimeUnit.SECONDS);
        assertTrue(success, "Did not receive all Kafka messages in time");

        List<String> messages = testKafkaConsumer.getReceivedMessages();
        assertEquals(expectedMessages, messages.size());
        assertTrue(messages.getFirst().contains("SEND_EMAIL"));
        assertTrue(messages.getFirst().contains("john@example.com"));
    }

    @Test
    void shouldReturnBadRequestWhenCustomerNotFound() {
        //given
        ComplaintRequestDTO request = new ComplaintRequestDTO();
        request.setCustomerId(9999L); // non-existent
        request.setProductId(1L);
        request.setContent("Broken");

        HttpEntity<ComplaintRequestDTO> httpEntity = new HttpEntity<>(request, createHeaders("203.0.113.1"));

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(
                getComplaintsBaseUrl(),
                httpEntity,
                String.class
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Customer with id 9999 not found");
    }

    @Test
    void shouldReturnBadRequestForMissingFields() {
        //given
        ComplaintRequestDTO request = new ComplaintRequestDTO(); // empty

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(
                getComplaintsBaseUrl(),
                request,
                String.class
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldIncreaseSubmissionCountOnDuplicateComplaint() throws InterruptedException {
        //given
        int expectedMessages = 2;
        testKafkaConsumer.resetLatch(expectedMessages);
        // First complaint
        ComplaintRequestDTO request = new ComplaintRequestDTO();
        request.setCustomerId(savedCustomer.getId());
        request.setProductId(42L);
        request.setContent("Same product complaint");

        HttpEntity<ComplaintRequestDTO> httpEntity = new HttpEntity<>(request, createHeaders("203.0.113.1"));

        //when
        restTemplate.postForEntity(getComplaintsBaseUrl(), httpEntity, ComplaintResponseDTO.class);

        // Duplicate complaint
        ResponseEntity<ComplaintResponseDTO> response = restTemplate.postForEntity(getComplaintsBaseUrl(), httpEntity, ComplaintResponseDTO.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().submissionCount()).isEqualTo(2);
        assertThat(response.getBody().content()).isEqualTo("Same product complaint");
        assertThat(response.getBody().productId()).isEqualTo(42);


        boolean success = testKafkaConsumer.awaitMessages(10, TimeUnit.SECONDS);
        assertTrue(success, "Did not receive all Kafka messages in time");

        List<String> messages = testKafkaConsumer.getReceivedMessages();
        assertEquals(expectedMessages, messages.size());
        assertTrue(messages.stream().allMatch(message -> message.contains("SEND_EMAIL")));
        assertTrue(messages.stream().allMatch(message -> message.contains("john@example.com")));
    }
}