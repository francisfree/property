package com.castle.property.service;

import com.castle.property.dto.user.CreateUserRequest;
import com.castle.property.dto.user.ResetPasswordRequest;
import com.castle.property.dto.user.UpdateUserRequest;
import com.castle.property.dto.user.UserResponse;
import com.castle.property.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    Page<User> getUsers(String search, Pageable pageable);

    User getUser(@NotNull UUID publicId);

    UserResponse createUser(@Valid CreateUserRequest request);

    UserResponse updateUser(@NotNull UUID publicId, @Valid UpdateUserRequest request);

    void deleteUser(@NotNull UUID publicId);

    void enableUser(@NotNull UUID publicId);

    void disableUser(@NotNull UUID publicId);

    void resetPassword(@NotNull UUID publicId, @Valid ResetPasswordRequest request);
}
