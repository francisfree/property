package com.castle.property.dto;

import com.castle.property.datatype.Floor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class HouseRequest {
    @NotBlank
    @Size(max = 250)
    private String number;

    @NotNull
    private Floor floor;

    @NotNull
    private UUID propertyPublicId;
}
