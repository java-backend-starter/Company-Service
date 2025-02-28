package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProfitabilityAnalysisDto {
    private long grossAsset;
    private long ownerCapital;
    private LocalDate startDate;
    private LocalDate endDate;
}
