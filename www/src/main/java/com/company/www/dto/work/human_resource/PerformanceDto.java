package com.company.www.dto.work.human_resource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceDto {
    private String staff;
    private String userId;
    private String name;
    private String workType;
    private String plan;
    private String archive;
    private int ratio;
    private String performanceStatement;
    private String checkStaffStatement;
}
