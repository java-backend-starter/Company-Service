package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="property")
@Getter
@Setter
public class Property {
    @Id
    @Column(name="property_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long propertyId;

    @Column
    private String name;

    @Column
    private String type;

    @Column(name="detailed_type")
    private String detailedType;

    @Column
    private String statement;

    @Column
    private long amount;

    @Column(name="closing_start")
    private LocalDate closingStart;

    @Column(name="closing_end")
    private LocalDate closingEnd;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
