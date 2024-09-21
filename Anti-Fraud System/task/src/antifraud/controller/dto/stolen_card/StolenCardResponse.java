package antifraud.controller.dto.stolen_card;

public class StolenCardResponse {
    private Long id;
    private String number;

    public StolenCardResponse() {}

    public StolenCardResponse(Long id, String number) {
        this.id = id;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }
}
