package myapps;

import myapps.evento.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONObject;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static void configAndRun(StreamsBuilder builder)
    {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-challenge");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {

        final StreamsBuilder builder = new StreamsBuilder();
        APIRequestAndReturn apiRequestAndReturn = new APIRequestAndReturn();
        KStream<String, JSONObject> source = builder
                .stream("streams-informacaocliente-input",
                        Consumed.with(Serdes.String(), new InformacaoClienteSerde())
                ).peek(
                        (k, v) -> System.out.println(v.printInformacaoCliente())
                ).map(
                        (k, v) -> new KeyValue<>(k, apiRequestAndReturn.buildRetornoAPI(v))
                );
        source.to("streams-retornoapi-output", Produced.with(Serdes.String(), new JSONObjectSerde()));

        configAndRun(builder);
    }
}
