package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HolidayDto {
    private String staff;
    private String holidayType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String half;
    private String reason;
}
