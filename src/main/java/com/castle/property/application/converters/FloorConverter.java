package com.castle.property.application.converters;

import com.castle.property.datatype.Floor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FloorConverter implements AttributeConverter<Floor, String> {

    @Override
    public String convertToDatabaseColumn(Floor floor) {
        return (floor != null) ? floor.label : null;
    }

    @Override
    public Floor convertToEntityAttribute(String dbData) {
        try {
            return Floor.forValue(dbData);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}