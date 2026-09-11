package com.castle.property.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;


@Getter
@Setter
@Entity
@Table(name = "rental_payment_weekly")
@SQLRestriction("deleted = false")
public class RentalPaymentWeekly extends AbstractAuditableActivityEntity {

    @ManyToOne
    @JoinColumn(name = "rental_payment_id")
    private RentalPayment rentalPayment;

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

    @Column(name = "total_amount")
    private String totalAmount;

    public BigDecimal getTotalWeeklyAmount() {
        if (totalAmount != null) {
            return parseMoney(getTotalAmount());
        }
        return parseMoney(getCash())
                .add(parseMoney(getTill()))
                .add(parseMoney(getMpesa()));
    }

    private BigDecimal parseMoney(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public String getCash() {
        return cash == null ? "0.00" : cash.trim();
    }

    public String getTill() {
        return till == null ? "0.00" : till.trim();
    }

    public String getMpesa() {
        return mpesa == null ? "0.00" : mpesa.trim();
    }

    public String getTotalAmount() {
        return totalAmount == null ? "0.00" : totalAmount.trim();
    }
}
