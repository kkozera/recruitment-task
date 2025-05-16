package pl.kkozera.recruitment_task.configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    private final ObjectMapper objectMapper;

    public JacksonConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void setUp() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StringTrimmingDeserializer());
        objectMapper.registerModule(module);
    }
}