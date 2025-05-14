package com.castle.property.service;

import com.castle.property.application.config.exception.ApplicationOperationException;
import com.castle.property.dto.HouseRequest;
import com.castle.property.entity.House;
import com.castle.property.entity.Property;
import com.castle.property.repository.HouseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private final HouseRepository houseRepository;
    private final PropertyService propertyService;

    @Override
    public House createHouse(@Valid HouseRequest request) {
        Property property = propertyService.getProperty(request.getPropertyPublicId());
        boolean present = houseRepository.findByNumberIgnoreCaseAndFloorAndProperty(request.getNumber(), request.getFloor(), property).isPresent();
        if (present) {
            throw new ApplicationOperationException("operation.record.exist");
        }
        House house = new House();
        house.setFloor(request.getFloor());
        house.setNumber(request.getNumber());
        house.setProperty(property);
        return houseRepository.save(house);
    }

    @Override
    public House updateHouse(@NotNull UUID housePublicId, @Valid HouseRequest request) {
        House house = getHouse(housePublicId);

        houseRepository.findByNumberIgnoreCaseAndFloorAndProperty(request.getNumber(), request.getFloor(), house.getProperty()).ifPresent(existingHouse -> {
            if (!ObjectUtils.nullSafeEquals(existingHouse.getId(), house.getId())) {
                throw new ApplicationOperationException("operation.record.exist");
            }
        });

        house.setFloor(request.getFloor());
        house.setNumber(request.getNumber());
        return houseRepository.save(house);
    }


    @Override
    public House getHouse(@NotNull UUID housePublicId) {
        return houseRepository.findByPublicId(housePublicId).orElseThrow(() -> new ApplicationOperationException("operation.record.not.found"));
    }

    @Override
    public List<House> listHouses() {
        return houseRepository.findAll();
    }

    @Override
    public Page<House> getHouses(Pageable pageable) {
        return houseRepository.findAll(pageable);
    }
}
