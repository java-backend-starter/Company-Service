package com.company.www.entity.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="inner_order")
@Getter
@Setter
public class InnerOrder {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="inner_order_id")
    private Long innerOrderId;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;

    @Column
    private String item;

    @Column
    private String type;

    @Column
    private long cost;

    @Column
    private LocalDate date;

    @Column
    private String statement;

    @ManyToOne
    @JoinColumn(name="cost_center_id")
    private CostCenter costCenter;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
