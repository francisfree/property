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

    @Column(name = "rent")
    private String rent;

    @Column(name = "garbage")
    private String garbage;

    @Column(name = "total_rent_paid_previous_month")
    private String totalRentPaidPreviousMonth;

    @Column(name = "arrears_b_f")
    private String arrearsBroughtForward;

    @Column(name = "total_rent_due")
    private String totalRentDue;

    @Column(name = "arrears_c_f")
    private String arrearsCarriedForward;

    @OneToMany(mappedBy = "rentalPayment", cascade = CascadeType.PERSIST)
    @OrderBy("id")
    private List<RentalPaymentWeekly> weeklyEntries = new ArrayList<>();

    @Column(name = "total_rent_paid")
    private String totalRentPaid;

    @Column(name = "previous_water_unit")
    private String previousWaterUnit;

    @Column(name = "current_water_unit")
    private String currentWaterUnit;

    @Column(name = "units_consumed")
    private String unitsConsumed;

    @Column(name = "price_per_unit")
    private String pricePerUnit;

    @Column(name = "water_bill")
    private String waterBill;

    public String getTotalRentPaid() {
        try {
            if (totalRentPaid != null) {
                BigDecimal userValue = new BigDecimal(totalRentPaid);
                if (userValue.compareTo(BigDecimal.ZERO) > 0) {
                    return totalRentPaid;
                }
            }
        } catch (Exception ignore) {
        }
        BigDecimal totalPaymentAmount = weeklyEntries.stream()
                .map(RentalPaymentWeekly::getTotalWeeklyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPaymentAmount.toString();
    }
}
