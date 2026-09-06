package com.castle.property.repository;

import com.castle.property.datatype.CounterType;
import com.castle.property.entity.Counter;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Counter c where c.counterType = ?1")
    Optional<Counter> findByCounterType(CounterType counterType);
}
