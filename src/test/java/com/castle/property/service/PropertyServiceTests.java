package com.castle.property.service;

import com.castle.property.PropertyApplicationTests;
import com.castle.property.dto.PropertyRequest;
import com.castle.property.entity.Property;
import org.apache.commons.lang3.RandomStringUtils;
import static org.hamcrest.Matchers.*;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public class PropertyServiceTests extends PropertyApplicationTests {

    @Autowired
    private PropertyService propertyService;

    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void createPropertyFailsRecordExist() {
        PropertyRequest request = new PropertyRequest();
        request.setArea("Nairobi");
        request.setLocation("Githurai");
        request.setName("Property 1");

        Property savedProperty = propertyService.createProperty(request);

    }

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void createPropertyFailsFieldsInvalid() {
        PropertyRequest request = new PropertyRequest();
        request.setArea(RandomStringUtils.randomAlphabetic(260));
        request.setLocation("");
        request.setName(RandomStringUtils.randomAlphabetic(260));

        Property savedProperty = propertyService.createProperty(request);

    }

    @Test
    public void createPropertyWorks() {
        PropertyRequest request = new PropertyRequest();
        request.setArea("Nairobi 11");
        request.setLocation("Githuri BB");
        request.setName("Property 1");

        Property savedProperty = propertyService.createProperty(request);

    }

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void updatePropertyFailsMissingValues() {
        PropertyRequest request = new PropertyRequest();
        request.setArea(RandomStringUtils.randomAlphabetic(260));
        request.setLocation("");
        request.setName(RandomStringUtils.randomAlphabetic(260));

        Property savedProperty = propertyService.updateProperty(null, request);

    }

    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void updatePropertyFailsInvalidPublicId() {
        PropertyRequest request = new PropertyRequest();
        request.setArea("Thika");
        request.setLocation("CBD/Town");
        request.setName("Property 2");

        Property savedProperty = propertyService.updateProperty(UUID.randomUUID(), request);

    }

    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void updatePropertyFailsRecordExist() {
        PropertyRequest request = new PropertyRequest();
        request.setArea("Nairobi");
        request.setLocation("Githurai");
        request.setName("Property 1");

        Property savedProperty = propertyService.updateProperty(UUID.randomUUID(), request);

    }

    @Test
    public void updatePropertyWorks() {
        PropertyRequest request = new PropertyRequest();
        request.setArea("Thika");
        request.setLocation("CBD/Town");
        request.setName("Property 2");

        Property savedProperty = propertyService.updateProperty(UUID.fromString("25e1fa0c-1dc9-11f0-9cd2-0242ac120002"), request);

    }

    @Test
    public void listPropertyWorks() {
        List<Property> properties = propertyService.listProperties();
        MatcherAssert.assertThat(properties.size(), greaterThanOrEqualTo(1));
    }

    @Test
    public void getPropertyWorks() {
        List<Property> properties = propertyService.getProperties(null, PageRequest.of(0, 20)).getContent();
        MatcherAssert.assertThat(properties.size(), greaterThanOrEqualTo(1));
    }

    @Test
    public void searchPropertyWorks() {
        List<Property> properties = propertyService.getProperties("Thika", PageRequest.of(0, 20)).getContent();
        MatcherAssert.assertThat(properties.size(), equalTo(1));
    }
}
