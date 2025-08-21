package com.castle.property.repository;

import com.castle.property.entity.Rental;
import com.castle.property.entity.RentalPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalPaymentRepository extends JpaRepository<RentalPayment, Long> {

    Optional<RentalPayment> findByPublicId(UUID publicId);

    List<RentalPayment> findByRentalAndPaymentMonth(Rental rental, LocalDate paymentMonth);
}
