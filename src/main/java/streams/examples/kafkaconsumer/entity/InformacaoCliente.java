package streams.examples.kafkaconsumer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import streams.examples.kafkaconsumer.entity.dto.InformacaoClienteDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformacaoCliente {

    private Long id;
    private Long timeStamp;
    private String clientIp;

    public InformacaoCliente(InformacaoClienteDTO informacaoClienteDTO){
        this.setId(informacaoClienteDTO.id());
        this.setTimeStamp(informacaoClienteDTO.timeStamp());
        this.setClientIp(informacaoClienteDTO.clientIp());
    }

    public InformacaoClienteDTO toDTO(){
        return new InformacaoClienteDTO(this.getId(), this.getTimeStamp(), this.getClientIp());
    }

    @Override
    public String toString(){
        return "{" +
                "'id':'"+this.id+"'," +
                "'timeStamp':'"+this.timeStamp+"'," +
                "'clientIp':'"+this.clientIp+"'" +
                "}";
    }
}
