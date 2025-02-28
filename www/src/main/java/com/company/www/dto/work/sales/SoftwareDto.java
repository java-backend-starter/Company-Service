package com.company.www.dto.work.sales;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SoftwareDto {
    private String softwareName;
    private String softwareType;
    private LocalDate developDate;
    private long price;
    private String statement;
    private String mainFunction;
}
