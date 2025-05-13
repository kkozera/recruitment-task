package pl.kkozera.recruitment_task.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeoLocationService {

    private final RestTemplate restTemplate;

    private final String url;

    public GeoLocationService(RestTemplateBuilder builder, Environment environment) {
        this.url = environment.getProperty("external.geoip.url");
        this.restTemplate = builder.build();
    }

    public String getCountryByIp(String ip) {
        try {
            String uri = url + ip + "/country_name/";
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "Unknown"; // fallback
        }
    }
}