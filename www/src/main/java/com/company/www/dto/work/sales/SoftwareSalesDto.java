package com.company.www.dto.work.sales;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SoftwareSalesDto {
    private String softwareName;
    private String softwareType;
    private LocalDate developDate;
    private long price;

    private LocalDate salesDate;
    private int quantity;
    private long total;
}
