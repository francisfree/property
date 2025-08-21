package com.castle.property.service;

import com.castle.property.dto.RentalPaymentActionRequest;
import com.castle.property.dto.RentalPaymentFilterRequest;
import com.castle.property.dto.RentalPaymentRequest;
import com.castle.property.entity.RentalPayment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface RentalPaymentService {
    RentalPayment createRentalPayment(@Valid RentalPaymentRequest request);

    BigDecimal getRentalBalance(@NotNull UUID rentalPublicId, @NotNull LocalDate paymentMonth);

    RentalPayment rentalPaymentActions(@NotNull UUID rentalPaymentPublicId, @Valid RentalPaymentActionRequest request);

    RentalPayment getRentalPayment(@NotNull UUID rentalPaymentPublicId);

    RentalPayment getRentalPaymentById(String rowKey);

    Page<RentalPayment> getRentalPayments(RentalPaymentFilterRequest rentalPaymentFilterRequest, Pageable pageable);

    Number getRentalPaymentsCount(RentalPaymentFilterRequest rentalPaymentFilterRequest);

}
