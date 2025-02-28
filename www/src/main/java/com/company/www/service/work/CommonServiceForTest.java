package com.company.www.service.work;

import com.company.www.constant.work.CommuteType;
import com.company.www.constant.work.PartialLeaveType;
import com.company.www.converter.Converter;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Commute;
import com.company.www.entity.work.human_reasource.Holiday;
import com.company.www.repository.work.human_resource.CommuteRepository;
import com.company.www.repository.work.human_resource.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class CommonServiceForTest {
    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    CommuteRepository commuteRepository;

    // 휴가
    public Holiday createHoliday(Work work, String holidayType, String reason, LocalDate start, LocalDate end){
        Holiday holiday = new Holiday();
        holiday.setWork(work);
        holiday.setHolidayType(Converter.convertToHolidayType(holidayType));
        holiday.setReason(reason);
        holiday.setStartDate(start);
        holiday.setEndDate(end);
        return holidayRepository.save(holiday);
    }

    public Holiday updateHoliday(Holiday holiday, String holidayType, String reason, LocalDate start, LocalDate end, String half){
        holiday.setHolidayType(Converter.convertToHolidayType(holidayType));
        holiday.setReason(reason);
        holiday.setHalf(half);
        holiday.setStartDate(start);
        holiday.setEndDate(end);
        return holidayRepository.save(holiday);
    }

    public void displayHoliday(Holiday holiday){
        Period period = Period.between(holiday.getStartDate(), holiday.getEndDate());
        System.out.println("-----");
        System.out.println("휴가유형 : " + holiday.getHolidayType().getCode());
        System.out.println("휴가 시작일 : " + holiday.getStartDate().toString());
        System.out.println("휴가 종료일 : " + holiday.getEndDate().toString());
        System.out.println("휴가일수 : " + period.getYears() + "년 " + period.getMonths() + "월 " + (period.getDays() + 1) + "일");
        System.out.println("휴가 사유 : " + holiday.getReason());
        System.out.println("반차 여부 : " + holiday.getHalf());
        System.out.println("-----");
    }

    // 출퇴근 관리
    public boolean checkClockOut(LocalTime clockIn, LocalTime clockOut){
        return clockIn.isAfter(clockOut);
    }

    public Commute createClockIn(Staff staff, LocalTime clockIn){
        Commute commute = new Commute();
        commute.setStaff(staff);
        commute.setCommuteDate(LocalDate.now());
        commute.setClockIn(clockIn);
        return commuteRepository.save(commute);
    }

    public Commute createClockOut(Commute commute, LocalTime clockOut){
        if(checkClockOut(commute.getClockIn(), clockOut)){
            throw new DateTimeException("퇴근 시간은 출근 시간과 같거나 빠를 수 없습니다.");
        }
        commute.setClockOut(clockOut);
        return commuteRepository.save(commute);
    }

    public Commute updateCommute(Commute commute, LocalTime clockIn, LocalTime clockOut){
        if(clockIn != null){
            commute.setClockIn(clockIn);
        }

        if(clockOut != null) {
            if(checkClockOut(commute.getClockIn(), clockOut)){
                throw new DateTimeException("퇴근 시간은 출근 시간과 같거나 빠를 수 없습니다.");
            }
            commute.setClockOut(clockOut);
        }
        return commuteRepository.save(commute);
    }

    public void displayCommute(Commute commute){
        String name = commute.getStaff().getStaffName();
        System.out.println("-----");
        System.out.println("출근자 : " + name);
        System.out.println("일자 : " + commute.getCommuteDate());
        System.out.println("출근 시간 : " + commute.getClockIn());
        if(commute.getClockOut() != null){
            System.out.println("퇴근 시간 : " + commute.getClockOut());
        }
        System.out.println("-----");
    }

    // 출퇴근 결산
    public boolean isOnTime(LocalTime standard, LocalTime clockIn){
        return clockIn.isBefore(standard);
    }

    public boolean isOvertime(LocalTime standard, LocalTime clockOut){
        return clockOut.isAfter(standard);
    }

    public Commute dailyRecord(Commute commute, Work work, LocalTime standardClockIn, LocalTime standardClockOut, PartialLeaveType partialLeaveType, String statement){
        commute.setWork(work);
        commute.setCommuteType(isOnTime(standardClockIn, commute.getClockIn()) ? CommuteType.COMMUTE : CommuteType.LATENESS);
        if(isOvertime(standardClockOut, commute.getClockOut())){
            Duration duration = Duration.between(standardClockOut, commute.getClockOut());
            int overtime = (int) duration.toMinutes();
            commute.setOvertime(overtime);
        }
        if(partialLeaveType != null){
            commute.setPartialLeaveType(partialLeaveType);
        }
        commute.setStatement(statement);
        return commuteRepository.save(commute);
    }

    public void displayDailyRecord(Commute commute){
        displayCommute(commute);
        System.out.println("출근 상태 : " + commute.getCommuteType().getCode());
        System.out.println("조퇴&반차 여부 : " + (commute.getPartialLeaveType() != null));
        System.out.println("조퇴&반차 유형 : " + (commute.getPartialLeaveType() != null ? commute.getPartialLeaveType().getCode() : "미적용"));
        System.out.println("초과근무 여부 : " + (commute.getOvertime() != 0));
        System.out.println("초과근무 시간(단위 : 분) : " + commute.getOvertime());
        System.out.println("-----");
    }
}
