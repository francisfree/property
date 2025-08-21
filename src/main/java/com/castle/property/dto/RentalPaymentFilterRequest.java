package com.castle.property.dto;

import com.castle.property.datatype.PaymentMode;
import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class RentalPaymentFilterRequest {
    private UUID propertyPublicId;
    private UUID housePublicId;
    private UUID rentalPublicId;
    private LocalDate paymentMonth;
    private LocalDate startPaymentDate;
    private LocalDate endPaymentDate;
    private List<LocalDate> paymentDates;
    private PaymentMode paymentMode;
    private String searchParam;
}
