package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="corporate_tax")
@Getter
@Setter
public class CorporateTax {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="corporate_tax_id")
    private Long corporateTaxId;

    @Column
    private long base;

    @Column(name="deficit_carried_forward")
    private long deficitCarriedForward;

    @Column(name="tax_free")
    private long taxFree;

    @Column
    private long tax;

    @Column
    private LocalDate date;
}
