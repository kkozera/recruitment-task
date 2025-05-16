package pl.kkozera.recruitment_task.external;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.kkozera.recruitment_task.util.IpFetcher;

@Slf4j
@Service
public class GeoLocationService {

    private static final String DEFAULT_COUNTRY = "Unknown";
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public GeoLocationService(RestTemplate restTemplate, @Value("${external.geoip.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";

    }

    public String fetchClientCountry(HttpServletRequest request) {
        String clientIp = IpFetcher.getClientIp(request);

        if (clientIp == null || clientIp.isBlank()) {
            log.warn("Client IP is missing or invalid.");
            return DEFAULT_COUNTRY;
        }

        String requestUrl = baseUrl + clientIp + "/country_name/";

        try {
            log.info("Fetching country name for IP: {}", clientIp);
            ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
            return response.getBody() != null ? response.getBody() : DEFAULT_COUNTRY;
        } catch (RestClientException e) {
            log.error("Failed to fetch country name for IP [{}]: {}. Returning default.", clientIp, e.getMessage());
            return DEFAULT_COUNTRY;
        }
    }
}