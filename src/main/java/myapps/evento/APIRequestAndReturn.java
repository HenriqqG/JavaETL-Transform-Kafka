package myapps.evento;

import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class APIRequestAndReturn {

    public RetornoAPI buildRetornoAPI(InformacaoCliente informacaoCliente){
        if(!ObjectUtils.isEmpty(informacaoCliente.getClientIp())){
            String accessKey = "088d4a51a0c974748455efe60d30f70c";
            String url = "http://api.ipstack.com/"+informacaoCliente.getClientIp()+"?access_key="+accessKey;
            String method = "GET";

            JSONObject retornoConsulta = consumeJsonAPIFromUrl(url, method);

            return new RetornoAPI(informacaoCliente.getId(), informacaoCliente.getTimeStamp(), informacaoCliente.getClientIp(),
                    retornoConsulta.containsKey("latitude") ? retornoConsulta.get("latitude").toString() : null,
                    retornoConsulta.containsKey("longitude") ? retornoConsulta.get("longitude").toString() : null,
                    retornoConsulta.containsKey("country_name") ? retornoConsulta.get("country_name").toString() : null,
                    retornoConsulta.containsKey("region_name") ? retornoConsulta.get("region_name").toString() : null,
                    retornoConsulta.containsKey("city") ? retornoConsulta.get("city").toString() : null,
                    retornoConsulta.containsKey("info") ? retornoConsulta.get("info").toString() : null);
        }else{
            return new RetornoAPI(null, null, null,
                    null, null, null,
                    null, null, "Client IP cannot be null");
        }
    }

    public JSONObject consumeJsonAPIFromUrl(String requestUrl, String requestMethod){
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

                if(dataObject.containsKey("error")){
                    JSONParser parse = new JSONParser();
                    JSONObject dataErrorObject;
                    try {
                        dataErrorObject = (JSONObject) parse.parse(String.valueOf(dataObject.get("error")));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    dataObject = dataErrorObject;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObject;
    }

}
