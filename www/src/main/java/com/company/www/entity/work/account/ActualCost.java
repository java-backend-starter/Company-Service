package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="actual_cost")
@Getter
@Setter
public class ActualCost {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="actual_cost_id")
    private Long actualCostId;

    @Column
    private long actualCost; // 실제로 들어간 직접인건비, 일일 단위로 계산

    @Column
    private int actualPeriod; // 실제 소요기간

    @Column
    private long differenceCost; // 차액

    @Column
    private int differencePeriod; // 시간 차이

    @Column
    private String statement;

    @Column
    private LocalDate date; // 실제원가 계산일자

    @OneToOne
    @JoinColumn(name="unit_id")
    private Unit unit;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
