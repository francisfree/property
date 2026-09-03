package com.castle.property.dto;

import com.castle.property.datatype.Floor;
import com.castle.property.datatype.HouseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API response for a House. Does not expose the JPA entity directly.
 */
public record HouseResponse(
        UUID id,
        String number,
        Floor floor,
        HouseStatus status,
        BigDecimal currentMonthlyRent,
        UUID propertyId,
        String propertyName,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
