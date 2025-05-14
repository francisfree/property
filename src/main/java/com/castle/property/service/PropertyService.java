package com.castle.property.service;

import com.castle.property.dto.PropertyRequest;
import com.castle.property.entity.Property;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PropertyService {
    Property createProperty(@Valid PropertyRequest request);

    Property updateProperty(@NotNull UUID propertyPublicId, @Valid PropertyRequest request);

    Property getProperty(@NotNull UUID propertyPublicId);

    List<Property> listProperties();

    Page<Property> getProperties(String searchParam, Pageable pageable);

    Property getPropertyById(String rowKey);

    Long getPropertiesCount(String searchParam);
}
