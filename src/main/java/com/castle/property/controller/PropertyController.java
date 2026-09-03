package com.castle.property.controller;

import com.castle.property.dto.PagedResponse;
import com.castle.property.dto.PropertyRequest;
import com.castle.property.dto.PropertyResponse;
import com.castle.property.entity.Property;
import com.castle.property.mapper.PropertyMapper;
import com.castle.property.service.PropertyService;
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
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public PagedResponse<PropertyResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search
    ) {
        Page<Property> result = propertyService.getProperties(search, PageRequest.of(page, size));
        return PagedResponse.of(result.map(PropertyMapper::toResponse));
    }

    @GetMapping("/list")
    public List<PropertyResponse> listAll() {
        return propertyService.listProperties().stream()
                .map(PropertyMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public PropertyResponse get(@PathVariable UUID id) {
        return PropertyMapper.toResponse(propertyService.getProperty(id));
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PropertyMapper.toResponse(propertyService.createProperty(request)));
    }

    @PutMapping("/{id}")
    public PropertyResponse update(@PathVariable UUID id, @Valid @RequestBody PropertyRequest request) {
        return PropertyMapper.toResponse(propertyService.updateProperty(id, request));
    }
}