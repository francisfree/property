package com.castle.property.service;

import com.castle.property.dto.RentalActionRequest;
import com.castle.property.dto.RentalRequest;
import com.castle.property.entity.Rental;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RentalService {
    Rental createRental(@Valid RentalRequest request);

    Rental rentalActions(@NotNull UUID rentalPublicId, @Valid RentalActionRequest request);

    Rental getRental(@NotNull UUID rentalPublicId);

    List<Rental> listRentals(String searchParam);

    Page<Rental> getRentals(String searchParam, UUID housePublicId, Pageable pageable);

    Page<Rental> searchRentals(UUID propertyPublicId, UUID housePublicId, String identificationNumber, String phoneNumber, String searchParam, Pageable pageable);

    Rental getRentalById(String rowKey);

    Number getRentalsCount(String searchParam, UUID housePublicId);

    Number searchRentalsCount(UUID propertyPublicId, UUID housePublicId, String identificationNumber, String phoneNumber, String searchParam);

}
