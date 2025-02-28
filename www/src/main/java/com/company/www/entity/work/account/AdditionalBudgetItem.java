package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="additional_budget_item")
@Getter
@Setter
public class AdditionalBudgetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="additional_budget_item_id")
    private Long additionalBudgetItemId;

    @Column
    private String type;

    @Column
    private String item;

    @Column
    private long addition;

    @Column
    private String reason;

    @ManyToOne
    @JoinColumn(name="additional_budget_id")
    private AdditionalBudget additionalBudget;

}
