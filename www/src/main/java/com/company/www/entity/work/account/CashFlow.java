package com.company.www.entity.work.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cash_flow")
@Getter
@Setter
public class CashFlow {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="cash_flow_id")
    private Long cashFlowId;

    @Column
    private String type;

    @Column(name="detailed_type")
    private String detailedType;

    @Column
    private long amount;

    @ManyToOne
    @JoinColumn(name="cash_flow_statement_id")
    private CashFlowStatement cashFlowStatement;
}
