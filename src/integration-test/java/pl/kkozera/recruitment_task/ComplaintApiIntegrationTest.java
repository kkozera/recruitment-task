package pl.kkozera.recruitment_task;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import pl.kkozera.recruitment_task.dto.ComplaintRequestDTO;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComplaintApiIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;


    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("external.geoip.url", () -> "http://localhost:9561/");
    }

    @BeforeEach
    void setupGeoMock() {
        wireMockServer.resetAll();
        wireMockServer.stubFor(get(urlPathMatching("/.*"))
                .willReturn(ok("Germany")));
    }

    @Test
    void shouldCreateComplaintAndSetCountryFromGeoService() {
        // Given
        ComplaintRequestDTO dto = new ComplaintRequestDTO(101L, "Product defect", "John");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Forwarded-For", "203.0.113.1");

        HttpEntity<ComplaintRequestDTO> request = new HttpEntity<>(dto, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/complaints", request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Product defect");
        assertThat(response.getBody()).contains("Germany");
    }
}