package com.castle.property.service;

import com.castle.property.dto.RentalActionRequest;
import com.castle.property.dto.RentalFilterRequest;
import com.castle.property.dto.RentalRequest;
import com.castle.property.entity.Rental;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RentalService {
    Rental createRental(@Valid RentalRequest request);

    Rental rentalActions(@NotNull UUID rentalPublicId, @Valid RentalActionRequest request);

    Rental getRental(@NotNull UUID rentalPublicId);

    Rental getRentalById(String rowKey);

    Page<Rental> getRentals(RentalFilterRequest rentalFilterRequest, Pageable pageable);

    Number getRentalsCount(RentalFilterRequest rentalFilterRequest);

}
