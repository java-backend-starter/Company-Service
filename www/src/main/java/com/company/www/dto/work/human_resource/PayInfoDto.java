package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayInfoDto {
    private String role;
    private String position;
    private long base;
    private long night;
    private long overtime;
    private long weekend;
    private long holiday;
    private int year;
}
