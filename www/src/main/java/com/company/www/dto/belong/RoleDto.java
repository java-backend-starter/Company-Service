package com.company.www.dto.belong;

import com.company.www.entity.staff.Staff;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleDto {
    private String roleName;
    private List<Staff> staff;
}
