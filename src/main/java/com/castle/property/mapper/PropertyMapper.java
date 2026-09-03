package com.castle.property.mapper;

import com.castle.property.dto.PropertyResponse;
import com.castle.property.entity.Property;

/**
 * Maps {@link Property} entities to {@link PropertyResponse} DTOs.
 */
public final class PropertyMapper {

    private PropertyMapper() {
    }

    public static PropertyResponse toResponse(Property property) {
        if (property == null) {
            return null;
        }
        return new PropertyResponse(
                property.getPublicId(),
                property.getName(),
                property.getLocation(),
                property.getArea(),
                property.getDateCreated(),
                property.getDateModified()
        );
    }
}
