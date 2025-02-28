package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="activity_based_cost")
@Getter
@Setter
public class ActivityBasedCost {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="activity_based_cost_id")
    private Long activityBasedCostId;


}
