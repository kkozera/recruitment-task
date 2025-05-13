package pl.kkozera.recruitment_task.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI complaintApiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Complaint API")
                        .description("REST API for managing product complaints")
                        .version("0.0.1"));
    }
}