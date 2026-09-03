package com.castle.property.mapper;

import com.castle.property.dto.HouseResponse;
import com.castle.property.entity.House;
import com.castle.property.entity.Property;

/**
 * Maps {@link House} entities to {@link HouseResponse} DTOs.
 */
public final class HouseMapper {

    private HouseMapper() {
    }

    public static HouseResponse toResponse(House house) {
        if (house == null) {
            return null;
        }
        Property property = house.getProperty();
        return new HouseResponse(
                house.getPublicId(),
                house.getNumber(),
                house.getFloor(),
                house.getStatus(),
                house.getCurrentMonthlyRent(),
                property != null ? property.getPublicId() : null,
                property != null ? property.getName() : null,
                house.getDateCreated(),
                house.getDateModified()
        );
    }
}
