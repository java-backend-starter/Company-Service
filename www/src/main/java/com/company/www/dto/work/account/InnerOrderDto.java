package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InnerOrderDto {
    private String department;
    private String section;
    private String item;
    private String type;
    private long cost;
    private LocalDate date;
    private String statement;
}
