package com.castle.property.service;

import com.castle.property.dto.HouseActionRequest;
import com.castle.property.dto.HouseRequest;
import com.castle.property.entity.House;
import com.castle.property.entity.Property;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HouseService {
    House createHouse(@Valid HouseRequest request);

    House updateHouse(@NotNull UUID housePublicId, @Valid HouseRequest request);

    House houseActions(@NotNull UUID housePublicId, @Valid HouseActionRequest request);

    House getHouse(@NotNull UUID housePublicId);

    List<House> listHouses(UUID propertyPublicId);

    List<Property> listProperties();

    Page<House> getHouses(Pageable pageable);

    Page<House> getHouses(String searchParam, UUID propertyPublicId, Pageable pageable);

    House getHouseById(String rowKey);

    Long getHouseCount(String searchParam, UUID propertyPublicId);
}
