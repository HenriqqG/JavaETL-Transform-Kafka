package myapps.evento;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.json.simple.JSONObject;

public class JSONObjectSerde implements Serde<JSONObject> {

    private JsonSerializer<JSONObject> serializer;
    private JsonDeserializer<JSONObject> deserializer;

    public JSONObjectSerde() {
        this.serializer = new JsonSerializer<>(JSONObject.class);
        this.deserializer = new JsonDeserializer<>(JSONObject.class);
    }

    @Override
    public Serializer<JSONObject> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<JSONObject> deserializer() {
        return deserializer;
    }
}