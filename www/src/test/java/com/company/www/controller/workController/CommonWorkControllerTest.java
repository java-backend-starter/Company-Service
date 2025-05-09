package com.company.www.controller.workController;

import com.company.www.constant.staff.Gender;
import com.company.www.constant.staff.ResignState;
import com.company.www.constant.work.*;
import com.company.www.converter.Converter;
import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.human_reasource.Commute;
import com.company.www.entity.work.human_reasource.Holiday;
import com.company.www.entity.work.human_reasource.Resign;
import com.company.www.repository.belong.DepartmentRepository;
import com.company.www.repository.belong.PositionRepository;
import com.company.www.repository.belong.RoleRepository;
import com.company.www.repository.belong.SectionRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.account.AdditionalBudgetItemRepository;
import com.company.www.repository.work.account.AdditionalBudgetRepository;
import com.company.www.repository.work.account.BudgetItemRepository;
import com.company.www.repository.work.account.BudgetPlanRepository;
import com.company.www.repository.work.human_resource.*;
import com.company.www.service.work.WorkServiceForTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommonWorkControllerTest {
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PositionRepository positionRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    WorkTypeRepository workTypeRepository;
    @Autowired
    WorkRepository workRepository;
    @Autowired
    WorkServiceForTest workServiceForTest;

    @Autowired
    HolidayRepository holidayRepository;
    @Autowired
    CommuteRepository commuteRepository;
    @Autowired
    ResignRepository resignRepository;
    @Autowired
    PayrollRepository payrollRepository;
    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    TrainingAppraisalRepository trainingAppraisalRepository;

    @Autowired
    BudgetPlanRepository budgetPlanRepository;
    @Autowired
    BudgetItemRepository budgetItemRepository;
    @Autowired
    AdditionalBudgetRepository additionalBudgetRepository;
    @Autowired
    AdditionalBudgetItemRepository additionalBudgetItemRepository;

    private final static String [][] STAFF_INFO = {
            // STAFF_INFO 인덱스에 해당하는 직위
            // 0 : 사장, 1 : 부사장, 2 : 상무이사
            // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
            { "경영진", "사장", "경영진", "경영진" },
            { "경영진", "부사장", "경영진", "경영진" },
            { "경영진", "상무이사", "경영진", "경영진" },
            { "인사", "부장", "인사부", "미정" },
            { "인사", "차장", "인사부", "미정" },
            { "인사", "과장", "인사부", "인사 1과" },
            { "인사", "대리", "인사부", "인사 1과" },
            { "인사", "주임", "인사부", "인사 1과" },
            { "인사", "사원", "인사부", "인사 1과" },
            { "인사", "인턴", "인사부", "인사 1과" }
    };

    // 0 : 연차, 1. 보상, 2. 가족돌봄, 3. 유산&사산, 4. 출산
    // 5 : 배우자 출산, 6 : 난임치료, 7 : 공가, 8 : 병가, 9 : 경조사
    // 10 : 여름휴가, 11 : 기타
    private final static HolidayType [] HOLIDAY_TYPES = HolidayType.values();

    // 직원 생성
    public Staff makeStaff(String id, String name, Gender gender, String [] info){
        Role role = roleRepository.findByRoleName(info[0]);
        Position position = positionRepository.findByPositionName(info[1]);
        Department department = departmentRepository.findByDepartmentName(info[2]);
        Section section = sectionRepository.findBySectionName(info[3]);

        Staff staff = new Staff();

        LocalDate birthday = LocalDate.of(1997, 1, 1);
        birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate start = LocalDate.of(2020, 1, 1);
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        staff.setUserId(id);
        staff.setPassword(passwordEncoder.encode("qwer1234"));

        staff.setStaffName(name);
        staff.setGender(gender);
        staff.setBirthDate(birthday);
        staff.setEmail("ascia333@naver.com");
        staff.setAddress("주소");
        staff.setContact("연락처");
        staff.setEmployDate(start);
        staff.setResign(null);

        staff.setRole(role);
        staff.setPosition(position);
        staff.setDepartment(department);
        staff.setSection(section);

        return staffRepository.save(staff);
    }

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

    @DisplayName("휴가 생성")
    @Test
    @Transactional
    public void createHolidayTest(){
        // HOLIDAY_TYPES 인덱스 정보(인덱스 : HOLIDAY_TYPE)
        // 0 : 연차, 1. 보상, 2. 가족돌봄, 3. 유산&사산, 4. 출산
        // 5 : 배우자 출산, 6 : 난임치료, 7 : 공가, 8 : 병가, 9 : 경조사
        // 10 : 여름휴가, 11 : 기타
        WorkType workType = workTypeRepository.findByWorkName("휴가");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Work work = workServiceForTest.createWork(workType, staff, "휴가 신청", "보존기간", "보안등급");
        Holiday holiday = createHoliday(
                work,
                HOLIDAY_TYPES[0].getCode(),
                "사유",
                LocalDate.of(2024, 10, 16),
                LocalDate.of(2024, 10, 16)
        );

        workServiceForTest.displayDraftWork(work);
        displayHoliday(holiday);

        assertEquals(HOLIDAY_TYPES[0].getCode(), holiday.getHolidayType().getCode(), "일치하지 않습니다.(휴가종류)");
        assertEquals("사유", holiday.getReason(), "일치하지 않습니다.(사유)");
        assertEquals(LocalDate.of(2024, 10, 16), holiday.getStartDate(), "일치하지 않습니다.(시작일)");
        assertEquals(LocalDate.of(2024, 10, 16), holiday.getEndDate(), "일치하지 않습니다.(종료일)");
        assertEquals("N", holiday.getHalf(), "일치하지 않습니다.(반차)");
    }

    @DisplayName("휴가 수정")
    @Test
    @Transactional
    public void updateHolidayTest(){
        // HOLIDAY_TYPES 인덱스 정보(인덱스 : HOLIDAY_TYPE)
        // 0 : 연차, 1. 보상, 2. 가족돌봄, 3. 유산&사산, 4. 출산
        // 5 : 배우자 출산, 6 : 난임치료, 7 : 공가, 8 : 병가, 9 : 경조사
        // 10 : 여름휴가, 11 : 기타
        WorkType workType = workTypeRepository.findByWorkName("휴가");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Work work = workServiceForTest.createWork(workType, staff, "휴가 신청", "보존기간", "보안등급");
        
        Holiday created = createHoliday(work, HOLIDAY_TYPES[0].getCode(),
                "사유",
                LocalDate.of(2024, 10, 16),
                LocalDate.of(2024, 10, 16)
        );
        
        Holiday holiday = updateHoliday(created, HOLIDAY_TYPES[7].getCode(), 
                "사유 수정",
                LocalDate.of(2024, 10, 18),
                LocalDate.of(2024, 10, 18),
                "Y"
        );
        workServiceForTest.displayDraftWork(work);
        displayHoliday(holiday);

        assertEquals(HOLIDAY_TYPES[7].getCode(), holiday.getHolidayType().getCode(), "일치하지 않습니다.(휴가종류)");
        assertEquals("사유 수정", holiday.getReason(), "일치하지 않습니다.(사유)");
        assertEquals(LocalDate.of(2024, 10, 18), holiday.getStartDate(), "일치하지 않습니다.(시작일)");
        assertEquals(LocalDate.of(2024, 10, 18), holiday.getEndDate(), "일치하지 않습니다.(종료일)");
        assertEquals("Y", holiday.getHalf(), "일치하지 않습니다.(반차)");
    }

    @DisplayName("휴가 결재")
    @Test
    @Transactional
    public void holidayApprovalTest(){
        // HOLIDAY_TYPES 인덱스 정보(인덱스 : HOLIDAY_TYPE)
        // 0 : 연차, 1. 보상, 2. 가족돌봄, 3. 유산&사산, 4. 출산
        // 5 : 배우자 출산, 6 : 난임치료, 7 : 공가, 8 : 병가, 9 : 경조사
        // 10 : 여름휴가, 11 : 기타
        WorkType workType = workTypeRepository.findByWorkName("휴가");

        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[3]);
        Staff thirdApprovalStaff = makeStaff("asica6", "홍대장", Gender.MALE, STAFF_INFO[2]);
        Staff finalApprovalStaff = makeStaff("asica7", "홍사장", Gender.MALE, STAFF_INFO[0]);

        Work work = workServiceForTest.createWork(workType, draftStaff, "휴가 신청", "보존기간", "보안등급");

        Holiday holiday = createHoliday(work, HOLIDAY_TYPES[0].getCode(),
                "사유",
                LocalDate.of(2024, 10, 16),
                LocalDate.of(2024, 10, 16)
        );

        work = workServiceForTest.approveWork(work, firstApprovalStaff);
        work = workServiceForTest.approveWork(work, secondApprovalStaff);
        work = workServiceForTest.approveWork(work, thirdApprovalStaff);
        work = workServiceForTest.approveWork(work, finalApprovalStaff);

        workServiceForTest.displayApprovalWork(work);
        
        assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("사원"), "사원이 아닙니다.");
        assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("과장"), "과장이 아닙니다.");
        assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장이 아닙니다.");
        assertEquals(thirdApprovalStaff.getPosition(), positionRepository.findByPositionName("상무이사"), "상무이사가 아닙니다.");
        assertEquals(finalApprovalStaff.getPosition(), positionRepository.findByPositionName("사장"), "상무이사가 아닙니다.");
        assertNotNull(work);
        assertNotNull(holiday);
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

    @DisplayName("출근 기록")
    @Test
    @Transactional
    public void createClockIn(){
        WorkType workType = workTypeRepository.findByWorkName("출퇴근");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Commute commute = createClockIn(staff, LocalTime.of(8, 30, 0));
        displayCommute(commute);

        assertEquals(staff, commute.getStaff(),"기안자가 일치하지 않습니다.");
        assertEquals(LocalTime.of(8, 30, 0), commute.getClockIn(), "시간이 일치하지 않습니다.");
    }

    @DisplayName("퇴근 기록")
    @Test
    @Transactional
    public void createClockOut(){
        WorkType workType = workTypeRepository.findByWorkName("출퇴근");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Commute commute = createClockIn(staff, LocalTime.of(8, 30, 0));

        commute = createClockOut(commute, LocalTime.of(18, 0, 0));
        displayCommute(commute);

        assertEquals(staff, commute.getStaff(),"기안자가 일치하지 않습니다.");
        assertEquals(LocalTime.of(8, 30, 0), commute.getClockIn(), "출근 시간이 일치하지 않습니다.");
        assertEquals(LocalTime.of(18, 0, 0), commute.getClockOut(), "퇴근 시간이 일치하지 않습니다.");
    }

    @DisplayName("출근 기록 수정")
    @Test
    @Transactional
    public void updateClockIn(){
        WorkType workType = workTypeRepository.findByWorkName("출퇴근");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Commute commute = createClockIn(staff, LocalTime.of(8, 30, 0));

        commute = updateCommute(commute, LocalTime.of(8, 45, 0), null);
        displayCommute(commute);

        assertEquals(staff, commute.getStaff(),"기안자가 일치하지 않습니다.");
        assertEquals(LocalTime.of(8, 45, 0), commute.getClockIn(), "시간이 일치하지 않습니다.");
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

    @DisplayName("출퇴근 체크 1")
    @Test
    @Transactional
    public void checkDailyCommute1(){
        WorkType workType = workTypeRepository.findByWorkName("출퇴근");
        Staff commuter = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff draftStaff = makeStaff("ascia44", "홍길순", Gender.FEMALE, STAFF_INFO[7]);

        Work work = workServiceForTest.createWork(workType, draftStaff, "일간 출퇴근 정산", "보존년한", "보안등급");
        Commute commute = createClockIn(commuter, LocalTime.of(8, 30, 0));

        commute = createClockOut(commute, LocalTime.of(13, 0, 0));
        commute = dailyRecord(commute, work, LocalTime.of(9, 0, 0), LocalTime.of(18, 0, 0), PartialLeaveType.PM_HALF, "비고");
        displayDailyRecord(commute);

        assertEquals(commuter, commute.getStaff(),"출퇴근자가 일치하지 않습니다.");
        assertEquals(LocalTime.of(8, 30, 0), commute.getClockIn(), "출근 시간이 일치하지 않습니다.");
        assertEquals(LocalTime.of(13, 0, 0), commute.getClockOut(), "퇴근 시간이 일치하지 않습니다.");
        assertEquals(CommuteType.COMMUTE, commute.getCommuteType(), "출근 상태가 일치하지 않습니다.");
        assertEquals(PartialLeaveType.PM_HALF, commute.getPartialLeaveType(), "조퇴&반차 여부가 일치하지 않습니다.");
        assertEquals(0, commute.getOvertime(), "초과근무 시간이 일치하지 않습니다.");
        assertEquals(work, commute.getWork(), "결재작업이 일치하지 않습니다.");
    }

    @DisplayName("출퇴근 체크 2")
    @Test
    @Transactional
    public void checkDailyCommute2(){
        WorkType workType = workTypeRepository.findByWorkName("출퇴근");
        Staff commuter = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff draftStaff = makeStaff("ascia44", "홍길순", Gender.FEMALE, STAFF_INFO[7]);

        Work work = workServiceForTest.createWork(workType, draftStaff, "일간 출퇴근 정산", "보존년한", "보안등급");
        Commute commute = createClockIn(commuter, LocalTime.of(8, 30, 0));

        commute = createClockOut(commute, LocalTime.of(19, 0, 0));
        commute = dailyRecord(commute, work, LocalTime.of(9, 0, 0), LocalTime.of(18, 0, 0), null, "비고");
        displayDailyRecord(commute);

        assertEquals(commuter, commute.getStaff(),"출퇴근자가 일치하지 않습니다.");
        assertEquals(LocalTime.of(8, 30, 0), commute.getClockIn(), "출근 시간이 일치하지 않습니다.");
        assertEquals(LocalTime.of(19, 0, 0), commute.getClockOut(), "퇴근 시간이 일치하지 않습니다.");
        assertEquals(CommuteType.COMMUTE, commute.getCommuteType(), "출근 상태가 일치하지 않습니다.");
        assertNull(commute.getPartialLeaveType(), "조퇴&반차 여부가 일치하지 않습니다.");
        assertEquals(60, commute.getOvertime(), "초과근무 시간이 일치하지 않습니다.");
        assertEquals(work, commute.getWork(), "결재작업이 일치하지 않습니다.");
    }

    @DisplayName("출퇴근 체크 결재")
    @Test
    @Transactional
    public void dailyRecordApproval(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("출퇴근");
        Staff commuter = makeStaff("ascia33", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff draftStaff = makeStaff("ascia44", "홍길순", Gender.FEMALE, STAFF_INFO[7]);
        Staff firsttStaff = makeStaff("ascia55", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondStaff = makeStaff("ascia66", "홍길순", Gender.FEMALE, STAFF_INFO[3]);
        Staff finalStaff = makeStaff("ascia77", "홍길순", Gender.FEMALE, STAFF_INFO[2]);

        Work work = workServiceForTest.createWork(workType, draftStaff, "일간 출퇴근 정산", "보존년한", "보안등급");
        Commute commute = createClockIn(commuter, LocalTime.of(8, 30, 0));

        commute = createClockOut(commute, LocalTime.of(19, 0, 0));
        commute = dailyRecord(commute, work, LocalTime.of(9, 0, 0), LocalTime.of(18, 0, 0), null, "비고");

        work = workServiceForTest.approveWork(work, firsttStaff);
        work = workServiceForTest.approveWork(work, secondStaff);
        work = workServiceForTest.approveWork(work, finalStaff);

        displayDailyRecord(commute);
        workServiceForTest.displayApprovalWork(work);

        assertEquals(commuter, commute.getStaff(),"출퇴근자가 일치하지 않습니다.");
        assertEquals(LocalTime.of(8, 30, 0), commute.getClockIn(), "출근 시간이 일치하지 않습니다.");
        assertEquals(LocalTime.of(19, 0, 0), commute.getClockOut(), "퇴근 시간이 일치하지 않습니다.");
        assertEquals(CommuteType.COMMUTE, commute.getCommuteType(), "출근 상태가 일치하지 않습니다.");
        assertNull(commute.getPartialLeaveType(), "조퇴&반차 여부가 일치하지 않습니다.");
        assertEquals(60, commute.getOvertime(), "초과근무 시간이 일치하지 않습니다.");
        assertEquals(work, commute.getWork(), "결재작업이 일치하지 않습니다.");
        assertEquals(WorkState.COMPLETED, work.getDraftState(), "결재가 완료되지 않았습니다.");
    }

    // 인사행정(View)
    public void displayStaff(Staff staff){
        System.out.println("-----");
        System.out.println("기본 정보");
        System.out.println("아이디 : " + staff.getUserId());
        System.out.println("-----");
        System.out.println("인적 정보");
        System.out.println("이름 : " + staff.getStaffName());
        System.out.println("성별 : " + Converter.convertToGenderString(staff.getGender()));
        System.out.println("생년월일 : " + staff.getBirthDate());
        System.out.println("주소 : " + staff.getAddress());
        System.out.println("연락처 : " + staff.getContact());
        System.out.println("이메일 : " + staff.getEmail());
        System.out.println("-----");
        System.out.println("회사 내 정보");
        System.out.println("직위 : " + staff.getPosition().getPositionName());
        System.out.println("직무 : " + staff.getRole().getRoleName());
        System.out.println("부 : " + staff.getDepartment().getDepartmentName());
        System.out.println("과 : " + staff.getSection().getSectionName());
        System.out.println("입사일 : " + staff.getEmployDate());
        if(staff.getResign() != null && staff.getResign().getResignState().equals(ResignState.COMPLETED)) {
            System.out.println("퇴사일 : " + staff.getResign().getRequestDate());
            System.out.println("퇴사처리 여부 : " + staff.getResign().getResignState().getCode());
        }
        System.out.println("-----");
    }

    // 퇴사 신청

    public Resign requestResign(Work work, Staff resignStaff, LocalDate requestResign){
        Resign resign = new Resign();
        resign.setWork(work);
        resign.setRequestDate(requestResign);
        resign.setResignStaff(resignStaff);
        resign.setResignState(ResignState.REQUEST);
        return resignRepository.save(resign);
    }

    public Resign cancelResign(Resign resign){
        resign.setRequestDate(LocalDate.now());
        resign.setResignState(ResignState.CANCELED);
        return resignRepository.save(resign);
    }
    
    /*
     * 추가 기능
     * 1. 예산 게획
     * 2. 추가 예산
     * 3. 급여 명세서 조회
     * 4. 승진&직무이동 조회
     * 5. 교육 프로그램 조회
     * 6. 교육 대상자 조회
     * 7. 교육 평가 조회
     */

}