package streams.examples.transform.config;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultBackOffHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.lang.NonNull;
import org.springframework.util.backoff.FixedBackOff;
import streams.examples.transform.entity.dto.InformacaoClienteDTO;

import java.util.HashMap;

@Configuration
@EnableKafka
public class KafkaRetryConfig {
    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    @Value("${spring.kafka.back-off.retries}")
    private Short retries;

    @Value("${spring.kafka.back-off.interval}")
    private Long interval;

    @Value("${spring.kafka.consumer.concurrency}")
    private Integer concurrency;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Bean(name = "kafkaListenerContainerFactoryConsumer")
    public ConcurrentKafkaListenerContainerFactory<String, InformacaoClienteDTO> kafkaListenerContainerFactoryConsumer(KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, InformacaoClienteDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        factory.setCommonErrorHandler(kafkaErrorHandler(kafkaProperties));
        factory.setConcurrency(concurrency);
        return factory;
    }

    private ConsumerFactory<String, InformacaoClienteDTO> consumerFactory(KafkaProperties kafkaProperties) {
        HashMap<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());

        ErrorHandlingDeserializer<InformacaoClienteDTO> deserializer = new ErrorHandlingDeserializer<>(new JsonDeserializer<>(InformacaoClienteDTO.class));

        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
    }

    private DefaultErrorHandler kafkaErrorHandler(KafkaProperties kafkaProperties) {
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(getRecoverer(kafkaProperties),
                new FixedBackOff(interval, retries), new DefaultBackOffHandler());
        defaultErrorHandler.setRetryListeners(retryListener());
        defaultErrorHandler.setSeekAfterError(false);
        return defaultErrorHandler;
    }

    private DeadLetterPublishingRecoverer getRecoverer(KafkaProperties kafkaProperties) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate(kafkaProperties), (rec, ex) -> {
            System.out.printf("Erro ao processar mensagem, enviando para o topico de dead letter, key {%s}%n", rec.key());
            return new TopicPartition(topic + "-dlt", rec.partition());
        });
    }

    private RetryListener retryListener() {
        return new RetryListener() {
            @Override
            public void failedDelivery(@NonNull ConsumerRecord<?, ?> rcrd, @NonNull Exception ex, int deliveryAttempt) {
                System.out.printf("Falha na entrega para registro: {%s} com mensagem exceção: {%s} e tentativa de entrega: {%s}",
                        rcrd, ex.getMessage(), deliveryAttempt);
            }

            @Override
            public void recovered(@NonNull ConsumerRecord<?, ?> rcrd, @NonNull Exception ex) {
                System.out.printf("Registro recuperado com sucesso: {%s} com mensagem exceção: {%s}", rcrd, ex.getMessage());
                RetryListener.super.recovered(rcrd, ex);
            }

            @Override
            public void recoveryFailed(@NonNull ConsumerRecord<?, ?> rcrd, @NonNull Exception original, @NonNull Exception failure) {
                System.out.printf("Falha na recuperação do registro: {%s} com mensagem exceção: {%s} e falha: {%s}",
                        rcrd, original.getMessage(), failure.getMessage());
                RetryListener.super.recoveryFailed(rcrd, original, failure);
            }

        };
    }

    private KafkaTemplate<String, InformacaoClienteDTO> kafkaTemplate(KafkaProperties kafkaProperties) {
        HashMap<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps, new StringSerializer(), new JsonSerializer<>(new TypeReference<>() {})));
    }
}
