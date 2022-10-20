package myapps.evento;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.Serializer;


public class JsonSerializer<T> implements Serializer<T> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Class<T> mainObject;
    private final ObjectMapper objectMapper;

    public JsonSerializer(Class<T> mainObject) {
        this.mainObject = mainObject;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.findAndRegisterModules();
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] serialize(String s, T o) {
        try {
            String json = objectMapper.writeValueAsString(o);
            return json.getBytes(CHARSET);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error Serializing Object", e);
        }
    }

}

