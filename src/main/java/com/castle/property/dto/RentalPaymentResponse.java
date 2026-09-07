package com.castle.property.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record RentalPaymentResponse(
        Long id,
        UUID publicId,
        LocalDateTime dateCreated,
        String receiptNumber,
        LocalDate month,
        String blockName,
        String houseNumber,
        String occupantName,
        String occupantPhoneNumber,
        String rentCurrentMonth,
        String rentPreviousMonth,
        String arrearsBroughtForward,
        String totalPayment,
        String previousWaterUnit,
        String currentWaterUnit,
        String pricePerUnit,
        String unitsConsumed,
        String waterBill,
        List<PaymentWeeklyEntryResponse> weeklyEntries
) {
}
