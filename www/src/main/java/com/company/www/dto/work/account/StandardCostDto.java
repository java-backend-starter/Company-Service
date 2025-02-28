package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StandardCostDto {
    private String type;
    private String item;
    private String grade;
    private long baseCost;
    private double task;
    private long cost;
    private String statement;
    private LocalDate date;
}
