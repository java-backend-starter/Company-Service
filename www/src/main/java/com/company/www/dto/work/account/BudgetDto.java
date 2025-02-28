package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BudgetDto {
    private String department;
    private String section;
    private long initial;
    private long additional;
    private long used;
    private String statement;
    private LocalDate startDate;
    private LocalDate endDate;
}
