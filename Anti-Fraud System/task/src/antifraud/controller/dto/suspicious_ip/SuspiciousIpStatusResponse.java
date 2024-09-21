package antifraud.controller.dto.suspicious_ip;

public class SuspiciousIpStatusResponse {
    private String status;

    public SuspiciousIpStatusResponse() {}

    public SuspiciousIpStatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
