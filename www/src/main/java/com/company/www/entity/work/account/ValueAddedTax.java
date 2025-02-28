package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="value_added_tax")
@Getter
@Setter
public class ValueAddedTax {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="value_added_tax_id")
    private Long valueAddedTaxId;

    @Column
    private String item;

    @Column
    private int quantity;

    @Column
    private long base;

    @Column
    private long tax;

    @Column
    private LocalDate date;
}
