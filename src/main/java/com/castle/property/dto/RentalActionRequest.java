package com.castle.property.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RentalActionRequest {
    @NotNull
    private RentalActionRequest.ActionTypes actionType;

    @Min(value = 1)
    private BigDecimal amount;

    public enum ActionTypes {
        ChangeAmount,
        CloseAccount
    }
}
