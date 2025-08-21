package com.castle.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RentalPaymentActionRequest {
    @NotNull
    private RentalPaymentActionRequest.ActionTypes actionType;

    @DecimalMin(value = "1.00", message = "amount must be greater than 1")
    @Digits(integer = 11, fraction = 2)
    private BigDecimal amount;

    public enum ActionTypes {
        ChangeAmount
    }
}
