package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BudgetPlanDto {
    private String staff;
    private String userId;
    private String department;
    private String section;
    private String team;
    private LocalDate startDate;
    private LocalDate endDate;
}
