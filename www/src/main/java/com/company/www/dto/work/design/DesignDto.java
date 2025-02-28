package com.company.www.dto.work.design;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DesignDto {
    private String project;
    private String phase;
    private String subject;
    private String status;
    private LocalDate complete;
    private LocalDate expire;
    private String statement;
    private String appraisal;
}
