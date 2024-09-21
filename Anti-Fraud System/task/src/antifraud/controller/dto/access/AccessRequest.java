package antifraud.controller.dto.access;

public class AccessRequest {
    private String username;
    private String operation;

    public AccessRequest() {
    }

    public String getUsername() {
        return username;
    }

    public String getOperation() {
        return operation;
    }
}
