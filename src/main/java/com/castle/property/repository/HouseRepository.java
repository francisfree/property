package com.castle.property.repository;

import com.castle.property.datatype.Floor;
import com.castle.property.entity.House;
import com.castle.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HouseRepository extends JpaRepository<House, Long> {
    Optional<House> findByPublicId(UUID publicId);

    Optional<House> findByNumberIgnoreCaseAndFloorAndProperty(String number, Floor floor, Property property);

}
