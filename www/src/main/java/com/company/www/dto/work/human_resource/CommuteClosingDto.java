package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommuteClosingDto {
    private String staff;
    private String userId;
    private String monthly;
    private int attendance;
    private int absence;
    private int lateness;
    private double holiday;
}
