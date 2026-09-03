package com.castle.property.mapper;

import com.castle.property.dto.PersonResponse;
import com.castle.property.entity.Person;

/**
 * Maps {@link Person} entities to {@link PersonResponse} DTOs.
 */
public final class PersonMapper {

    private PersonMapper() {
    }

    public static PersonResponse toResponse(Person person) {
        if (person == null) {
            return null;
        }
        return new PersonResponse(
                person.getPublicId(),
                person.getFirstName(),
                person.getLastName(),
                person.getOtherName(),
                person.getIdentificationType(),
                person.getIdentificationNumber(),
                person.getNationality(),
                person.getPhoneNumber(),
                person.getDateCreated(),
                person.getDateModified()
        );
    }
}
