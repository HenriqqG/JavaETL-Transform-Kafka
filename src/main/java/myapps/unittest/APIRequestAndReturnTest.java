package myapps.unittest;

import myapps.evento.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class APIRequestAndReturnTest {

    @Test
    void buildRetornoAPI() throws IOException {
        InformacaoCliente informacaoCliente = new InformacaoCliente();
        APIRequestAndReturn apiRequestAndReturn = new APIRequestAndReturn();

        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));

        String ip = in.readLine();

        Long unixTime = System.currentTimeMillis() / 1000L;

        informacaoCliente.setId(145L);
        informacaoCliente.setTimeStamp(unixTime);
        informacaoCliente.setClientIp(ip);

        RetornoAPI retornoAPI = apiRequestAndReturn.buildRetornoAPI(informacaoCliente);
        assertEquals(retornoAPI.getClass(), RetornoAPI.class);
    }

    @Test
    void consumeJsonAPIFromUrl() throws IOException {
        APIRequestAndReturn apiRequestAndReturn = new APIRequestAndReturn();

        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));

        String ip = in.readLine();

        String accessKey = "088d4a51a0c974748455efe60d30f70c";
        String url = "http://api.ipstack.com/"+ip+"?access_key="+accessKey;
        String method = "GET";
        JSONObject jsonObject = apiRequestAndReturn.consumeJsonAPIFromUrl(url, method);

        assertTrue(jsonObject.containsKey("ip"));
    }
}