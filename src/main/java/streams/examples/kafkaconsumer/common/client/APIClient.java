package streams.examples.kafkaconsumer.common.client;

import org.springframework.beans.factory.annotation.Value;
import streams.examples.kafkaconsumer.common.util.StringUtil;
import streams.examples.kafkaconsumer.entity.InformacaoCliente;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class APIClient {

    @Value("${api.ip-stack.url}")
    private static String apiUrl;

    @Value("${api.ip-stack.access-key}")
    private static String apiAccessKey;

    public JSONObject consumeAPI(InformacaoCliente informacaoCliente){
        String ip = informacaoCliente.getClientIp();
        String url = String.format("%s/%s?access_key=%s",apiUrl, ip, apiAccessKey);
        String method = "GET";

        HttpClient httpClient = new HttpClient();
        JSONObject retornoConsulta = httpClient.fetch(url, method);


        HashMap<String,Object> hashMap = new HashMap<>();
        if(!retornoConsulta.containsKey("info")){
            hashMap.put("id", informacaoCliente.getId());
            hashMap.put("timeStamp", informacaoCliente.getTimeStamp());
            hashMap.put("clientIp", retornoConsulta.get("ip"));
            hashMap.put("latitude", retornoConsulta.get("latitude"));
            hashMap.put("longitude", retornoConsulta.get("longitude"));
            hashMap.put("country", StringUtil.regex(retornoConsulta.get("country_name").toString()));
            hashMap.put("region", StringUtil.regex(retornoConsulta.get("region_name").toString()));
            hashMap.put("city", StringUtil.regex(retornoConsulta.get("city").toString()));
        }else{
            hashMap.put("error", retornoConsulta.get("info"));
        }
        return new JSONObject(hashMap);
    }
}
