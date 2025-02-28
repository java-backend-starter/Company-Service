package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CommuteDto {
    private String staff;
    private LocalDate absence;
    private LocalTime clockIn;
    private LocalTime clockOut;
    private String commuteType;
    private String commuteStatement;

}
