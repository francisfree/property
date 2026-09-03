package com.castle.property.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;


@Getter
@Setter
@Entity
@Table(name = "payment_weekly_entry")
@SQLRestriction("deleted = false")
public class PaymentWeeklyEntry extends AbstractAuditableActivityEntity {

    @ManyToOne
    @JoinColumn(name = "payment_month_id")
    private PaymentMonth paymentMonth;

    @Column(name = "week_count")
    private Integer weekCount;

    @Column(name = "week_name")
    private String weekName;

    @Column(name = "cash")
    private String cash;

    @Column(name = "till")
    private String till;

    @Column(name = "mpesa")
    private String mpesa;

}
