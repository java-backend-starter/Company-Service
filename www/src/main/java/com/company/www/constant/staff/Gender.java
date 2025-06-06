package com.company.www.constant.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String code;
}