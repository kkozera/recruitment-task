package pl.kkozera.recruitment_task.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate geoIpRestTemplate(@Value("${external.geoip.timeout:3}") int timeoutInSeconds) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(timeoutInSeconds * 1000);
        factory.setConnectTimeout(timeoutInSeconds * 2000);
        factory.setReadTimeout(timeoutInSeconds * 3000);

        return new RestTemplate(factory);
    }
}
