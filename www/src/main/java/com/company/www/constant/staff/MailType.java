package com.company.www.constant.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailType {
    SEND("SEND"),
    RECEIVE("RECEIVE"),
    FOR_ME("FOR_ME");

    private final String code;
}