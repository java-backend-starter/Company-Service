package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalPosition {
    CHAIRMAN(140, "회장", "대표"), // 회장
    VICE_CHAIRMAN(130, "부회장", "대표"), // 부회장
    PRESIDENT(120, "사장", "대표"), // 사장
    VICE_PRESIDENT(110, "부사장", "대표"), // 부사장
    SENIOR_MANAGING_DIRECTOR(100, "전무이사", "임원"), // 전무이사
    MANAGING_DIRECTOR(90, "상무이사", "임원"), // 상무이사
    DIRECTOR(80, "이사", "임원"), // 이사
    GENERAL_MANAGER(70, "부장", "중간관리자"), // 부장
    DEPUTY_GENERAL_MANAGER(60, "차장", "중간관리자"), // 차장
    MANAGER(50, "과장", "중간관리자"), // 과장
    ASSISTANT_MANAGER(40, "대리", "실무진"), // 대리
    ASSOCIATE(30, "주임", "실무진"), // 주임
    STAFF(20, "사원", "실무진"), // 사원
    INTERN(10, "인턴", "실무진"), // 인턴
    NOT_POSITION(0, "미정", "미정");

    private final int code;
    private final String position;
    private final String group;
}
