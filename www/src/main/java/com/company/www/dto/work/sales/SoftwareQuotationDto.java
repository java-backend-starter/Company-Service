package com.company.www.dto.work.sales;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SoftwareQuotationDto {
    private String name;
    private String type;
    private String location;
    private String contact;

    private String quotation;
    private LocalDate date;
}
