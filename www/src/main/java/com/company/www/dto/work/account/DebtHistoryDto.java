package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DebtHistoryDto {
    private String name;
    private String patronType;
    private String location;
    private String contact;


    private LocalDate start;
    private String item;
    private long total;
    private LocalDate date;
    private long amount;
    private String status;
    private String statement;
}
