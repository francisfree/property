package com.castle.property.service;

import com.castle.property.dto.auth.AuthResponse;
import com.castle.property.dto.auth.ChangePasswordRequest;
import com.castle.property.dto.auth.LoginRequest;
import com.castle.property.dto.user.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response);

    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

    UserResponse getCurrentUser();

    void changePassword(ChangePasswordRequest request);

    void logout(HttpServletResponse response);
}
