package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="standard_cost")
@Getter
@Setter
public class StandardCost {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="standard_cost_id")
    private Long standardCostId;

    @Column
    private String type;

    @Column
    private String item;

    @Column
    private String grade;

    @Column
    private long baseCost; // 단위단가(단위 : 일) 제경비 : 직접인건비 * 1.1, 기술료 : (직접인건비 + 제경비) * 0.2

    @Column
    private String statement;

    @Column
    private LocalDate date; // 적용일자

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
