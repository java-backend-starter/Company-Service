package com.company.www.dto.work.sales;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SoftwareOrderDto {

    private String quotation;
    private LocalDate date;

    private String status;
    private String payment;
    private LocalDate orderDate;
    private LocalDate provideDate;
    private String statement;

    private String name;
    private String type;
    private String location;
    private String contact;
}
