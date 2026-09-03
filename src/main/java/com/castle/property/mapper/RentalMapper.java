package com.castle.property.mapper;

import com.castle.property.dto.RentalResponse;
import com.castle.property.entity.Rental;

/**
 * Maps {@link Rental} entities to {@link RentalResponse} DTOs.
 */
public final class RentalMapper {

    private RentalMapper() {
    }

    public static RentalResponse toResponse(Rental rental) {
        if (rental == null) {
            return null;
        }
        return new RentalResponse(
                rental.getPublicId(),
                rental.getAmount(),
                rental.getAccountStatus(),
                rental.getArrearStatus(),
                PersonMapper.toResponse(rental.getPerson()),
                HouseMapper.toResponse(rental.getHouse()),
                rental.getDateCreated()
        );
    }
}
