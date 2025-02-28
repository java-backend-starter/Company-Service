package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Recruit {

    APPLY("APPLY"),
    CONSIDER("CONSIDER"),
    MIDDLE_PASS("MIDDLE_PASS"),
    FINAL_PASS("FINAL_PASS"),
    QUALIFY("QUALIFY"),
    DISQUALIFY("DISQUALIFY");

    private final String code;

}
