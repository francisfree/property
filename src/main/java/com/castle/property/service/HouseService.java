package com.castle.property.service;

import com.castle.property.dto.HouseRequest;
import com.castle.property.entity.House;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HouseService {
    House createHouse(@Valid HouseRequest request);

    House updateHouse(@NotNull UUID housePublicId, @Valid HouseRequest request);

    House getHouse(@NotNull UUID housePublicId);

    List<House> listHouses();

    Page<House> getHouses(Pageable pageable);
}
