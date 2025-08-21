package com.castle.property.repository;

import com.castle.property.datatype.RentalAccountStatus;
import com.castle.property.entity.House;
import com.castle.property.entity.Person;
import com.castle.property.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByHouseAndPerson(House house, Person person);

    Optional<Rental> findByPublicId(UUID publicId);

    @Query("select r from Rental r where r.house.property.publicId = ?1 and r.accountStatus = ?2")
    List<Rental> findByPropertyPublicIdAndRentalAccountStatus(UUID propertyPublicId, RentalAccountStatus rentalAccountStatus);
}
