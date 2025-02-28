package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AppraisalDto {
    private String staff;
    private String userId;
    private String result;
    private LocalDate date;
    private String contribution;
    private String judge;
    private String improvement;
    private String propulsion;
    private String resolution;
    private String accuracy;
    private String management;
    private String sincerity;
    private String sympathy;
    private String belonging;
    private String statement;
}
