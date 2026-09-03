package com.castle.property.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserRequest {

    @Email(message = "Email must be valid")
    private String email;

    private String firstName;

    private String lastName;

    private boolean enabled;

    private List<String> roleNames;
}
