package com.company.www.constant.staff;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResignState {
    REQUEST("신청"),
    HANDLING("처리 중"),
    COMPLETED("처리 완료"),
    CANCELED("취소");

    private final String code;
}
