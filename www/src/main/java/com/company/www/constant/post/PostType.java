package com.company.www.constant.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostType {
    NOTICE("NOTICE"),
    NORMAL("NORMAL");

    private final String code;
}