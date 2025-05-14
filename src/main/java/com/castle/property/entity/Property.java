package com.castle.property.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@ToString
@Entity
@Table(name = "properties")
@SQLRestriction("deleted = false")
public class Property extends AbstractAuditableActivityEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "area")
    private String area;
}
