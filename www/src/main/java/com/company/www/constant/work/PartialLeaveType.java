package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartialLeaveType {
    EARLY("조퇴"),
    AM_HALF("오전 반차"),
    PM_HALF("오후 반차");

    private final String code;
}
