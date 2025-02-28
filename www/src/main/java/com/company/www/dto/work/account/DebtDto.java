package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DebtDto {
    private String name;
    private String patronType;
    private String location;
    private String contact;

    private String debtType; // 채권, 채무
    private String item;
    private String type;
    private long total;
    private LocalDate start;
    private LocalDate expire;
    private String statement;
}
