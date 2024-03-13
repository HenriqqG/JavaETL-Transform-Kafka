package streams.exameples.kafkaconsumer;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import streams.exameples.kafkaconsumer.util.InformacaoClienteTestFactory;
import streams.examples.transform.common.client.APIClient;
import streams.examples.transform.entity.InformacaoCliente;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import streams.examples.transform.entity.dto.InformacaoClienteDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor
class APIClientTest {

    private InformacaoClienteDTO informacaoClienteDTO;
    @BeforeEach
    void init() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");

        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        String ip = in.readLine();

        informacaoClienteDTO = InformacaoClienteTestFactory.createInformacaoClienteDTO(ip);
    }

    @Test
    void assertTrueJSONObjectContainsKey() {
        InformacaoCliente informacaoCliente = new InformacaoCliente(informacaoClienteDTO);
        JSONObject jsonObject = new APIClient().consumeAPI(informacaoCliente);

        assertTrue(jsonObject.containsKey("ip"));
    }
}
