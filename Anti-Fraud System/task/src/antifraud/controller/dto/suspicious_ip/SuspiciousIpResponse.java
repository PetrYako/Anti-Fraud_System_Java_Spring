package antifraud.controller.dto.suspicious_ip;

public class SuspiciousIpResponse {
    private Long id;
    private String ip;

    public SuspiciousIpResponse() {}

    public SuspiciousIpResponse(Long id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }
}
