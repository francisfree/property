package com.castle.property.dto;


import java.time.LocalDateTime;
import java.util.UUID;


public record PaymentWeeklyEntryResponse(
        Long id,
        UUID publicId,
        LocalDateTime dateCreated,
        Integer weekCount,
        String weekName,
        String cash,
        String till,
        String mpesa
) {
}
