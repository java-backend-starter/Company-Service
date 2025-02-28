package com.company.www.constant.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentType {
    ORIGINAL("ORIGINAL"),
    REPLY("REPLY");

    private final String code;
}