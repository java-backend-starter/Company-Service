package com.company.www.constant.work;

import com.company.www.entity.staff.Staff;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
    FIRST("1급"),
    SECOND("2급"),
    THIRD("3급"),
    FOURTH("4급"),
    FIFTH("5급"),
    DISPLAY("공람");

    private final String code;
}
