package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalState {
    FINALIZATION("최종결재"),
    APPROVE("결재"),
    DEFER("보류"),
    REJECT("반려"),
    ARBITRARY("전결"),
    PROXY("대결"),
    ARBITRARY_PROXY("전대결");

    private final String code;
}