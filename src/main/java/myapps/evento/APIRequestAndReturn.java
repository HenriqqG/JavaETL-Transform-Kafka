package myapps.evento;

import org.apache.commons.lang3.ObjectUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.Scanner;

public class APIRequestAndReturn {

    public JSONObject buildRetornoAPI(InformacaoCliente informacaoCliente){
        String ip = ObjectUtils.isEmpty(informacaoCliente.getClientIp()) ? null : informacaoCliente.getClientIp();
        String accessKey = "088d4a51a0c974748455efe60d30f70c";
        String url = "http://api.ipstack.com/"+ip+"?access_key="+accessKey;
        String method = "GET";

        JSONObject retornoConsulta = consumeJsonAPIFromUrl(url, method);
        JSONObject jsonObject = new JSONObject();

        if(!retornoConsulta.containsKey("info")){
            jsonObject.put("id", informacaoCliente.getId());
            jsonObject.put("timeStamp", informacaoCliente.getTimeStamp());
            jsonObject.put("clientIp", retornoConsulta.get("ip"));
            jsonObject.put("latitude", retornoConsulta.get("latitude"));
            jsonObject.put("longitude", retornoConsulta.get("longitude"));
            jsonObject.put("country", regexString(retornoConsulta.get("country_name").toString()));
            jsonObject.put("region", regexString(retornoConsulta.get("region_name").toString()));
            jsonObject.put("city", regexString(retornoConsulta.get("city").toString()));
        }else{
            jsonObject.put("error", retornoConsulta.get("info"));
        }

        return jsonObject;
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

                StringBuilder retornoAPIRequest = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    retornoAPIRequest.append(scanner.nextLine());
                }
                scanner.close();

                JSONParser jsonParser = new JSONParser();
                dataObject = (JSONObject) jsonParser.parse(String.valueOf(retornoAPIRequest));

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

    private String regexString(String src){
        return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

}
