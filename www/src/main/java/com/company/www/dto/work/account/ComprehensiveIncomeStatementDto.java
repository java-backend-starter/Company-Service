package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ComprehensiveIncomeStatementDto {
    private long netIncome;
    private long otherIncome;
    private long totalIncome;
    private LocalDate startDate;
    private LocalDate endDate;
}
