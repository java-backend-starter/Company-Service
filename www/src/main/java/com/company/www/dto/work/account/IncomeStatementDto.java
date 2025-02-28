package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class IncomeStatementDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private long sales;
    private long salesCost;
    private long operatingExpense;
    private long financialEarning;
    private long financialExpense;
    private long corporateTax;
}
