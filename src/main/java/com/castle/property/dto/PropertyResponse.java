package com.castle.property.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API response for a Property. Does not expose the JPA entity directly.
 */
public record PropertyResponse(
        UUID id,
        String name,
        String location,
        String area,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
