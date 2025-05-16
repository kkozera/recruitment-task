package pl.kkozera.recruitment_task.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class StringTrimmingDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext context) throws IOException {
        String value = p.getValueAsString();
        return value != null ? value.trim() : null;
    }
}
