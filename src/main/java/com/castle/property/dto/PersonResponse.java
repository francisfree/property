package com.castle.property.dto;

import com.castle.property.datatype.IdentificationType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API response for a Person. Does not expose the JPA entity directly.
 */
public record PersonResponse(
        UUID id,
        String firstName,
        String lastName,
        String otherName,
        IdentificationType identificationType,
        String identificationNumber,
        String nationality,
        String phoneNumber,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
