package com.company.www.dto.work.sales;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UnitDto {
    private String [] type;
    private String [] item;
    private String [] grade;
    private LocalDate [] date;
    private int [] quantity;
    private double [] manMonth;
    private String [] task;
}
