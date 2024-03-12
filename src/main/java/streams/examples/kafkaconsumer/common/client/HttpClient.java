package streams.examples.kafkaconsumer.common.client;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HttpClient {
    public JSONObject fetch(String requestUrl, String requestMethod){
        JSONObject dataObject;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = getHttpURLConnection(requestMethod, url);
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                HttpStatus httpStatus = HttpStatus.valueOf(responseCode);
                throw new ResponseStatusException(httpStatus, httpStatus.getReasonPhrase());
            } else {
                StringBuilder stringBuilder = fetchData(url);
                JSONParser jsonParser = new JSONParser();
                dataObject = (JSONObject) jsonParser.parse(String.valueOf(stringBuilder));
                if (dataObject.containsKey("error")) {
                    dataObject = parseErrorObject(dataObject);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataObject;
    }

    private StringBuilder fetchData(URL url) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(url.openStream())) {
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }
        }
        return stringBuilder;
    }

    private JSONObject parseErrorObject(JSONObject dataObject) {
        JSONParser parse = new JSONParser();
        try {
            return (JSONObject) parse.parse(String.valueOf(dataObject.get("error")));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpURLConnection getHttpURLConnection(String requestMethod, URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);
        conn.connect();
        return conn;
    }
}
