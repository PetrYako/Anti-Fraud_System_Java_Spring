package antifraud.controller.dto.user;

public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String role;

    public UserResponse() {}

    public UserResponse(Long id, String name, String username, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
