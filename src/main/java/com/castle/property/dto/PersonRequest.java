package com.castle.property.dto;

import com.castle.property.datatype.IdentificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonRequest {
    @NotBlank
    @Size(max = 250)
    private String firstName;

    @NotBlank
    @Size(max = 250)
    private String lastName;

    @Size(max = 250)
    private String otherName;

    @NotBlank
    @Size(max = 50)
    private String identificationNumber;

    @NotNull
    private IdentificationType identificationType;

    @NotBlank
    @Size(max = 250)
    private String nationality;

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^(\\+254|0)(7[0-9]|1[0-1])[0-9]{7}$", message = "invalid phone number ${validatedValue}")
    private String phoneNumber;
}
