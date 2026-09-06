package com.castle.property.mapper;

import com.castle.property.dto.PaymentMonthResponse;
import com.castle.property.dto.PaymentWeeklyEntryResponse;
import com.castle.property.entity.RentalPayment;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps {@link RentalPayment} entities to {@link PaymentMonthResponse} DTOs.
 */
public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentMonthResponse toResponse(RentalPayment rentalPayment) {
        if (rentalPayment == null) {
            return null;
        }
        List<PaymentWeeklyEntryResponse> weeklyEntries = rentalPayment.getWeeklyEntries()
                .stream().map(paymentWeekly -> new PaymentWeeklyEntryResponse(
                        paymentWeekly.getId(),
                        paymentWeekly.getPublicId(),
                        paymentWeekly.getDateCreated(),
                        paymentWeekly.getWeekCount(),
                        paymentWeekly.getWeekName(),
                        paymentWeekly.getCash(),
                        paymentWeekly.getTill(),
                        paymentWeekly.getMpesa()
                )).collect(Collectors.toList());

        return new PaymentMonthResponse(
                rentalPayment.getId(),
                rentalPayment.getPublicId(),
                rentalPayment.getDateCreated(),
                rentalPayment.getReceiptNumber(),
                rentalPayment.getMonth(),
                rentalPayment.getBlockName(),
                rentalPayment.getHouseNumber(),
                rentalPayment.getOccupantName(),
                rentalPayment.getOccupantPhoneNumber(),
                rentalPayment.getRentCurrentMonth(),
                rentalPayment.getRentPreviousMonth(),
                rentalPayment.getArrearsBroughtForward(),
                rentalPayment.getTotalPayment(),
                rentalPayment.getPreviousWaterUnit(),
                rentalPayment.getCurrentWaterUnit(),
                rentalPayment.getPricePerUnit(),
                rentalPayment.getUnitsConsumed(),
                rentalPayment.getWaterBill(),
                weeklyEntries
        );
    }
}
