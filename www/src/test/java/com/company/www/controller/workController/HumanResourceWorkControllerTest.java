package com.company.www.controller.workController;

import com.company.www.constant.staff.Gender;
import com.company.www.constant.staff.ResignState;
import com.company.www.converter.Converter;
import com.company.www.entity.belong.*;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.human_reasource.Commute;
import com.company.www.entity.work.human_reasource.Holiday;
import com.company.www.repository.belong.*;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.human_resource.*;
import com.company.www.service.work.WorkServiceForTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class HumanResourceWorkControllerTest {
    @Autowired
    StaffRepository staffRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PositionRepository positionRepository;

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    WorkTypeRepository workTypeRepository;
    @Autowired
    WorkRepository workRepository;

    @Autowired
    HolidayRepository holidayRepository;
    @Autowired
    CommuteRepository commuteRepository;
    @Autowired
    CommuteMonthlyRecordRepository commuteMonthlyRecordRepository;
    @Autowired
    RecruitRepository recruitRepository;
    @Autowired
    ApplicantRepository applicantRepository;
    @Autowired
    TrainingRepository trainingRepository;
    @Autowired
    TraineeRepository traineeRepository;
    @Autowired
    TrainingAppraisalRepository trainingAppraisalRepository;
    @Autowired
    AppraisalRepository appraisalRepository;
    @Autowired
    PerformanceRepository performanceRepository;
    @Autowired
    PromotionChangeRepository promotionChangeRepository;
    @Autowired
    PayInfoRepository payInfoRepository;
    @Autowired
    PayRepository payRepository;
    @Autowired
    PayrollRepository payrollRepository;

    @Autowired
    WorkServiceForTest workServiceForTest;

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

    private final static String [][] ACCOUNT = {
            { "재무회계", "부장", "재무회계부", "미정" },
            { "재무회계", "차장", "재무회계부", "미정" },
            { "재무회계", "과장", "재무회계부", "재무과" },
            { "재무회계", "대리", "재무회계부", "재무과" },
            { "재무회계", "주임", "재무회계부", "재무과" },
            { "재무회계", "사원", "재무회계부", "재무과" },
            { "재무회계", "인턴", "재무회계부", "재무과" }
    };

    public List<WorkType> filter(WorkTypeRepository workTypeRepository, String ... types){
        List<WorkType> filtered = new ArrayList<>();
        for(String type : types ){
            filtered.add(workTypeRepository.findByWorkName(type));
        }
        return filtered;
    }

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

    // 부서관리 테스트
    public Department createDepartment(String name, String englishName){
        Department department = new Department();
        department.setDepartmentName(name);
        department.setDepartmentEnglishName(englishName);
        department.setSections(new ArrayList<>());
        department.setStaff(new ArrayList<>());
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Department department, String name, String englishName){
        department.setDepartmentName(name);
        department.setDepartmentEnglishName(englishName);
        return departmentRepository.save(department);
    }

    public boolean deleteDepartment(Department department){
        if(department != null && department.getStaff().isEmpty()){
            departmentRepository.delete(department);
            return true;
        }
        else {
            return false;
        }
    }

    public void displayDepartment(Department department){
        int sectionSize = department.getSections().size();
        int staffSize = department.getStaff().size();
        System.out.println("-----");
        System.out.println("부서명 : " + department.getDepartmentName());
        System.out.println("부서명(영문) : " + department.getDepartmentEnglishName());
        System.out.println("소속된 과 수 : " + sectionSize);
        System.out.println("소속된 직원 수 : " + staffSize);
        System.out.println("-----");
    }

    @DisplayName("부 확인")
    @Test
    @Transactional
    public void displayDepartment(){
        List<Department> departments = departmentRepository.findAll();
        for(Department department : departments){
            displayDepartment(department);
            System.out.println(department.getSections() == null);
        }
    }

    @DisplayName("부 생성")
    @Test
    @Transactional
    public void createDepartment(){
        Department created = createDepartment("비서부", "biser");

        Department department = departmentRepository.findByDepartmentName("비서부");

        System.out.println("검색여부 : " + (departmentRepository.findByDepartmentName("비서부") != null));
        displayDepartment(department);
    }

    @DisplayName("부 수정")
    @Test
    @Transactional
    public void updateDepartment(){
        Department created = createDepartment("비서부", "biser");

        updateDepartment(created, "비서부", "secretary");

        Department department = departmentRepository.findByDepartmentName("비서부");

        displayDepartment(department);
    }

    @DisplayName("부 삭제")
    @Test
    @Transactional
    public void deleteDepartment(){
        Department created = createDepartment("비서부", "secretary");

        System.out.println(deleteDepartment(created));
    }

    // 과 관리 테스트
    public Section createSection(Department department, String sectionName){
        Section section = new Section();
        section.setSectionName(sectionName);
        section.setDepartment(department);
        section.setStaff(new ArrayList<>());
        return sectionRepository.save(section);
    }
    public Section updateSection(Section section, Department department, String sectionName){
        section.setDepartment(department);
        section.setSectionName(sectionName);
        return sectionRepository.save(section);
    }
    public boolean deleteSection(Section section){
        if(section != null && section.getStaff().isEmpty()){
            sectionRepository.delete(section);
            return true;
        }
        return false;
    }
    public void displaySection(Section section){
        int staffSize = section.getStaff().size();
        System.out.println("-----");
        System.out.println("과 이름 : " + section.getSectionName());
        System.out.println("상위 부서(부) : " + section.getDepartment().getDepartmentName());
        System.out.println("소속 직원 수 : " + staffSize);
        System.out.println("-----");
    }

    @DisplayName("과 확인")
    @Test
    @Transactional
    public void displaySection(){
        List<Section> sections = sectionRepository.findAll();
        for(Section section : sections){
            displaySection(section);
        }
    }

    @DisplayName("과 생성")
    @Test
    @Transactional
    public void createSection(){
        Department department = departmentRepository.findByDepartmentName("인사부");

        Section section = createSection(department, "인사 3과");

        System.out.println("검색여부 : " + (sectionRepository.findBySectionName("인사 3과") != null));
        displaySection(section);
    }

    @DisplayName("과 수정")
    @Test
    @Transactional
    public void updateSection(){
        Department department = departmentRepository.findByDepartmentName("인사부");

        Section section = createSection(department, "인사 3과");

        section = updateSection(section, department, "인사 4과");

        displaySection(section);
    }

    @DisplayName("과 삭제")
    @Test
    @Transactional
    public void deleteSection(){
        Department department = departmentRepository.findByDepartmentName("인사부");

        Section section = createSection(department, "인사 3과");

        System.out.println(deleteSection(section));
    }
    

    // 직위 테스트
    public Position createPosition(String positionName){
        Position position = new Position();
        position.setPositionName(positionName);
        position.setStaff(new ArrayList<>());
        return positionRepository.save(position);
    }

    public Position updatePosition(Position position, String positionName){
        position.setPositionName(positionName);
        return  positionRepository.save(position);
    }

    public boolean deletePosition(Position position){
        if(position != null && position.getStaff().isEmpty()){
            positionRepository.delete(position);
            return true;
        }
        return false;
    }

    public void displayPosition(Position position){
        int staffSize = position.getStaff().size();
        System.out.println("-----");
        System.out.println("직위명 : " +  position.getPositionName());
        System.out.println("직위 수여자 수 : " + staffSize);
        System.out.println("-----");
    }

    @DisplayName("직위 확인")
    @Test
    @Transactional
    public void displayPosition(){
        List<Position> positions = positionRepository.findAll();
        for(Position position : positions){
            displayPosition(position);
        }
    }

    @DisplayName("직위 생성")
    @Test
    @Transactional
    public void createPosition(){
        Position position = createPosition("임시 직위");

        System.out.println("검색여부 : " + (positionRepository.findByPositionName("임시 직위") != null));
        displayPosition(position);
    }

    @DisplayName("직위 수정")
    @Test
    @Transactional
    public void updatePosition(){
        Position position = createPosition("임시 직위");

        Position updated = updatePosition(position, "이사장");
        displayPosition(updated);
    }

    @DisplayName("직위 삭제")
    @Test
    @Transactional
    public void deletePosition(){
        Position position = createPosition("임시 직위");

        deletePosition(position);
        System.out.println("삭제여부 : " + (positionRepository.findByPositionName("임시 직위") == null));
    }
    
    // 직무 테스트
    public Role createRole(String roleName){
        Role role = new Role();
        role.setRoleName(roleName);
        role.setStaff(new ArrayList<>());
        return roleRepository.save(role);
    }

    public Role updateRole(Role role, String roleName){
        role.setRoleName(roleName);
        return roleRepository.save(role);
    }

    public boolean deleteRole(Role role){
        if(role != null && role.getStaff().isEmpty()){
            roleRepository.delete(role);
            return true;
        }
        return false;
    }

    public void displayRole(Role role){
        int staffSize = role.getStaff().size();
        System.out.println("-----");
        System.out.println("직무명 : " + role.getRoleName());
        System.out.println("직무종사자 수 : " + staffSize);
        System.out.println("-----");
    }

    @DisplayName("직무 확인")
    @Test
    @Transactional
    public void displayRole(){
        List<Role> roles = roleRepository.findAll();
        for(Role role : roles){
            displayRole(role);
        }
    }

    @DisplayName("직무 생성")
    @Test
    @Transactional
    public void createRole(){
        Role role = createRole("임시 직무");

        System.out.println("검색여부 : " + (roleRepository.findByRoleName("임시 직무") != null));
        displayRole(role);
    }

    @DisplayName("직무 수정")
    @Test
    @Transactional
    public void updateRole(){
        Role role = createRole("임시 직무");

        Role updated = updateRole(role, "총괄");

        displayRole(updated);
    }

    @DisplayName("직무 삭제")
    @Test
    @Transactional
    public void deleteRole(){
        Role role = createRole("임시 직무");

        deleteRole(role);
        System.out.println("삭제여부 : " + (roleRepository.findByRoleName("임시 직무") == null));
    }
    
    // 인사행정 테스트
    public Staff createStaff(String id, String name, String gender, int [] birthDateInfo, String email, String address, String contact){
        Staff staff = new Staff();

        LocalDate birthDate = LocalDate.of(birthDateInfo[0], birthDateInfo[1], birthDateInfo[2]);
        LocalDate start = LocalDate.now();

        staff.setUserId(id);
        staff.setPassword(passwordEncoder.encode("qwer1234"));

        staff.setStaffName(name);
        staff.setGender(Converter.convetToGender(gender));
        staff.setBirthDate(birthDate);
        staff.setEmail(email);
        staff.setAddress(address);
        staff.setContact(contact);
        staff.setEmployDate(start);
        staff.setResign(null);

        staff.setRole(roleRepository.findByRoleName("미정"));
        staff.setPosition(positionRepository.findByPositionName("미정"));
        staff.setDepartment(departmentRepository.findByDepartmentName("미정"));
        staff.setSection(sectionRepository.findBySectionName("미정"));

        return staffRepository.save(staff);
    }

    public Staff updateStaffInfo(Staff staff, String name, String gender, int [] birthDateInfo, String email, String address, String contact){
        LocalDate birthDate = LocalDate.of(birthDateInfo[0], birthDateInfo[1], birthDateInfo[2]);

        staff.setStaffName(name);
        staff.setGender(Converter.convetToGender(gender));
        staff.setBirthDate(birthDate);
        staff.setEmail(email);
        staff.setAddress(address);
        staff.setContact(contact);

        return staffRepository.save(staff);
    }

    public Staff updateStaffWorkInfo(Staff staff, String role, String position, String authority, String departmentName, String sectionName, String teamName){
        staff.setRole(roleRepository.findByRoleName(role));
        staff.setPosition(positionRepository.findByPositionName(position));

        Department department = departmentRepository.findByDepartmentName(departmentName);
        Section section = sectionRepository.findBySectionName(sectionName);

        staff.setDepartment(department);
        staff.setSection(section);

        return staffRepository.save(staff);
    }


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
            // System.out.println("퇴사일 : " + staff.getResign().getRequestResign());
            System.out.println("퇴사처리 여부 : " + staff.getResign().getResignState());
        }
        System.out.println("-----");
    }

    @DisplayName("직원 계정 생성")
    @Test
    @Transactional
    public void createStaff(){
        Staff staff = createStaff("아이디123", "이름", "여자", new int[] {1997, 1, 1}, "email@email.com", "집주소", "연락처");

        System.out.println("계정 생성 여부 : " + (staffRepository.findByUserId(staff.getUserId()) != null));
        displayStaff(staff);
    }

    @DisplayName("직원 계정 수정(인적 정보)")
    @Test
    @Transactional
    public void updateStaffInfo(){
        String userId = "jsksh9999";
        Staff created = createStaff(userId, "이름", "여자", new int[] {1997, 1, 1}, "email@email.com", "집주소", "연락처");

        updateStaffInfo(created, "가나다", "여자", new int[] {1998, 10, 10}, "jsksh@naver.com", "인천광역시 연수구 함박뫼로 191", "010-1234-5678");

        Staff staff = staffRepository.findByUserId(userId);

        displayStaff(staff);
    }

    @DisplayName("직원 계정 수정(회사 정보)")
    @Test
    @Transactional
    public void updateStaffWorkInfo(){
        String userId = "jsksh9999";
        Staff created = createStaff(userId, "이름", "여자", new int[] {1997, 1, 1}, "email@email.com", "집주소", "연락처");

        updateStaffWorkInfo(created, "인사", "사원", "사원", "인사부", "인사 1과", "인사 1팀");

        Staff staff = staffRepository.findByUserId(userId);
        displayStaff(staff);
    }

    @DisplayName("직원 퇴사 신청")
    @Test
    @Transactional
    public void resignStaff(){
        String userId = "jsksh9999";
        Staff created = createStaff(userId, "이름", "여자", new int[] {1997, 1, 1}, "email@email.com", "집주소", "연락처");

        updateStaffWorkInfo(created, "인사", "사원", "사원", "인사부", "인사 1과", "인사 1팀");


        Staff staff = staffRepository.findByUserId(userId);

        displayStaff(staff);
    }

    @DisplayName("직원 퇴사 처리")
    @Test
    @Transactional
    public void handleResign(){
        String userId = "jsksh9999";
        Staff created = createStaff(userId, "이름", "여자", new int[] {1997, 1, 1}, "email@email.com", "집주소", "연락처");

        updateStaffWorkInfo(created, "인사", "사원", "사원", "인사부", "인사 1과", "인사 1팀");

        Staff staff = staffRepository.findByUserId(userId);

        displayStaff(staff);
    }

    // 출퇴근 테스트

}