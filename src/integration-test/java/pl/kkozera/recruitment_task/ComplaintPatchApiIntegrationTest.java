package pl.kkozera.recruitment_task;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintPatchDTO;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.model.Complaint;
import pl.kkozera.recruitment_task.model.Customer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class ComplaintPatchApiIntegrationTest extends BaseIntegrationTest {

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("external.geoip.url", () -> "http://localhost:9561/");
    }

    private Long complaintId;

    @BeforeEach
    void setupEach() {
        complaintRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = customerRepository.save(new Customer("John Doe", "john@example.com"));

        Complaint complaint = new Complaint();
        complaint.setProductId(100L);
        complaint.setContent("Initial complaint");
        complaint.setCountry("USA");
        complaint.setCustomer(customer);

        complaintId = complaintRepository.save(complaint).getId();
    }

    @Test
    void shouldPatchComplaintContent() {
        //given
        ComplaintPatchDTO patch = new ComplaintPatchDTO();
        patch.setContent("Updated content");
        HttpEntity<ComplaintPatchDTO> request = new HttpEntity<>(patch, createHeaders("203.0.113.1"));

        //when
        ResponseEntity<ComplaintResponseDTO> response = restTemplate.exchange(
                getComplaintsBaseUrl() + "/" + complaintId + "/content", HttpMethod.PATCH, request, ComplaintResponseDTO.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().content()).isEqualTo("Updated content");
    }

    @Test
    void shouldReturnNotFoundWhenComplaintDoesNotExist() {
        //given
        ComplaintPatchDTO patch = new ComplaintPatchDTO();
        patch.setContent("Updated content");
        HttpEntity<ComplaintPatchDTO> request = new HttpEntity<>(patch, createHeaders("203.0.113.1"));

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                getComplaintsBaseUrl() + "99999" + "/content", HttpMethod.PATCH, request, String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnBadRequestWhenPatchIsEmpty() {
        //given
        HttpEntity<String> request = new HttpEntity<>("{}", createHeaders("203.0.113.1"));

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                getComplaintsBaseUrl() + "/" + complaintId + "/content", HttpMethod.PATCH, request, String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestForInvalidContentLength() {
        //given
        ComplaintPatchDTO patch = new ComplaintPatchDTO();
        patch.setContent("X".repeat(15000));
        HttpEntity<ComplaintPatchDTO> request = new HttpEntity<>(patch, createHeaders("203.0.113.1"));

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                getComplaintsBaseUrl() + "/" + complaintId + "/content", HttpMethod.PATCH, request, String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}