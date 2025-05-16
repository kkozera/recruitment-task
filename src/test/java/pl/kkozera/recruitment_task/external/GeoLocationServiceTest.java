package pl.kkozera.recruitment_task.external;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pl.kkozera.recruitment_task.util.IpFetcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeoLocationServiceTest {

    private RestTemplate restTemplate;
    private GeoLocationService geoLocationService;
    private HttpServletRequest request;

    private final String BASE_URL = "http://geo.service/";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        geoLocationService = new GeoLocationService(restTemplate, BASE_URL);
        request = mock(HttpServletRequest.class);
    }

    @Test
    void shouldReturnCountryWhenApiReturnsResponse() {
        withMockedIp("123.123.123.123", () -> {
            //given
            when(restTemplate.getForEntity(BASE_URL + "123.123.123.123/country_name/", String.class))
                    .thenReturn(ResponseEntity.ok("Poland"));

            //when
            String result = geoLocationService.fetchClientCountry(request);

            //then
            assertEquals("Poland", result);
        });
    }

    @Test
    void shouldReturnDefaultCountryWhenResponseBodyIsNull() {
        withMockedIp("123.123.123.123", () -> {
            //given
            when(restTemplate.getForEntity(BASE_URL + "123.123.123.123/country_name/", String.class))
                    .thenReturn(ResponseEntity.ok(null));

            //when
            String result = geoLocationService.fetchClientCountry(request);

            //then
            assertEquals("Unknown", result);
        });
    }

    @Test
    void shouldReturnDefaultCountryWhenExceptionThrown() {
        withMockedIp("123.123.123.123", () -> {
            //given
            when(restTemplate.getForEntity(BASE_URL + "123.123.123.123/country_name/", String.class))
                    .thenThrow(new RestClientException("Timeout"));

            //when
            String result = geoLocationService.fetchClientCountry(request);

            //then
            assertEquals("Unknown", result);
        });
    }

    @Test
    void shouldReturnDefaultCountryWhenIpIsNull() {
        withMockedIp(null, () -> {
            //when
            String result = geoLocationService.fetchClientCountry(request);

            //then
            assertEquals("Unknown", result);
        });
    }

    @Test
    void shouldReturnDefaultCountryWhenIpIsBlank() {
        withMockedIp("   ", () -> {
            //when
            String result = geoLocationService.fetchClientCountry(request);

            //then
            assertEquals("Unknown", result);
        });
    }

    private void withMockedIp(String ip, Runnable testBlock) {
        try (MockedStatic<IpFetcher> mockedIpFetcher = Mockito.mockStatic(IpFetcher.class)) {
            mockedIpFetcher.when(() -> IpFetcher.getClientIp(request)).thenReturn(ip);
            testBlock.run();
        }
    }
}