package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingDto {
    private String name;
    private String content;
    private String statement;
    private int trainingDay;
    private LocalDate startDate;
    private LocalDate endDate;
}
