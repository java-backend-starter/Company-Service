package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ActualCostDto {
    private String type;
    private String item;
    private String grade;
    private LocalDate apply;

    private long ActualCost;
    private long difference;
    private String statement;
    private LocalDate date;
}
