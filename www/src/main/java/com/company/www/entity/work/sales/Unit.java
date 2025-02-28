package com.company.www.entity.work.sales;

import com.company.www.entity.work.account.StandardCost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="unit")
@Getter
@Setter
public class Unit {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="unit_id")
    private Long unitId;

    @ManyToOne
    @JoinColumn(name="standard_cost_id")
    private StandardCost standardCost;

    @Column
    private int quantity;

    @Column
    private int period; // 투입 기간(단위 : 일)

    @Column
    private String task;

    @Column
    private String developQuotation;

}
