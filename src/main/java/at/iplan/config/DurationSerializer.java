package at.iplan.config;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DurationSerializer extends JsonSerializer<Duration> {
    @Override
    public void serialize(Duration arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException, JsonProcessingException {
        arg1.writeNumber(arg0.getSeconds());
    }
}
