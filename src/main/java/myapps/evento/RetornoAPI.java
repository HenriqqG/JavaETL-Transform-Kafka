package myapps.evento;

public class RetornoAPI {
    //Client ID
    private Long id;
    //Timestamp
    private Long timeStamp;
    //IP
    private String clientIp;
    //Latitude
    private String latitude;
    //Longitude
    private String longitude;
    //Country
    private String country;
    //Region
    private String region;
    //City
    private String city;

    public RetornoAPI(Long id, Long timeStamp, String clientIp, String latitude, String longitude, String country, String region, String city) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.clientIp = clientIp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.region = region;
        this.city = city;
    }

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String printRetornoAPI(){
        return ("{'id':'"+this.id+"'," +
                "'timeStamp':'"+this.timeStamp+"'," +
                "'clientIp':'"+this.clientIp+"'," +
                "'latitude':'"+this.latitude+"'," +
                "'longitude':'"+this.longitude+"'," +
                "'country':'"+this.country+"'," +
                "'region':'"+this.region+"'," +
                "'city':'"+this.city+"'}");
    }
}
