package com.company.www.entity.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="additional_budget")
@Getter
@Setter
public class AdditionalBudget {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="additional_budget_id")
    private Long additionalBudgetId;

    @ManyToOne
    @JoinColumn(name="staff_id")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;

    @OneToMany(mappedBy="additionalBudget")
    private List<AdditionalBudgetItem> additionalBudgetItem;

    @ManyToOne
    @JoinColumn(name="budget_id")
    private Budget budget;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
