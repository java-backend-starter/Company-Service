package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="tax_type")
@Getter
@Setter
public class TaxType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="tax_type_id")
    private Long taxTypeId;

    @Column(name="tax_name")
    private String taxName;

    @Column
    private double tax; // 퍼센트 기록, 100으로 나눔

    // 부가 정보 : 세율 적용구간, 공제액
    @Column
    private long minimum;

    @Column
    private long maximum;

    @Column
    private long deduction;

}
