package com.castle.property.service;

import com.castle.property.PropertyApplicationTests;
import com.castle.property.datatype.IdentificationType;
import com.castle.property.dto.RentalFilterRequest;
import com.castle.property.dto.RentalRequest;
import com.castle.property.entity.Rental;
import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class RentalServiceTests extends PropertyApplicationTests {

    @Autowired
    private RentalService rentalService;

    private final Faker faker = new Faker();

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void createRentalFailsFieldsInvalid() {

        RentalRequest request = new RentalRequest();
        request.setHousePublicId(UUID.fromString("5aafba44-72ce-11f0-8de9-0242ac120002"));
        request.setAmount(new BigDecimal("2000"));

        Rental savedRental = rentalService.createRental(request);

    }

    @Test
    public void createRentalWorks() {
        RentalRequest request = new RentalRequest();
        request.setFirstName(faker.name().firstName());
        request.setOtherName(faker.name().lastName());
        request.setLastName(faker.name().nameWithMiddle());
        request.setIdentificationNumber(faker.idNumber().valid());
        request.setIdentificationType(IdentificationType.NATIONAL_ID);
        request.setNationality("Kenyan");
        request.setPhoneNumber("07"+RandomStringUtils.randomNumeric(8));
        request.setHousePublicId(UUID.fromString("5aafba44-72ce-11f0-8de9-0242ac120002"));
        request.setAmount(new BigDecimal("2000"));

        Rental savedRental = rentalService.createRental(request);

    }


    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void createRentalFailsAlreadyExist() {

        RentalRequest request = new RentalRequest();
        request.setFirstName(faker.name().firstName());
        request.setOtherName(faker.name().lastName());
        request.setLastName(faker.name().nameWithMiddle());
        request.setIdentificationNumber(faker.idNumber().valid());
        request.setIdentificationType(IdentificationType.NATIONAL_ID);
        request.setNationality("Kenyan");
        request.setPhoneNumber("07"+RandomStringUtils.randomNumeric(8));
        request.setHousePublicId(UUID.fromString("a2976d3a-72cd-11f0-8de9-0242ac120002"));
        request.setAmount(new BigDecimal("2000"));

        Rental savedRental = rentalService.createRental(request);

        rentalService.createRental(request);

    }

    @Test
    public void searchRentalWorks() {
        RentalFilterRequest rentalFilterRequest = new RentalFilterRequest();
        rentalFilterRequest.setSearchParam("1B");
        List<Rental> rentals = rentalService.getRentals(rentalFilterRequest, PageRequest.of(0, 10)).getContent();
        MatcherAssert.assertThat(rentals.size(), greaterThanOrEqualTo(1));
        MatcherAssert.assertThat(rentals.get(0).getHouse().getNumber(), containsStringIgnoringCase(rentalFilterRequest.getSearchParam()));
    }


}
