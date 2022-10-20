package myapps.evento;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class RetornoAPISerde implements Serde<RetornoAPI> {

    private JsonSerializer<RetornoAPI> serializer;
    private JsonDeserializer<RetornoAPI> deserializer;

    public RetornoAPISerde() {
        this.serializer = new JsonSerializer<>(RetornoAPI.class);
        this.deserializer = new JsonDeserializer<>(RetornoAPI.class);
    }

    @Override
    public Serializer<RetornoAPI> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<RetornoAPI> deserializer() {
        return deserializer;
    }
}