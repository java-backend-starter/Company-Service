package com.company.www.dto.work.account;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssetHistoryDto {
    private String name;
    private String type;
    private String code;
    private String department;

    private LocalDate date;
    private int month;
    private long formerAccumulatedDepreciation;
    private long currentDepreciation;
    private long currentAccumulatedDepreciation;
    private long currentResidualValue;
}
