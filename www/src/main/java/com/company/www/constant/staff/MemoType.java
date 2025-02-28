package com.company.www.constant.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemoType {
    WORK("WORK"),
    SCHEDULE("SCHEDULE"),
    AND_SO_ON("AND_SO_ON");

    private final String code;
}
