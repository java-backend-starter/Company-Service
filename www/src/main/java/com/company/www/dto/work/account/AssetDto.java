package com.company.www.dto.work.account;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssetDto {
    private String subCode;
    private String name;
    private LocalDate date;
    private long price;
    private int quantity;
    private long residualValue;
    private String department;
    private String status;
}
