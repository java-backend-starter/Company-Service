package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RecruitDto {
    private String recruitName;
    private String role;
    private String position;
    private String qualification;
    private String preference;
    private int recruitNumber;
    private String employmentStatus;
    private long pay;
    private String place;
    private String process;
    private String document;
    private String means;
    private LocalDate startDate;
    private LocalDate endDate;
}
