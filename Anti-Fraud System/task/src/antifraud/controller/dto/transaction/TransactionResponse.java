package antifraud.controller.dto.transaction;

public class TransactionResponse {
    private String result;
    private String info;

    public TransactionResponse() {}

    public TransactionResponse(String result, String info) {
        this.result = result;
        this.info = info;
    }

    public String getResult() {
        return result;
    }

    public String getInfo() {
        return info;
    }
}
