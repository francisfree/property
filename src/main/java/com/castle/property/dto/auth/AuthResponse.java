package com.castle.property.dto.auth;

import com.castle.property.dto.user.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
