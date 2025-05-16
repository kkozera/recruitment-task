package pl.kkozera.recruitment_task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.kkozera.recruitment_task.configuration.IntegrationTestConfiguration;
import pl.kkozera.recruitment_task.dto.PagedResponse;
import pl.kkozera.recruitment_task.dto.customer.CustomerRequestDTO;
import pl.kkozera.recruitment_task.dto.customer.CustomerResponseDTO;
import pl.kkozera.recruitment_task.model.Customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration.class)
@Testcontainers
@ActiveProfiles("test")
public class CustomerApiIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("external.geoip.url", () -> "http://localhost:9561/");
    }

    @Test
    void shouldCreateCustomer() {
        //given
        CustomerRequestDTO dto = new CustomerRequestDTO("John", "john@example.com");

        //when
        ResponseEntity<CustomerResponseDTO> response = restTemplate.postForEntity(getCustomersBaseUrl(), dto, CustomerResponseDTO.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("John");
        assertThat(response.getBody().email()).isEqualTo("john@example.com");
    }

    @Test
    void shouldNotCreateCustomerWithMissingFields() {
        //given
        CustomerRequestDTO invalidDTO = new CustomerRequestDTO("", "");

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(
                getCustomersBaseUrl(),
                invalidDTO,
                String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Name is required"));
        assertTrue(response.getBody().contains("Email is required"));
    }

    @Test
    void shouldNotCreateCustomerWithMissingEmail() {
        //given
        CustomerRequestDTO invalidDTO = new CustomerRequestDTO("John", "");

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(
                getCustomersBaseUrl(),
                invalidDTO,
                String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Email is required"));
    }

    @Test
    void shouldNotCreateCustomerWithMissingName() {
        //given
        CustomerRequestDTO invalidDTO = new CustomerRequestDTO("", "john@example.com");

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(
                getCustomersBaseUrl(),
                invalidDTO,
                String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Name is required"));
    }

    @Test
    void shouldNotCreateCustomerWithInvalidEmail() {
        //given
        CustomerRequestDTO invalidDTO = new CustomerRequestDTO("Jane", "not-an-email");

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(
                getCustomersBaseUrl(),
                invalidDTO,
                String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Email must be valid"));
    }

    @Test
    void shouldReturnAllCustomers() {
        //given
        customerRepository.save(prepareCustomer("John", "john@example.com"));
        customerRepository.save(prepareCustomer("Jan", "jan@example.com"));

        //when
        ResponseEntity<PagedResponse<CustomerResponseDTO>> response = restTemplate.exchange(
                getCustomersBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
        assertThat(response.getBody().getContent().size()).isEqualTo(2);
        assertThat(response.getBody().getContent().stream()
                .map(CustomerResponseDTO::name)
                .allMatch(name -> "John".equals(name) || "Jan".equals(name))).isTrue();
    }

    @Test
    void shouldReturnEmptyPageWhenNoCustomers() {
        //when
        ResponseEntity<PagedResponse<CustomerResponseDTO>> response = restTemplate.exchange(
                getCustomersBaseUrl() + "?page=10&size=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
    }

    @Test
    void shouldNotAllowDuplicateEmail() {
        //given
        CustomerRequestDTO request = new CustomerRequestDTO("John Smith", "john.duplicate@example.com");

        // First creation
        restTemplate.postForEntity(getCustomersBaseUrl(), request, CustomerResponseDTO.class);

        //when
        // Attempt duplicate
        ResponseEntity<String> response = restTemplate.postForEntity(getCustomersBaseUrl(), request, String.class);

        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Customer with email already exists:"));
    }

    @Test
    void shouldRejectTooLongEmail() {
        //given
        String longEmail = "a".repeat(250) + "@example.com";
        CustomerRequestDTO request = new CustomerRequestDTO("Test Name", longEmail);

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(getCustomersBaseUrl(), request, String.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldRejectTooLongName() {
        //given
        String longName = "a".repeat(300);
        CustomerRequestDTO request = new CustomerRequestDTO(longName, "longname@example.com");

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(getCustomersBaseUrl(), request, String.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldHandleNonNumericPaginationParamsGracefully() {
        //when
        ResponseEntity<String> response = restTemplate.exchange(
                getCustomersBaseUrl() + "?page=abc&size=xyz",
                HttpMethod.GET,
                null,
                String.class
        );

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldTrimInputFields() {
        //given
        CustomerRequestDTO request = new CustomerRequestDTO("  Alice  ", "  alice.trim@example.com  ");

        //when
        ResponseEntity<CustomerResponseDTO> response = restTemplate.postForEntity(
                getCustomersBaseUrl(), request, CustomerResponseDTO.class
        );

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertEquals("Alice", response.getBody().name());
        assertEquals("alice.trim@example.com", response.getBody().email());
    }

    private Customer prepareCustomer(String name, String email) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        return customer;
    }
}