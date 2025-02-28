package com.company.www.constant.no_use;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Position {
    PRESIDENT("PRESIDENT"),
    EXECUTIVE_VICE_PRESIDENT("EXECUTIVE_VICE_PRESIDENT"),
    GENERAL_MANAGER("GENERAL_MANAGER"),
    DEPUTY_GENERAL_MANAGER("DEPUTY_GENERAL_MANAGER"),
    MANAGER("MANAGER"),
    ASSISTANT_MANAGER("ASSISTANT_MANAGER"),
    STAFF("STAFF"),
    TRAINEE("TRAINEE"),
    INTERN("INTERN"),
    NOT_POSITION("NOT_POSITION");

    private final String code;
}