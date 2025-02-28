package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="cash_flow_statement")
@Getter
@Setter
public class CashFlowStatement {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="cash_flow_statement_id")
    private Long cashFlowStatementId;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

    @Column
    private int stage;

    @Column
    private String statement;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy="cashFlowStatement")
    private List<CashFlow> cashFlow;

}
