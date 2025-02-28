package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalPosition {
    CHAIRMAN(140), // 회장
    VICE_CHAIRMAN(130), // 부회장
    PRESIDENT(120), // 사장
    VICE_PRESIDENT(110), // 부사장
    SENIOR_MANAGING_DIRECTOR(100), // 전무이사
    MANAGING_DIRECTOR(90), // 상무이사
    DIRECTOR(80), // 이사
    GENERAL_MANAGER(70), // 부장
    DEPUTY_GENERAL_MANAGER(60), // 차장
    MANAGER(50), // 과장
    ASSISTANT_MANAGER(40), // 대리
    ASSOCIATE(30), // 주임
    STAFF(20), // 사원
    INTERN(10),
    NOT_POSITION(0); // 인턴

    private final int code;
}
