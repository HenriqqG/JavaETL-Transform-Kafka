package myapps.evento;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class InformacaoClienteSerde implements Serde<InformacaoCliente> {

    private JsonSerializer<InformacaoCliente> serializer;
    private JsonDeserializer<InformacaoCliente> deserializer;

    public InformacaoClienteSerde() {
        this.serializer = new JsonSerializer<>(InformacaoCliente.class);
        this.deserializer = new JsonDeserializer<>(InformacaoCliente.class);
    }

    @Override
    public Serializer<InformacaoCliente> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<InformacaoCliente> deserializer() {
        return deserializer;
    }
}
