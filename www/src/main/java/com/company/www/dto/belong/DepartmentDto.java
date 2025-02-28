package com.company.www.dto.belong;

import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DepartmentDto {
    private String departmentName;
    private String departmentEnglishName;
    private List<Section> sections;
    private List<Staff> staff;
}
