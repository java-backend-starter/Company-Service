package com.company.www.entity.work.account;

import com.company.www.entity.work.Work;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="income_statement")
@Getter
@Setter
public class IncomeStatement {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="income_statement_id")
    private Long incomeStatementId;

    @OneToOne
    @JoinColumn(name="work_id")
    private Work work;

    @Column(name="start_date")
    private LocalDate startDate;

    @Column(name="end_date")
    private LocalDate endDate;

    @Column
    private long sales; // 매출액

    @Column(name="sales_cost")
    private long salesCost; // 매출원가, 매출 총이익 : 매출액 - 매출원가

    @Column(name="operating_expense")
    private long operatingExpense; // 영업비용, 영업이익 : 매출 총이익 - 영업비용 = 매출액 - 매출원가 - 영업비용

    @Column(name="financial_earning")
    private long financialEarning; // 금융수익

    @Column(name="financial_expense")
    private long financialExpense; // 금융비용

    @Column(name="corporate_tax")
    private long corporateTax; // 법인세, 법인세비용 차감 전 순이익 : 영업이익(매출액 - 매출원가 - 영업비용) + 금융수익 - 금융비용, 당기순이익 : 법인세비용 차감 전 순이익 - 법인세

    // 매출총이익
    public long getGrossSales(){
        return (sales - salesCost);
    }

    // 영업이익
    public long getOperatingProfit(){
        return (sales - salesCost - operatingExpense);
    }

    // 법인세비용 차감 전 순이익
    public long getGrossProfit(){
        return (sales - salesCost - operatingExpense + financialEarning - financialExpense);
    }

    // 당기 순이익
    public long getNetIncome(){
        return (sales - salesCost - operatingExpense + financialEarning - financialExpense - corporateTax);
    }
}
