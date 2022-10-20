package myappstest.unittest;

import myapps.evento.*;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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

        JSONObject retornoAPI = apiRequestAndReturn.buildRetornoAPI(informacaoCliente);
        assertEquals(retornoAPI.getClass(), JSONObject.class);
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