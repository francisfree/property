package com.castle.property.repository;

import com.castle.property.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByPublicId(UUID publicId);

    Optional<Person> findById(Long id);

    Optional<Person> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndOtherNameIgnoreCaseAndPhoneNumberIgnoreCase(String firstname, String lastname, String otherName, String phoneNumber);
}
