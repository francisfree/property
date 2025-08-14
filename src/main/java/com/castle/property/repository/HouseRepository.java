package com.castle.property.repository;

import com.castle.property.datatype.Floor;
import com.castle.property.entity.House;
import com.castle.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseRepository extends JpaRepository<House, Long> {
    Optional<House> findByPublicId(UUID publicId);

    Optional<House> findByNumberIgnoreCaseAndFloorAndProperty(String number, Floor floor, Property property);

    @Query("select h from House h where h.property.publicId = ?1 order by h.property.id, h.floor")
    List<House> findByPropertyPublicId(UUID propertyPublicId);

    @Query("select h from House h order by h.property.id, h.floor")
    List<House> findAllOrderByProperty();

}
