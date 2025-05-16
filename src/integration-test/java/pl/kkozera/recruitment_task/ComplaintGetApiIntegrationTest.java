package pl.kkozera.recruitment_task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pl.kkozera.recruitment_task.dto.PagedResponse;
import pl.kkozera.recruitment_task.dto.complaint.ComplaintResponseDTO;
import pl.kkozera.recruitment_task.model.Customer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ComplaintGetApiIntegrationTest extends BaseIntegrationTest {

    private Customer customer;

    @BeforeEach
    void setupEach() {
        complaintRepository.deleteAll();
        customerRepository.deleteAll();

        customer = customerRepository.save(new Customer("John Doe", "john@example.com"));

        createComplaintInDb(1L, customer);
    }

    @Test
    void shouldReturnComplaintWithDefaultPagination() {
        //when
        ResponseEntity<PagedResponse<ComplaintResponseDTO>> response = restTemplate.exchange(
                getComplaintsBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).isNotEmpty();
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
        ComplaintResponseDTO dto = response.getBody().getContent().getFirst();
        assertThat(dto).isNotNull();
        assertThat(dto.submissionCount()).isEqualTo(1);
        assertThat(dto.content()).isEqualTo("Initial complaint");
        assertThat(dto.country()).isEqualTo("USA");
        assertThat(dto.customer()).isNotNull();
        assertThat(dto.productId()).isEqualTo(1L);
        assertThat(dto.customer().name()).isEqualTo("John Doe");
    }

    @Test
    void shouldReturnEmptyWhenNoComplaints() {
        //given
        complaintRepository.deleteAll();

        //when
        ResponseEntity<PagedResponse<ComplaintResponseDTO>> response = restTemplate.exchange(
                getComplaintsBaseUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
        assertThat(response.getBody().getPage()).isEqualTo(0);
        assertThat(response.getBody().isLast()).isTrue();
    }

    @Test
    void shouldReturnBadRequestOnInvalidSortField() {
        //when
        ResponseEntity<String> response = restTemplate.exchange(
                getComplaintsBaseUrl() + "?sort=nonexistentField,asc",
                HttpMethod.GET,
                null,
                String.class
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldSortComplaintsByProductId() {
        //given
        complaintRepository.deleteAll();
        createComplaintInDb(100L, customer);
        createComplaintInDb(200L, customer);

        //when
        ResponseEntity<PagedResponse<ComplaintResponseDTO>> response = restTemplate.exchange(
                getComplaintsBaseUrl() + "?sort=productId,desc",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        //then
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().getContent())
                .map(ComplaintResponseDTO::productId)
                .containsExactly(200L, 100L);
    }
}