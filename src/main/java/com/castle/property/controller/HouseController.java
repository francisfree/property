package com.castle.property.controller;

import com.castle.property.dto.HouseActionRequest;
import com.castle.property.dto.HouseRequest;
import com.castle.property.dto.HouseResponse;
import com.castle.property.dto.PagedResponse;
import com.castle.property.entity.House;
import com.castle.property.mapper.HouseMapper;
import com.castle.property.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @GetMapping
    public PagedResponse<HouseResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID propertyId
    ) {
        Page<House> result = houseService.getHouses(search, propertyId, PageRequest.of(page, size));
        return PagedResponse.of(result.map(HouseMapper::toResponse));
    }

    @GetMapping("/list")
    public List<HouseResponse> listAll(
            @RequestParam(required = false) UUID propertyId
    ) {
        return houseService.listHouses(propertyId).stream()
                .map(HouseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public HouseResponse get(@PathVariable UUID id) {
        return HouseMapper.toResponse(houseService.getHouse(id));
    }

    @PostMapping
    public ResponseEntity<HouseResponse> create(@Valid @RequestBody HouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HouseMapper.toResponse(houseService.createHouse(request)));
    }

    @PutMapping("/{id}")
    public HouseResponse update(@PathVariable UUID id, @Valid @RequestBody HouseRequest request) {
        return HouseMapper.toResponse(houseService.updateHouse(id, request));
    }

    @PutMapping("/{id}/actions")
    public HouseResponse actions(@PathVariable UUID id, @Valid @RequestBody HouseActionRequest request) {
        return HouseMapper.toResponse(houseService.houseActions(id, request));
    }
}