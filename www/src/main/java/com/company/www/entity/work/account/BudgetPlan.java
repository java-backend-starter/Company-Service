package com.company.www.entity.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="budget_plan")
@Getter
@Setter
public class BudgetPlan {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="budget_plan_id")
    private Long budgetPlanId;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column
    private long total;

    @OneToMany(mappedBy="budgetPlan")
    private List<BudgetItem> budgetItem;

    @ManyToOne
    @JoinColumn(name="budget_id")
    private Budget budget;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
