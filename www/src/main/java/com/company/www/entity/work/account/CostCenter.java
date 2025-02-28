package com.company.www.entity.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Performance;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="cost_center")
@Getter
@Setter
public class CostCenter {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="cost_center_id")
    private Long costCenterId;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="budget_id")
    private Budget budget;

    @Column
    private long initial;

    @Column
    private long additional;

    @Column
    private long used;

    @Column(name="other_cost")
    private long otherCost;
    
    // 실적 테이블 연관관계 매핑
    @OneToMany(mappedBy="costCenter")
    private List<Performance> performance;

    @OneToMany(mappedBy="costCenter")
    private List<InnerOrder> innerOrder;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}