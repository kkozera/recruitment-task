package pl.kkozera.recruitment_task.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class IpFetcherTest {

    @Test
    void shouldReturnIpFromXForwardedForHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.0.1");

        String ip = IpFetcher.getClientIp(request);

        assertEquals("192.168.0.1", ip);
    }

    @Test
    void shouldReturnFirstIpFromCommaSeparatedHeader() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.0.1, proxy1, proxy2");

        String ip = IpFetcher.getClientIp(request);

        assertEquals("192.168.0.1", ip);
    }

    @Test
    void shouldReturnIpFromOtherHeaderWhenXForwardedIsNull() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn("10.0.0.1");

        String ip = IpFetcher.getClientIp(request);

        assertEquals("10.0.0.1", ip);
    }

    @Test
    void shouldReturnRemoteAddrWhenNoHeadersPresent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        for (String header : new String[]{
                "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
        }) {
            when(request.getHeader(header)).thenReturn(null);
        }
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String ip = IpFetcher.getClientIp(request);

        assertEquals("127.0.0.1", ip);
    }

    @Test
    void shouldIgnoreUnknownHeaderValues() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(request.getHeader("Proxy-Client-IP")).thenReturn("unknown");
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn("");
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("172.16.0.5");

        String ip = IpFetcher.getClientIp(request);

        assertEquals("172.16.0.5", ip);
    }
}