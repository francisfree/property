package com.castle.property.service;

import com.castle.property.PropertyApplicationTests;
import com.castle.property.datatype.PaymentMode;
import com.castle.property.dto.RentalPaymentRequest;
import com.castle.property.entity.RentalPayment;
import com.github.javafaker.Faker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class RentalPaymentServiceTests extends PropertyApplicationTests {

    @Autowired
    private RentalPaymentService rentalPaymentService;

    private final Faker faker = new Faker();

    @Test(expected = jakarta.validation.ConstraintViolationException.class)
    public void createRentalPaymentFailsFieldsInvalid() {

        RentalPaymentRequest request = new RentalPaymentRequest();
        request.setRentalPublicId(UUID.fromString("5aafba44-72ce-11f0-8de9-0242ac120002"));
//        request.setAmount(new BigDecimal("2000"));

        rentalPaymentService.createRentalPayment(request);

    }

    @Test
    public void createRentalWorks() {
        RentalPaymentRequest request = new RentalPaymentRequest();
        request.setRentalPublicId(UUID.fromString("0deccb69-3904-4687-8006-53525d82b4a3"));
        request.setAmount(new BigDecimal("2000"));
        request.setPaymentDate(LocalDate.of(2024, 6, 6));
        request.setPaymentMonth(LocalDate.of(2024, 6, 6));
        request.setPaymentMode(PaymentMode.Cash);
        RentalPayment savedRentalPayment = rentalPaymentService.createRentalPayment(request);

    }


    @Test(expected = com.castle.property.application.config.exception.ApplicationOperationException.class)
    public void createRentalPaymentFailsAmountGreaterThanBalance() {
        RentalPaymentRequest request = new RentalPaymentRequest();
        request.setRentalPublicId(UUID.fromString("0deccb69-3904-4687-8006-53525d82b4a3"));
        request.setAmount(new BigDecimal("2000"));
        request.setPaymentDate(LocalDate.of(2024, 7, 10));
        request.setPaymentMonth(LocalDate.of(2024, 7, 1));
        request.setPaymentMode(PaymentMode.Cash);
        RentalPayment savedRentalPayment = rentalPaymentService.createRentalPayment(request);

    }

//    @Test
//    public void searchRentalWorks() {
//        RentalFilterRequest rentalFilterRequest = new RentalFilterRequest();
//        rentalFilterRequest.setSearchParam("1B");
//        List<Rental> rentals = rentalPaymentService.getRentals(rentalFilterRequest, PageRequest.of(0, 10)).getContent();
//        MatcherAssert.assertThat(rentals.size(), greaterThanOrEqualTo(1));
//        MatcherAssert.assertThat(rentals.get(0).getHouse().getNumber(), containsStringIgnoringCase(rentalFilterRequest.getSearchParam()));
//    }

}
