package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashFlowDataDto {
    private String [] type;
    private String [] detailedType;
    private long [] amount;
}
