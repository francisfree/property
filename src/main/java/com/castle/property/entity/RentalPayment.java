package com.castle.property.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rental_payments")
@SQLRestriction("deleted = false")
public class RentalPayment extends AbstractAuditableActivityEntity {

    @Column(name = "revision_count")
    private Integer revisionCount;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "entry_month")
    private LocalDate month;

    @Column(name = "block_name")
    private String blockName;

    @Column(name = "house_no")
    private String houseNumber;

    @Column(name = "occupant_name")
    private String occupantName;

    @Column(name = "occupant_phone_number")
    private String occupantPhoneNumber;

    @Column(name = "rent_current_month")
    private String rentCurrentMonth;

    @Column(name = "rent_previous_month")
    private String rentPreviousMonth;

    @Column(name = "arrears_b_f")
    private String arrearsBroughtForward;

    @OneToMany(mappedBy = "rentalPayment", cascade = CascadeType.PERSIST)
    @OrderBy("id")
    private List<PaymentWeeklyEntry> weeklyEntries = new ArrayList<>();

    @Column(name = "total_payment")
    private String totalPayment;

    @Column(name = "previous_water_unit")
    private String previousWaterUnit;

    @Column(name = "current_water_unit")
    private String currentWaterUnit;

    @Column(name = "price_per_unit")
    private String pricePerUnit;

    @Column(name = "units_consumed")
    private String unitsConsumed;

    @Column(name = "water_bill")
    private String waterBill;

    public String getTotalPayment() {
        try {
            if (totalPayment != null) {
                BigDecimal userAmount = new BigDecimal(totalPayment);
                if (userAmount.compareTo(BigDecimal.ZERO) > 0) {
                    return totalPayment;
                }
            }
        } catch (Exception ignore) {
        }
        BigDecimal totalPaymentAmount = weeklyEntries.stream()
                .map(PaymentWeeklyEntry::getTotalWeeklyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPaymentAmount.toString();
    }
}
