package com.castle.property.service;

import com.castle.property.PropertyApplicationTests;
import com.castle.property.datatype.Floor;
import com.castle.property.dto.HouseRequest;
import com.castle.property.entity.House;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class HouseServiceTests extends PropertyApplicationTests {

    @Autowired
    private HouseService houseService;

    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void createHouseFailsRecordExist() {
        HouseRequest request = new HouseRequest();
        request.setNumber("1A");
        request.setFloor(Floor.FLOOR_1);
        request.setPropertyPublicId(UUID.fromString("b9c962be-b94b-42fe-8eee-c7730e5a335a"));

        House savedHouse = houseService.createHouse(request);

    }

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void createHouseFailsFieldsInvalid() {
        HouseRequest request = new HouseRequest();
        request.setNumber(RandomStringUtils.randomAlphabetic(260));
        request.setFloor(Floor.FLOOR_1);
        request.setPropertyPublicId(UUID.fromString("b9c962be-b94b-42fe-8eee-c7730e5a335a"));

        House savedHouse = houseService.createHouse(request);

    }

    @Test
    public void createHouseWorks() {
        HouseRequest request = new HouseRequest();
        request.setNumber("2A");
        request.setFloor(Floor.FLOOR_2);
        request.setPropertyPublicId(UUID.fromString("b9c962be-b94b-42fe-8eee-c7730e5a335a"));

        House savedHouse = houseService.createHouse(request);

    }

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void updateHouseFailsMissingValues() {
        HouseRequest request = new HouseRequest();
        request.setNumber(RandomStringUtils.randomAlphabetic(260));
        request.setFloor(Floor.FLOOR_1);
        request.setPropertyPublicId(UUID.fromString("b9c962be-b94b-42fe-8eee-c7730e5a335a"));

        House savedHouse = houseService.updateHouse(null, request);

    }

    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void updateHouseFailsInvalidPublicId() {
        HouseRequest request = new HouseRequest();
        request.setNumber("1A");
        request.setFloor(Floor.FLOOR_1);
        request.setPropertyPublicId(UUID.fromString("b9c962be-b94b-42fe-8eee-c7730e5a335a"));

        House savedHouse = houseService.updateHouse(UUID.randomUUID(), request);

    }

    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void updateHouseFailsRecordExist() {
        HouseRequest request = new HouseRequest();
        request.setNumber("1A");
        request.setFloor(Floor.FLOOR_1);
        request.setPropertyPublicId(UUID.fromString("b9c962be-b94b-42fe-8eee-c7730e5a335a"));

        House savedHouse = houseService.updateHouse(UUID.randomUUID(), request);

    }

    @Test
    public void updateHouseWorks() {
        HouseRequest request = new HouseRequest();
        request.setNumber("4A");
        request.setFloor(Floor.FLOOR_1);
        request.setPropertyPublicId(UUID.fromString("b9c962be-b94b-42fe-8eee-c7730e5a335a"));

        House savedHouse = houseService.updateHouse(UUID.fromString("155eeb98-1dd0-11f0-9cd2-0242ac120002"), request);

    }

    @Test
    public void listHouseWorks() {
        List<House> houses = houseService.listHouses();
        MatcherAssert.assertThat(houses.size(), greaterThanOrEqualTo(1));
    }
}
