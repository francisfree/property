package com.castle.property.repository;

import com.castle.property.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    Optional<Property> findById(Long id);

    Optional<Property> findByPublicId(UUID publicId);

    Optional<Property> findByNameIgnoreCaseAndAreaIgnoreCaseAndLocationIgnoreCase(String name, String area, String location);

    @Query("select p from Property p order by p.dateCreated")
    Page<Property> getAllOrderByDateCreated(Pageable pageable);

    @Query("select p from Property p order by p.dateCreated")
    List<Property> listAllOrderByDateCreated();

}
