package com.castle.property.entity;

import com.castle.property.application.converters.FloorConverter;
import com.castle.property.datatype.Floor;
import com.castle.property.datatype.HouseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "houses")
@SQLRestriction("deleted = false")
public class House extends AbstractAuditableActivityEntity {

    @Column(name = "house_number")
    private String number;

//    @Enumerated(EnumType.STRING)
    @Column(name = "floor")
    @Convert(converter = FloorConverter.class)
    private Floor floor;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private HouseStatus status;

    @Column(name = "current_monthly_rent")
    private BigDecimal currentMonthlyRent;
}
