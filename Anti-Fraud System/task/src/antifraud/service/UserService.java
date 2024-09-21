package antifraud.service;

import antifraud.adapter.UserAdapter;
import antifraud.controller.dto.*;
import antifraud.controller.dto.access.AccessRequest;
import antifraud.controller.dto.access.AccessResponse;
import antifraud.controller.dto.user.UserRequest;
import antifraud.controller.dto.user.UserResponse;
import antifraud.model.User;
import antifraud.model.UserRole;
import antifraud.model.UserStatus;
import antifraud.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse create(UserRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername()).orElse(null);
        if (user != null) {
            throw new EntityExistsException("User already exists");
        }
        User admin = userRepository.findByAuthority(UserRole.ADMINISTRATOR.name()).orElse(null);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(request.getName(), request.getUsername(), encodedPassword, UserRole.MERCHANT.name(), UserStatus.LOCKED.name());
        if (admin == null) {
            newUser.setAuthority(UserRole.ADMINISTRATOR.name());
            newUser.setLocked(UserStatus.UNLOCKED.name());
        }
        User createdUser = userRepository.save(newUser);
        return mapToUserResponse(createdUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserAdapter(user);
    }

    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAllByOrderByIdAsc();
        return users.stream().map(this::mapToUserResponse).toList();
    }

    public void delete(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getAuthority());
    }

    public UserResponse changeRole(RoleRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getAuthority().equals(request.getRole())) {
            throw new EntityExistsException("User already has this role");
        }
        user.setAuthority(request.getRole());
        userRepository.save(user);
        return mapToUserResponse(user);
    }

    public AccessResponse changeAccess(AccessRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getAuthority().equals(UserRole.ADMINISTRATOR.name())) {
            throw new IllegalArgumentException("Can't lock administrator");
        }
        if (request.getOperation().equals(UserStatus.LOCKED.getOperation())) {
            user.setLocked(UserStatus.LOCKED.name());
        } else {
            user.setLocked(UserStatus.UNLOCKED.name());
        }
        user = userRepository.save(user);
        return new AccessResponse(
                "User" + " " + request.getUsername() + " " + user.getLocked().toLowerCase() + "!"
        );
    }
}
