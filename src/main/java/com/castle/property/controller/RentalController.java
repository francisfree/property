package com.castle.property.controller;

import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.datatype.RentalArrearStatus;
import com.castle.property.dto.PagedResponse;
import com.castle.property.dto.RentalActionRequest;
import com.castle.property.dto.RentalFilterRequest;
import com.castle.property.dto.RentalRequest;
import com.castle.property.dto.RentalResponse;
import com.castle.property.entity.Rental;
import com.castle.property.mapper.RentalMapper;
import com.castle.property.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping
    public PagedResponse<RentalResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) UUID propertyId,
            @RequestParam(required = false) UUID houseId,
            @RequestParam(required = false) String identificationNumber,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) RentalAccountStatus accountStatus,
            @RequestParam(required = false) RentalArrearStatus arrearStatus,
            @RequestParam(required = false) String search
    ) {
        RentalFilterRequest filter = new RentalFilterRequest();
        filter.setPropertyPublicId(propertyId);
        filter.setHousePublicId(houseId);
        filter.setIdentificationNumber(identificationNumber);
        filter.setPhoneNumber(phoneNumber);
        filter.setAccountStatus(accountStatus);
        filter.setArrearStatus(arrearStatus);
        filter.setSearchParam(search);

        Page<Rental> result = rentalService.getRentals(filter, PageRequest.of(page, size));
        return PagedResponse.of(result.map(RentalMapper::toResponse));
    }

    @GetMapping("/{id}")
    public RentalResponse get(@PathVariable UUID id) {
        return RentalMapper.toResponse(rentalService.getRental(id));
    }

    @PostMapping
    public ResponseEntity<RentalResponse> create(@Valid @RequestBody RentalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RentalMapper.toResponse(rentalService.createRental(request)));
    }

    @PutMapping("/{id}/change-amount")
    public RentalResponse changeAmount(@PathVariable UUID id, @Valid @RequestBody RentalActionRequest request) {
        request.setActionType(RentalActionRequest.ActionTypes.ChangeAmount);
        return RentalMapper.toResponse(rentalService.rentalActions(id, request));
    }

    @PutMapping("/{id}/close-account")
    public RentalResponse closeAccount(@PathVariable UUID id) {
        RentalActionRequest request = new RentalActionRequest();
        request.setActionType(RentalActionRequest.ActionTypes.CloseAccount);
        return RentalMapper.toResponse(rentalService.rentalActions(id, request));
    }
}