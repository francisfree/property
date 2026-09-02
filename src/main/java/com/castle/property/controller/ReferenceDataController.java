package com.castle.property.controller;

import com.castle.property.datatype.Floor;
import com.castle.property.datatype.IdentificationType;
import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Exposes reference/enum data for dropdowns and filters in the frontend.
 */
@RestController
@RequestMapping("/api/v1/reference")
public class ReferenceDataController {

    @GetMapping("/floors")
    public List<String> floors() {
        return List.of(Floor.values()).stream()
                .map(Floor::getLabel)
                .toList();
    }

    @GetMapping("/identification-types")
    public List<IdentificationType> identificationTypes() {
        return List.of(IdentificationType.values());
    }

    @GetMapping("/account-statuses")
    public List<RentalAccountStatus> accountStatuses() {
        return List.of(RentalAccountStatus.values());
    }

    @GetMapping("/arrear-statuses")
    public List<RentalArrearStatus> arrearStatuses() {
        return List.of(RentalArrearStatus.values());
    }
}