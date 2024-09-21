package antifraud.controller.dto.transaction;


import java.time.LocalDateTime;

public class TransactionRequest {
    private Double amount;
    private String ip;
    private String number;
    private String region;
    private LocalDateTime date;

    public TransactionRequest() {}

    public Double getAmount() {
        return amount;
    }

    public String getIp() {
        return ip;
    }

    public String getNumber() {
        return number;
    }

    public String getRegion() {
        return region;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
