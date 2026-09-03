package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.dto.user.CreateUserRequest;
import com.castle.property.dto.user.ResetPasswordRequest;
import com.castle.property.dto.user.UpdateUserRequest;
import com.castle.property.dto.user.UserResponse;
import com.castle.property.entity.Role;
import com.castle.property.entity.User;
import com.castle.property.repository.RoleRepository;
import com.castle.property.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsers(String search, Pageable pageable) {
        return userRepository.findBySearch(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(@NotNull UUID publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    @Transactional
    public UserResponse createUser(@Valid CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApplicationOperationException("user.username.exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationOperationException("user.email.exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);

        Set<Role> roles = resolveRoles(request.getRoleNames());
        user.setRoles(roles);

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse updateUser(@NotNull UUID publicId, @Valid UpdateUserRequest request) {
        User user = getUser(publicId);

        if (request.getEmail() != null) {
            userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new ApplicationOperationException("user.email.exists");
                }
            });
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        user.setEnabled(request.isEnabled());

        if (request.getRoleNames() != null) {
            Set<Role> roles = resolveRoles(request.getRoleNames());
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);
        return toUserResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(@NotNull UUID publicId) {
        User user = getUser(publicId);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void enableUser(@NotNull UUID publicId) {
        User user = getUser(publicId);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void disableUser(@NotNull UUID publicId) {
        User user = getUser(publicId);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resetPassword(@NotNull UUID publicId, @Valid ResetPasswordRequest request) {
        User user = getUser(publicId);
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private Set<Role> resolveRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return new HashSet<>();
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ApplicationOperationException("operation.record.not.found")))
                .collect(Collectors.toSet());
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getPublicId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEnabled(),
                user.getRoles().stream().map(Role::getName).toList(),
                user.getDateCreated(),
                user.getDateModified()
        );
    }
}
