package com.castle.property.entity;

import com.castle.property.datatype.CounterType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Entity
@Table(name = "counters")
@SQLRestriction("deleted = false")
public class Counter extends AbstractAuditableActivityEntity {

    @Column(name = "current")
    private Integer current;

    @Enumerated(EnumType.STRING)
    @Column(name = "counter_type")
    private CounterType counterType;

    public Counter(CounterType counterType) {
        this.current = 10000;
        this.counterType = counterType;
    }

    public Counter() {

    }
}
