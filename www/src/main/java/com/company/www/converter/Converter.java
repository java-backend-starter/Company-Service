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
        if (gender == null) {
            return null;
        }

        String trimmed = gender.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        for (Gender gd : Gender.values()) {
            if (gd.getCode().equals(trimmed)) {
                return gd;
            }
        }
        return null;

    }

    public static String convertToGenderString(Gender gender){
        return gender.getCode();
    }

    // 결재 상태
    public static ApprovalState convertToApprovalState(String approvalState){
        if (approvalState == null) {
            return null;
        }
        String trimmed = approvalState.trim();

        if(trimmed.isEmpty()){
            return null;
        }

        for (ApprovalState state : ApprovalState.values()) {
            if (state.getCode().equals(trimmed)) {
                return state;
            }
        }
        return null;
    }

    public static String convertToApprovalStateString(ApprovalState approvalState){
        return approvalState.getCode();
    }

    // 결재 상태
    public static WorkState convertToWorkState(String workState){
        if(workState == null){
            return null;
        }

        String trimmed = workState.trim();

        if(trimmed.isEmpty()){
            return null;
        }

        for(WorkState state : WorkState.values()){
            if(state.getCode().equals(trimmed)){
                return state;
            }
        }
        return null;
    }

    public static String convertToWorkStateString(WorkState workState){
        return workState.getCode();
    }

    // 결재 직급
    public static String convertToApprovalPositionString(ApprovalPosition approvalPosition){
        return approvalPosition.getPosition();
    }

    // 휴가
    public static HolidayType convertToHolidayType(String holidayType){
        if(holidayType == null){
            return null;
        }

        String trimmed = holidayType.trim();

        if(trimmed.isEmpty()){
            return null;
        }

        for(HolidayType type : HolidayType.values()){
            if(type.getCode().equals(trimmed)){
                return type;
            }
        }
        return HolidayType.OTHER;
    }

    public static String convertToHolidayTypeString(HolidayType holidayType){
        return holidayType.getCode();
    }
    
    // 출근 상태
    public static CommuteType convertToCommuteType(String commuteType){
        if(commuteType == null){
            return null;
        }

        String trimmed = commuteType.trim();

        if(trimmed.isEmpty()){
            return null;
        }

        for(CommuteType type : CommuteType.values()){
            if(type.getCode().equals(trimmed)){
                return type;
            }
        }
        return null;
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
