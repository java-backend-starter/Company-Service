package com.company.www.dto.work.sales;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditDto {
    private String name;
    private String type;
    private String location;
    private String contact;

    private String grade;
    private int score;
    private long totalPayable;
    private long paid;
    private long totalReceivable;
    private long received;
}
