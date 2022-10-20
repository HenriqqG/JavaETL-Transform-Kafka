package myapps.evento;

public class InformacaoCliente {

    private Long id;
    private Long timeStamp;
    private String clientIp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String printInformacaoCliente(){
        return ("{'id':'"+this.id+"','timeStamp':'"+this.timeStamp+"','clientIp':'"+this.clientIp+"'}");
    }
}
