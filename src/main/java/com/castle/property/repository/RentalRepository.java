package com.castle.property.repository;

import com.castle.property.entity.House;
import com.castle.property.entity.Person;
import com.castle.property.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByHouseAndPerson(House house, Person person);

    Optional<Rental> findByPublicId(UUID publicId);
}
