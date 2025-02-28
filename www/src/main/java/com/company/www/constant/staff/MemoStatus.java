package com.company.www.constant.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemoStatus {
    PLAN("PLAN"),
    DELAY("DELAY"),
    PROCEED("PROCEED"),
    COMPLETE("COMPLETE");

    private final String code;
}
