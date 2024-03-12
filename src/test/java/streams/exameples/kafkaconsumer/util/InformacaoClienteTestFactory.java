package streams.exameples.kafkaconsumer.util;

import streams.examples.kafkaconsumer.entity.InformacaoCliente;
import streams.examples.kafkaconsumer.entity.dto.InformacaoClienteDTO;

public class InformacaoClienteTestFactory {
    public static InformacaoClienteDTO createInformacaoClienteDTO(String ip) {
        Long unixTime = System.currentTimeMillis() / 1000L;
        return new InformacaoClienteDTO(1L, unixTime, ip);
    }
}
