package antifraud.controller.dto.user;

public class UserDeleteResponse {
    private String username;
    private String status;

    public UserDeleteResponse() {}

    public UserDeleteResponse(String username, String status) {
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}
