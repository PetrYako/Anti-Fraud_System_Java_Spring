package antifraud.controller.dto.access;

public class AccessResponse {
    private String status;

    public AccessResponse() {}

    public AccessResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
