package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetItemDto {
    private String [] type;
    private String [] item;
    private long [] required;
    private String [] statement;
}
