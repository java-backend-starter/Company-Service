package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Grade {
    EXCELLENT("EXCELLENT"),
    VERY_GOOD("VERY_GOOD"),
    GOOD("GOOD"),
    NEEDED_IMPROVEMENT("NEEDED_IMPROVEMENT"),
    UNSATISFACTORY("UNSATISFACTORY"),
    NOT_RELEVANT("NOT_RELEVANT");

    private final String code;
}
