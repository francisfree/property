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
        String rent,
        String garbage,
        String totalRentPaidPreviousMonth,
        String arrearsBroughtForward,
        String totalRentDue,
        String totalRentPaid,
        String previousWaterUnit,
        String currentWaterUnit,
        String unitsConsumed,
        String pricePerUnit,
        String waterBill,
        String arrearsCarriedForward,
        List<PaymentWeeklyEntryResponse> weeklyEntries
) {
}
