package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EquityChangeStatementDto {
    private LocalDate startDate;
    private LocalDate endDate;
}
