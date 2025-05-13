package pl.kkozera.recruitment_task.configuration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Configuration(proxyBeanMethods = false)
@Testcontainers
@SpringBootTest
public class IntegrationTestConfiguration {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withExposedPorts(5432)
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());
        return http.build();
    }
}
