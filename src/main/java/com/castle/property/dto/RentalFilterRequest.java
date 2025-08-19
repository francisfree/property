package com.castle.property.dto;

import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class RentalFilterRequest {
    private UUID propertyPublicId;
    private UUID housePublicId;
    private String identificationNumber;
    private String phoneNumber;
    private RentalAccountStatus accountStatus;
    private RentalArrearStatus arrearStatus;
    private String searchParam;
}
