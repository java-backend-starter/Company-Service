package com.company.www.entity.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="budget")
@Getter
@Setter
public class Budget {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="budget_id")
    private Long budgetId;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;

    @Column
    private long initial;

    @Column
    private long additional;

    @Column
    private String statement;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    // 추경 예산
    @OneToMany(mappedBy="budget")
    private List<AdditionalBudget> additionalBudget;

    // 예산 계획
    @OneToMany(mappedBy="budget")
    private List<BudgetPlan> budgetPlan;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

}
