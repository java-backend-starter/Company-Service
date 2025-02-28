package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalBudgetItemDto {
    private String [] type;
    private String [] item;
    private long [] addition;
    private String [] reason;
}
