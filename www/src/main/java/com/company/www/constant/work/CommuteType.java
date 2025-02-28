package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommuteType {
    COMMUTE("출근"),
    LATENESS("지각"),
    ABSENCE("결근");

    private final String code;
}
