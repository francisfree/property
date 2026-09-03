package com.castle.property.mapper;

import com.castle.property.dto.PaymentMonthResponse;
import com.castle.property.dto.PaymentWeeklyEntryResponse;
import com.castle.property.entity.PaymentMonth;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps {@link PaymentMonth} entities to {@link PaymentMonthResponse} DTOs.
 */
public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentMonthResponse toResponse(PaymentMonth paymentMonth) {
        if (paymentMonth == null) {
            return null;
        }
        List<PaymentWeeklyEntryResponse> weeklyEntries = paymentMonth.getWeeklyList()
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
                paymentMonth.getId(),
                paymentMonth.getPublicId(),
                paymentMonth.getDateCreated(),
                paymentMonth.getMonth(),
                paymentMonth.getBlockName(),
                paymentMonth.getHouseNumber(),
                paymentMonth.getOccupantName(),
                paymentMonth.getOccupantPhoneNumber(),
                paymentMonth.getRentCurrentMonth(),
                paymentMonth.getRentPreviousMonth(),
                paymentMonth.getArrearsBroughtForward(),
                paymentMonth.getTotalPayment(),
                paymentMonth.getPreviousWaterUnit(),
                paymentMonth.getCurrentWaterUnit(),
                paymentMonth.getPricePerUnit(),
                paymentMonth.getUnitsConsumed(),
                paymentMonth.getWaterBill(),
                weeklyEntries
        );
    }
}
