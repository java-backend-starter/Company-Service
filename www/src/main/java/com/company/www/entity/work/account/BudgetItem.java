package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="budget_item")
@Getter
@Setter
public class BudgetItem {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="budget_item_id")
    private Long budgetItemId;

    @Column
    private String type;

    @Column
    private String item;

    @Column
    private long required;

    @Column
    private String statement;

    @ManyToOne
    @JoinColumn(name="budget_plan_id")
    private BudgetPlan budgetPlan;
}
