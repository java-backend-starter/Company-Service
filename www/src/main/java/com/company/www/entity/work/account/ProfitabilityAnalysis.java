package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="profitability_analysis")
@Getter
@Setter
public class ProfitabilityAnalysis {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="profitability_analysis_id")
    private Long profitabilityAnalysisId;

    @Column(name="gross_asset")
    private long grossAsset; // 총자산

    @Column(name="owner_capital")
    private long ownerCapital; // 자기자본

    // 매출순이익률 : 당기순이익/매출액
    @Column(name="net_income_rate")
    private double netIncomeRate;

    // 매출총이익률 = 매출총이익/매출액 = (매출액 - 매출원가)/매출액
    @Column(name="gross_margin_rate")
    private double grossMarginRate; 

    // 매출영업이익률 = 영업이익/매출액 = (매출액 - 매출원가 - 영업비용)/매출액
    @Column(name="operating_profit_rate")
    private double operatingProfitRate;

    // 총자산순이익률 = 당기순이익/총자산
    @Column(name="gross_capital_rate")
    private double grossCapitalRate;

    // 자기자본순이익률 = 당기순이익/자기자본 = 당기순이익/(자본금 + 이익잉여금)
    @Column(name="return_on_equity")
    private double returnOnEquity;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;
}
