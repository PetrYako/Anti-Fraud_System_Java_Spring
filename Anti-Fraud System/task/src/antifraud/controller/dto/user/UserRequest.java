package antifraud.controller.dto.user;

public class UserRequest {
    private String name;
    private String username;
    private String password;

    public UserRequest() {}

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
