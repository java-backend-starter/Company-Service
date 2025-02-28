package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkState {
    PENDING("결재대기"),
    HANDLING("결재중"),
    COMPLETED("결재완료"),
    DISPLAY("공람"),
    OVERDUE("보존년한 만기"),
    DELETE("삭제");

    private final String code;
}
