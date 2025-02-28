package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="corporate_additional_tax")
@Getter
@Setter
public class CorporateAdditionalTax {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="corporate_additional_tax_id")
    private Long corporateAdditionalTaxId;

    @Column
    private String reason;

    @Column
    private long base;

    @Column
    private long tax;

    @Column
    private LocalDate date;
}
