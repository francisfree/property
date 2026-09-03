package com.castle.property.repository;

import com.castle.property.entity.PaymentWeeklyEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentWeeklyEntryRepository extends JpaRepository<PaymentWeeklyEntry, Long> {
}
