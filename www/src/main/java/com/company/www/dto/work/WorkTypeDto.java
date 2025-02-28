package com.company.www.dto.work;

import com.company.www.entity.belong.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WorkTypeDto {
    private Long workTypeId;
    private Role role;
    private String workName;
    private String workLink;
}