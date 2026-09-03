package com.castle.property.repository;

import com.castle.property.entity.PaymentMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Set;

public interface PaymentMonthRepository extends JpaRepository<PaymentMonth, Long> {

    int countByMonthAndBlockNameIgnoreCase(LocalDate month, String blockName);

    @Query("select pm.blockName from PaymentMonth pm where pm.month = ?1")
    Set<String> getBlockNameDistinctByMonth(LocalDate paymentMonth);

    @Query("select pm.revisionCount from PaymentMonth pm where pm.month = ?1 and pm.blockName = ?2")
    Set<Integer> getRevisionDistinctByMonth(LocalDate paymentMonth, String blockName);
}
