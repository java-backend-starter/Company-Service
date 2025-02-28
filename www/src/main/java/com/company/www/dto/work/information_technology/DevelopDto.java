package com.company.www.dto.work.information_technology;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DevelopDto {
    private String project;
    private String phase;
    private String subject;
    private String status;
    private LocalDate complete;
    private LocalDate expire;
    private String statement;
    private String appraisal;

    // 파일
    // 종류 : 정리 예정

}
