package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="comprehensive_income_statement")
@Getter
@Setter
public class ComprehensiveIncomeStatement {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="comprehensive_income_statement_id")
    private Long comprehensiveIncomeStatementId;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

    @OneToOne
    @JoinColumn(name="income_statement_id")
    private IncomeStatement incomeStatement;

    @Column(name="net_income")
    private long netIncome; // 당기 순이익

    @Column(name="other_income")
    private long otherIncome; // 기타 포괄손익

    @Column(name="total_income")
    private long totalIncome; // 총포괄손익

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;
}
