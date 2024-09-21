package antifraud.controller;

import antifraud.controller.dto.*;
import antifraud.controller.dto.access.AccessRequest;
import antifraud.controller.dto.access.AccessResponse;
import antifraud.controller.dto.user.UserDeleteResponse;
import antifraud.controller.dto.user.UserRequest;
import antifraud.controller.dto.user.UserResponse;
import antifraud.model.UserRole;
import antifraud.model.UserStatus;
import antifraud.service.UserService;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        try {
            if (request.getName() == null ||
                    request.getName().isBlank() ||
                    request.getPassword() == null ||
                    request.getPassword().isBlank() ||
                    request.getUsername() == null ||
                    request.getUsername().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            UserResponse response = userService.create(request);
            return ResponseEntity.status(201).body(response);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> getAll() {
        List<UserResponse> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<UserDeleteResponse> delete(@PathVariable String username) {
        try {
            userService.delete(username);
            return ResponseEntity.ok(new UserDeleteResponse(username, "Deleted successfully!"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/role")
    public ResponseEntity<UserResponse> changeRole(@RequestBody RoleRequest request) {
        try {
            if (request.getRole() == null || request.getRole().isBlank() ||
                    request.getUsername() == null || request.getUsername().isBlank() ||
                    (!request.getRole().equals(UserRole.SUPPORT.name()) && !request.getRole().equals(UserRole.MERCHANT.name()))) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(userService.changeRole(request));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (EntityExistsException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/access")
    public ResponseEntity<AccessResponse> changeAccess(@RequestBody AccessRequest request) {
        try {
            if (request.getUsername() == null || request.getUsername().isBlank() ||
                    request.getOperation() == null || request.getOperation().isBlank() ||
                    (!request.getOperation().equals(UserStatus.LOCKED.getOperation()) && !request.getOperation().equals(UserStatus.UNLOCKED.getOperation()))) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(userService.changeAccess(request));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
