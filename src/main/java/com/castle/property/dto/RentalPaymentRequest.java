package com.castle.property.dto;

import com.castle.property.datatype.PaymentMode;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
public class RentalPaymentRequest {
    @NotNull(message =  "missing rental")
    private UUID rentalPublicId;

    @NotNull(message = "missing payment mode")
    private PaymentMode paymentMode;

    @NotNull(message = "missing payment month")
    private LocalDate paymentMonth;

    @NotNull(message = "missing payment date")
    private LocalDate paymentDate;

    @NotNull(message = "missing amount")
    @DecimalMin(value = "1.00", message = "amount must be greater than 1")
    @Digits(integer = 11, fraction = 2)
    private BigDecimal amount;

    @Size(max = 250)
    private String message;

}
