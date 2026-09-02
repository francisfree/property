package com.castle.property.dto;

import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API response for a Rental. Does not expose the JPA entity directly.
 */
public record RentalResponse(
        UUID id,
        BigDecimal amount,
        RentalAccountStatus accountStatus,
        RentalArrearStatus arrearStatus,
        PersonResponse person,
        HouseResponse house,
        LocalDateTime dateCreated
) {
}
