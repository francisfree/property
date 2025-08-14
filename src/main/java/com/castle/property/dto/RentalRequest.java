package com.castle.property.dto;

import com.castle.property.datatype.IdentificationType;
import com.castle.property.entity.Person;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString
@Setter
public class RentalRequest {
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

    @Size(max = 250)
    private String nationality;

    @NotBlank
    @Size(max = 20)
    @Pattern(regexp = "^(\\+254|0)(7[0-9]|1[0-1])[0-9]{7}$", message = "invalid phone number ${validatedValue}")
    private String phoneNumber;

    @NotNull(message = "missing house")
    private UUID housePublicId;

    @NotNull(message = "amount missing")
    @Min(value = 0)
    private BigDecimal amount;

}
