package com.company.www.constant.no_use;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    SUPERVISE("SUPERVISE"),
    MANAGE("MANAGE"),
    SALES("SALES"),
    ACCOUNT("ACCOUNT"),
    FINANCE("FINANCE"),
    DESIGN("DESIGN"),
    HUMAN_RESOURCE("HUMAN_RESOURCE"),
    INFORMATION("INFORMATION"),
    NOT_ROLE("NOT_ROLE");

    private final String code;
}