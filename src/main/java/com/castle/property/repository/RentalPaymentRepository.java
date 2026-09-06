package com.castle.property.repository;

import com.castle.property.entity.RentalPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface RentalPaymentRepository extends JpaRepository<RentalPayment, Long> {

    @Query("select coalesce(max(pm.revisionCount), 0) from RentalPayment pm where pm.month = ?1 and lower(pm.blockName) = lower(?2)")
    Integer countByMonthAndBlockNameIgnoreCase(LocalDate month, String blockName);

    @Query("select pm.blockName from RentalPayment pm where pm.month = ?1")
    Set<String> getBlockNameDistinctByMonth(LocalDate paymentMonth);

    @Query("select pm.revisionCount from RentalPayment pm where pm.month = ?1 and lower(pm.blockName) = lower(?2)")
    Set<Integer> getRevisionDistinctByMonth(LocalDate paymentMonth, String blockName);

    Optional<RentalPayment> findByPublicId(UUID publicId);
}
