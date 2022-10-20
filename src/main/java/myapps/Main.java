package myapps;

import myapps.evento.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static void configAndRun(StreamsBuilder builder){
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
        KStream<String, RetornoAPI> source = builder
                .stream("streams-informacaocliente-input",
                        Consumed.with(Serdes.String(), new InformacaoClienteSerde())
                ).peek(
                        (k, v) -> System.out.println(v.printInformacaoCliente())
                ).map(
                        (k, v) -> {
                            try {
                                return new KeyValue<>(k, buildRetornoAPI(v));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).peek(
                        (k, v) -> System.out.println(v.printRetornoAPI())
                );
        source.to("streams-retornoapi-output", Produced.with(Serdes.String(), new RetornoAPISerde()));

        configAndRun(builder);
    }

    private static RetornoAPI buildRetornoAPI(InformacaoCliente informacaoCliente) throws IOException {
        String accessKey = "088d4a51a0c974748455efe60d30f70c";
        String url = "http://api.ipstack.com/"+informacaoCliente.getClientIp()+"?access_key="+accessKey;
        String method = "GET";

        JSONObject retornoConsulta = consumeJsonAPIFromUrl(url, method);

        return new RetornoAPI(informacaoCliente.getId(), informacaoCliente.getTimeStamp(), informacaoCliente.getClientIp(),
                retornoConsulta.get("latitude").toString(),
                retornoConsulta.get("longitude").toString(),
                retornoConsulta.get("country_name").toString(),
                retornoConsulta.get("region_name").toString(),
                retornoConsulta.get("city").toString());
    }

    private static JSONObject consumeJsonAPIFromUrl(String requestUrl, String requestMethod){
        JSONObject dataObject = new JSONObject();
        try {
            URL url = new URL(requestUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                StringBuilder retornoIpStackAPI = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    retornoIpStackAPI.append(scanner.nextLine());
                }
                scanner.close();

                JSONParser jsonParser = new JSONParser();
                dataObject = (JSONObject) jsonParser.parse(String.valueOf(retornoIpStackAPI));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObject;
    }

}
