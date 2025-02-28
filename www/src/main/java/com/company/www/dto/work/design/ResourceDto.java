package com.company.www.dto.work.design;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResourceDto {
    private String subject;
    private LocalDate complete;
    private String statement;
    private String appraisal;
}
