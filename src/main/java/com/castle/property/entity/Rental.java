package com.castle.property.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "rentals")
@SQLRestriction("deleted = false")
public class Rental extends AbstractAuditableActivityEntity {

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "house_id")
    private House house;
}
