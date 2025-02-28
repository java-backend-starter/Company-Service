package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PayDto {
    private String staff;
    private String userId;
    private int night;
    private int overtime;
    private int weekend;
    private int holiday;
    private LocalDate payDate;
    private String period;
    private long incomeTax;
}
