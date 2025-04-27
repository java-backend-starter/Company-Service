package com.company.www.converter;

import com.company.www.constant.staff.Gender;
import com.company.www.constant.work.*;
import com.company.www.entity.work.WorkType;
import com.company.www.repository.work.WorkTypeRepository;

import java.util.ArrayList;
import java.util.List;

public class Converter {
    // 성별
    public static Gender convetToGender(String gender){
        switch (gender){
            case "남자" -> {return Gender.MALE;}
            case "여자" -> {return Gender.FEMALE;}
            default -> throw new IllegalStateException("잘못된 데이터입니다.");
        }
    }

    public static String convertToGenderString(Gender gender){
        return gender.getCode();
    }

    // 결재 상태
    public static ApprovalState convertToApprovalState(String approvalState){
        if (approvalState == null) {
            return null;
        }

        return switch(approvalState.trim()){
            case "최종결재" -> ApprovalState.FINALIZATION;
            case "결재" -> ApprovalState.APPROVE;
            case "보류" -> ApprovalState.DEFER;
            case "반려" -> ApprovalState.REJECT;
            case "전결" -> ApprovalState.ARBITRARY;
            case "대결" -> ApprovalState.PROXY;
            case "전대결" -> ApprovalState.ARBITRARY_PROXY;
            default -> null;
        };

    }

    public static String convertToApprovalStateString(ApprovalState approvalState){
        return approvalState.getCode();
    }

    // 결재 상태
    public static WorkState convertToWorkState(String workState){
        switch (workState){
            case "결재대기" -> { return WorkState.PENDING; }
            case "결재중" -> { return  WorkState.HANDLING; }
            case "결재완료" -> { return WorkState.COMPLETED; }
            case "공람" -> { return WorkState.DISPLAY; }
            case "보존년한 만기" -> { return WorkState.OVERDUE; }
            case "삭제" -> { return WorkState.DELETE; }
            default -> { return null; }
        }
    }

    public static String convertToWorkStateString(WorkState workState){
        return workState.getCode();
    }

    // 결재 직급
    public static String convertToApprovalPositionString(ApprovalPosition approvalPosition){
        switch (approvalPosition){
            case NOT_POSITION -> { return "미정"; }
            case CHAIRMAN -> { return "회장"; }
            case VICE_CHAIRMAN -> { return "부회장"; }
            case PRESIDENT -> { return "사장"; }
            case VICE_PRESIDENT -> { return "부사장"; }
            case SENIOR_MANAGING_DIRECTOR -> { return "전무이사"; }
            case MANAGING_DIRECTOR -> { return "상무이사"; }
            case DIRECTOR -> { return "이사"; }
            case GENERAL_MANAGER -> { return "부장"; }
            case DEPUTY_GENERAL_MANAGER -> { return "차장"; }
            case MANAGER -> { return "과장"; }
            case ASSISTANT_MANAGER -> { return "대리"; }
            case ASSOCIATE -> { return "주임"; }
            case STAFF -> { return "사원"; }
            case INTERN -> { return "인턴";}
            default -> { return null; }
        }
    }

    // 휴가
    public static HolidayType convertToHolidayType(String holidayType){
        switch (holidayType){
            case "연차" -> { return HolidayType.ANNUAL; }
            case "보상" -> { return HolidayType.REWORD; }
            case "가족돌봄" -> { return HolidayType.FAMILY_CARE; }
            case "유산&사산" -> { return HolidayType.MISCARRIAGE_STILLBIRTH; }
            case "출산" -> { return HolidayType.CHILDBIRTH; }
            case "배우자 출산" -> { return HolidayType.SPOUSE_CHILDBIRTH; }
            case "난임치료" -> { return HolidayType.FERTILITY_TREATMENT; }
            case "공가" -> { return HolidayType.OFFICIAL; }
            case "병가" -> { return HolidayType.SICK; }
            case "경조사" -> { return HolidayType.FAMILY_EVENT; }
            case "여름휴가" -> { return HolidayType.SUMMER; }
            default -> { return HolidayType.OTHER; }
        }
    }

    public static String convertToHolidayTypeString(HolidayType holidayType){
        return holidayType.getCode();
    }
    
    // 출근 상태
    public static CommuteType convertToCommuteType(String commuteType){
        switch (commuteType){
            case "출근" -> { return CommuteType.COMMUTE; }
            case "지각" -> { return CommuteType.LATENESS; }
            case "결근" -> { return CommuteType.ABSENCE; }
            default ->  { return null; }
        }
    }
    public static String convertToCommuteTypeString(CommuteType commuteType){
        return commuteType.getCode();
    }

    // 업무와 부서명 제거
    public static String convertToRoleName(String name){
        return name.substring(0, name.length()-1);
    }

    // 세부 업무 제거
    public static List<WorkType> filter(WorkTypeRepository workTypeRepository, String ... types){
        List<WorkType> filtered = new ArrayList<>();
        for(String type : types ){
            filtered.add(workTypeRepository.findByWorkName(type));
        }
        return filtered;
    }

    public static String [] makeStringArray(String ... datas){
        return datas;
    }
}
