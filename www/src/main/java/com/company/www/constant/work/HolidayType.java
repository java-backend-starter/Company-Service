package com.company.www.constant.work;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HolidayType {
    ANNUAL("연차"),
    REWORD("보상"),
    FAMILY_CARE("가족돌봄"),
    MISCARRIAGE_STILLBIRTH("유산&사산"),
    CHILDBIRTH("출산"),
    SPOUSE_CHILDBIRTH("배우자 출산"),
    FERTILITY_TREATMENT("난임치료"),
    OFFICIAL("공가"),
    SICK("병가"),
    FAMILY_EVENT("경조사"),
    SUMMER("여름휴가"),
    OTHER("기타");

    private final String code;
}
