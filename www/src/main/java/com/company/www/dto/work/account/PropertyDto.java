package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PropertyDto {
    private String name;
    private String type;
    private String detailedType;
    private String statement;
    private long amount;
    private LocalDate closingStart;
    private LocalDate closingEnd;
}
