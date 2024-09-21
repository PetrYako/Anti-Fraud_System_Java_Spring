package antifraud.controller.dto.stolen_card;

public class StolenCardStatusResponse {
    private String status;

    public StolenCardStatusResponse() {}

    public StolenCardStatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
