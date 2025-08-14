package com.castle.property.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HouseActionRequest {
    @NotNull
    private HouseActionRequest.ActionTypes actionType;

    @Min(value = 0)
    private BigDecimal amount;

    public enum ActionTypes {
        Occupied,
        Vacant,
        ChangeAmount
    }
}
