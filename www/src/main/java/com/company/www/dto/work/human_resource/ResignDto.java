package com.company.www.dto.work.human_resource;

import com.company.www.constant.staff.ResignState;
import com.company.www.entity.staff.Staff;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResignDto {
    private Staff resignStaff;
    private LocalDate requestDate;
    private LocalDate processDate;
    private LocalDate resignDate;
    private ResignState resignState;
}
