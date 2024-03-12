package streams.examples.kafkaconsumer.listener;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import streams.examples.kafkaconsumer.common.client.APIClient;
import streams.examples.kafkaconsumer.entity.InformacaoCliente;
import streams.examples.kafkaconsumer.entity.dto.InformacaoClienteDTO;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class KafkaListenerComponent {

    @Value("${spring.kafka.producer.topic}")
    private String topic;
    private final KafkaTemplate<String, InformacaoClienteDTO> kafkaTemplate;
    @KafkaListener(
            id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.consumer.topic}",
            containerFactory = "kafkaListenerContainerFactoryConsumer"
    )
    public void onMessage(@Payload InformacaoClienteDTO message,
                          @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                          @Header(name = KafkaHeaders.CORRELATION_ID, required = false) String correlationId) throws ExecutionException, InterruptedException {
        try {
            correlationId = getCorrelationId(correlationId);
            MDC.put("correlationId", correlationId);
            System.out.printf("Mensagem recebida, key: {%s}, correlationId: {%s}", key, correlationId);
            process(message);
        } finally {
            MDC.remove("correlationId");
        }
    }

    private void process(InformacaoClienteDTO message) throws ExecutionException, InterruptedException {
        JSONObject jsonObject = new APIClient().consumeAPI(new InformacaoCliente(message));

        Message<JSONObject> payload = MessageBuilder.withPayload(jsonObject)
                .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                .setHeader(KafkaHeaders.CORRELATION_ID, MDC.get("correlationId"))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(payload).get();
    }

    private static String getCorrelationId(String correlationId) {
        return Objects.requireNonNullElse(correlationId, UUID.randomUUID().toString());
    }
}
