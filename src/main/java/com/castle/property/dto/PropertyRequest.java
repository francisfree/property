package com.castle.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
public class PropertyRequest {
    @NotBlank(message = "name missing")
    @Size(max = 250)
    private String name;

    @NotBlank(message = "location missing")
    @Size(max = 250)
    private String location;

    @NotBlank(message = "area missing")
    @Size(max = 250)
    private String area;
}
