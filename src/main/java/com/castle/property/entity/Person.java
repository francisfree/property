package com.castle.property.entity;

import com.castle.property.datatype.IdentificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "persons")
@SQLRestriction("deleted = false")
public class Person extends AbstractAuditableActivityEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "other_name")
    private String otherName;

    @Column(name = "identification_number")
    private String identificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type")
    private IdentificationType identificationType;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Transient
    private String componentLabel;

    public String getComponentLabel() {
        return String.format("%s (%s %s %s)", phoneNumber, firstName, lastName, otherName);
    }
}
