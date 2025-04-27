package com.company.www.service.work;

import com.company.www.constant.staff.Gender;
import com.company.www.constant.work.ApprovalPosition;
import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Position;
import com.company.www.entity.belong.Role;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.ApprovalLine;
import com.company.www.entity.work.Approver;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.exception.UnauthorizedException;
import com.company.www.repository.belong.DepartmentRepository;
import com.company.www.repository.belong.PositionRepository;
import com.company.www.repository.belong.RoleRepository;
import com.company.www.repository.belong.SectionRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.ApprovalLineRepository;
import com.company.www.repository.work.ApproverRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WorkServiceTest {
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
    ApprovalLineRepository approvalLineRepository;
    @Autowired
    ApproverRepository approverRepository;

    private final static String [][] STAFF_INFO = {
            { "경영진", "사장", "경영진", "경영진" },
            { "경영진", "부사장", "경영진", "경영진" },
            { "인사", "상무이사", "경영진", "경영진" },
            { "인사", "부장", "인사부", "미정" },
            { "인사", "차장", "인사부", "미정" },
            { "인사", "과장", "인사부", "인사 1과" },
            { "인사", "대리", "인사부", "인사 1과" },
            { "인사", "주임", "인사부", "인사 1과" },
            { "인사", "사원", "인사부", "인사 1과" },
            { "인사", "인턴", "인사부", "인사 1과" }
    };

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


        staff.setRole(role);
        staff.setPosition(position);
        staff.setDepartment(department);
        staff.setSection(section);

        return staffRepository.save(staff);
    }

    private final Map<Integer, ApprovalPosition> APPROVAL_POSITION_MAP = Map.ofEntries(
            Map.entry(0, ApprovalPosition.NOT_POSITION),
            Map.entry(120, ApprovalPosition.PRESIDENT),
            Map.entry(110, ApprovalPosition.VICE_PRESIDENT),
            Map.entry(90, ApprovalPosition.MANAGING_DIRECTOR),
            Map.entry(70, ApprovalPosition.GENERAL_MANAGER),
            Map.entry(60, ApprovalPosition.DEPUTY_GENERAL_MANAGER),
            Map.entry(50, ApprovalPosition.MANAGER),
            Map.entry(40, ApprovalPosition.ASSISTANT_MANAGER),
            Map.entry(30, ApprovalPosition.ASSOCIATE),
            Map.entry(20, ApprovalPosition.STAFF),
            Map.entry(10, ApprovalPosition.INTERN)
    );

    // 중간 권한 체크
    // 매개변수에 들어올 값의 정보
    // 0 : 미정, 100 : 사장, 90 : 부사장, 80 : 상무이사
    // 70 : 부장, 60 : 차장, 50 : 과장, 40 : 대리, 30 : 주임, 20 : 사원,  10 : 인턴
    // 직급 우선순위 비교
    // prior (이전 결재자의 직급)과 staff (현재 결재자의 직급)을 비교하여, staff가 우선순위가 높은지 확인
    private boolean higherThanPrior(int prior, int staff) {
        // 이전 결재자의 직급(prior)보다 staff의 직급이 높은 경우 true를 반환
        return prior < staff;
    }

    // Proxy 상태 체크
    // ApprovalState가 "대결" 또는 "전대결"일 경우 Proxy 상태로 간주
    private boolean isProxy(ApprovalState approvalState) {
        // ApprovalState 코드가 "대결" 또는 "전대결"이면 Proxy로 간주
        return "대결".equals(approvalState.getCode()) || "전대결".equals(approvalState.getCode());
    }

    // 최종 권한 체크
    // prior : 이전 결재자의 직급
    // approval : 결재선의 직급 코드 (결재 대기 중인 직급)
    // staff : 현재 결재자의 직급
    // state : ApprovalState (결재 상태)
    private boolean hasApprovalAuthority(int prior, int approval, int staff, ApprovalState state) {
        // Proxy 상태일 경우
        if (isProxy(state)) {
            // Proxy 상태일 때는 특정 직급 이상일 경우 (staff >= 80 && approval > 70)
            // 또는 prior < staff 이고 approval이 staff 이상인 경우 권한이 있다고 판단
            return (staff >= 80 && approval > 70) || (higherThanPrior(prior, staff) && approval >= staff);
        }
        // Proxy가 아닐 경우, 단순히 prior보다 staff가 우선순위가 높고, approval이 staff와 동일해야 권한이 있다고 판단
        return higherThanPrior(prior, staff) && approval == staff;
    }

    // 상무이사 이상 Proxy 권한 체크
    // 상무이사 이상 직급의 경우, Proxy 상태와 결재자가 있는지를 확인하여 권한을 부여
    public boolean hasDirectorProxyAuthority(Work work, ApprovalState approvalState) {
        // 결재자가 Draft로 지정한 직원의 직급을 가져옴
        Position draftPosition = work.getDraftStaff().getPosition();
        // 결재자 리스트를 가져옴
        List<Approver> approvers = work.getApprovers();

        // 상무이사 이상 직급이고, Proxy 상태일 때 결재자가 있다면 권한을 부여
        return draftPosition.getPositionRank() >= 80 && isProxy(approvalState) && !approvers.isEmpty();
    }

    // 결재 권한 확인
// 결재자가 결재선에서 통과할 차례인지, 그리고 결재 권한을 가지고 있는지 확인
    public boolean checkApprovalAuthority(Work work, Staff approvalStaff, ApprovalState approvalState) {
        // 결재자 리스트를 가져옴
        List<Approver> approvers = work.getApprovers();
        // 결재선에서 각 결재자의 직급을 가져옴
        List<ApprovalPosition> approvalPositions = work.getApprovalLine().getApprovalPositions();

        // 결재자가 모든 결재선을 이미 통과한 경우, 추가적인 권한 체크는 불필요
        if (approvers.size() == approvalPositions.size()) {
            return false;
        }

        // priorAuthority : 이전 결재자의 직급 (approvers 리스트에서 마지막 결재자의 직급)
        // approvalAuthority : 현재 결재선의 직급 코드
        // staffAuthority : 현재 결재자의 직급
        int priorAuthority = approvers.isEmpty() ?
                work.getDraftStaff().getPosition().getPositionRank() :
                approvers.get(approvers.size() - 1).getApprovalStaff().getPosition().getPositionRank();

        int approvalAuthority = approvalPositions.get(approvers.size()).getCode();
        int staffAuthority = approvalStaff.getPosition().getPositionRank();

        // 결재 권한 확인 : 권한이 있거나 상무이사 이상의 Proxy 권한이 있는 경우 true
        return hasApprovalAuthority(priorAuthority, approvalAuthority, staffAuthority, approvalState) ||
                hasDirectorProxyAuthority(work, approvalState);
    }

    // 결재 라인 설정(기본값)
    // 주어진 직원의 직급에 따라 기본 결재 라인을 설정하는 메서드
    public ApprovalLine makeDefaultApprovalLine(WorkType workType, Staff draftStaff) {
        // 직원의 직급명 가져오기
        String positionName = draftStaff.getPosition().getPositionName();
        List<ApprovalPosition> positions;

        // 직급에 따라 결재 라인 설정
        switch(positionName){
            case "사장" -> {
                // 사장은 상무이사, 부사장을 결재자로 설정
                positions = List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.VICE_PRESIDENT);
            }
            case "부사장" -> {
                // 부사장은 상무이사, 사장을 결재자로 설정
                positions = List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT);
            }
            case "상무이사" -> {
                // 상무이사는 부사장, 사장을 결재자로 설정
                positions = List.of(ApprovalPosition.VICE_PRESIDENT, ApprovalPosition.PRESIDENT);
            }
            case "부장" -> {
                // 부장은 상무이사, 부사장, 사장을 결재자로 설정
                positions = List.of(ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.VICE_PRESIDENT, ApprovalPosition.PRESIDENT);
            }
            case "차장", "과장" -> {
                // 차장과 과장은 부장, 상무이사, 사장을 결재자로 설정
                positions = List.of(ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT);
            }
            case "대리", "주임", "사원" -> {
                // 대리, 주임, 사원은 과장, 부장, 상무이사, 사장을 결재자로 설정
                positions = List.of(ApprovalPosition.MANAGER, ApprovalPosition.GENERAL_MANAGER, ApprovalPosition.MANAGING_DIRECTOR, ApprovalPosition.PRESIDENT);
            }
            default -> {
                // 그 외 직급은 결재 라인이 없음
                positions = List.of();
            }
        }

        // 결재 역할 설정: 작업 타입이 "공통"이면 직원의 역할을 사용, 아니면 작업 타입의 역할 사용
        Role role = workType.getRole().getRoleName().equals("공통") ? draftStaff.getRole() : workType.getRole();

        // 결재 라인 객체 생성
        ApprovalLine approvalLine = new ApprovalLine();
        approvalLine.setApprovalPositions(positions); // 설정된 결재 직급들
        approvalLine.setRole(role); // 역할 설정
        approvalLine.setApproverSize(positions.size()); // 결재자가 몇 명인지 설정

        // 결재 라인을 데이터베이스에 저장하고 반환
        return approvalLineRepository.save(approvalLine);
    }

    // 결재라인 설정(직접 설정)
    // 주어진 결재 직급 리스트에 맞춰 결재 라인을 설정하는 메서드
    public ApprovalLine makeApprovalLine(String ... approvalPositions) {
        // 결재 라인 객체 생성
        ApprovalLine approvalLine = new ApprovalLine();
        List<ApprovalPosition> positions = new ArrayList<>();

        // 결재 직급 리스트를 받아서 해당 직급의 ApprovalPosition을 찾아서 추가
        for(String approvalPosition : approvalPositions){
            // 결재 직급 이름에 해당하는 Position 객체를 조회
            Position position = positionRepository.findByPositionName(approvalPosition);
            if(position != null){
                // 직급에 해당하는 ApprovalPosition을 맵에서 찾기
                ApprovalPosition ap = APPROVAL_POSITION_MAP.get(position.getPositionRank());
                if(ap != null){
                    // 해당 직급이 존재하면 결재 직급 리스트에 추가
                    positions.add(ap);
                }
            }
        }

        // 결재자 수 설정
        approvalLine.setApproverSize(positions.size());

        // 결재 라인을 데이터베이스에 저장하고 반환
        return approvalLineRepository.save(approvalLine);
    }

    public WorkType setArbitraryApprovalLine(WorkType workType, String arbitraryPosition){
        Position position = positionRepository.findByPositionName(arbitraryPosition);
        workType.setArbitraryPosition(position);
        return workTypeRepository.save(workType);
    }

    // 문서 생성
    public Work initiateWork(WorkType workType, Staff draftStaff, String subject, String retentionPeriod, String securityLevel, ApprovalLine approvalLine){
        Work work = new Work();
        work.setWorkType(workType);
        work.setSubject(subject);
        work.setRetentionPeriod(retentionPeriod);
        work.setSecurityLevel(securityLevel);

        work.setDraftStaff(draftStaff);
        work.setDraftDate(LocalDateTime.now());
        work.setDraftState(WorkState.PENDING);
        work.setApprovers(new ArrayList<>());
        work.setApprovalLine(approvalLine);
        return work;
    }

    public Work createWork(WorkType workType, Staff draftStaff, String subject, String retentionPeriod, String securityLevel){
        boolean checkAuthority = !draftStaff.getPosition().getPositionName().equals("미정");

        if(checkAuthority){
            Work work = initiateWork(workType, draftStaff, subject, retentionPeriod, securityLevel, makeDefaultApprovalLine(workType, draftStaff));
            return workRepository.save(work);
        }
        else{
            throw new UnauthorizedException("문서를 생성할 권한이 없습니다.");
        }
    }

    // 결재라인 설정에 대해서는 차후 구현 예정
    public Work createWork(WorkType workType, Staff draftStaff, String subject, String retentionPeriod, String securityLevel, String ... positions){
        boolean checkAuthority = !draftStaff.getPosition().getPositionName().equals("미정");

        if(checkAuthority){
            Work work = initiateWork(workType, draftStaff, subject, retentionPeriod, securityLevel, makeApprovalLine(positions));
            return workRepository.save(work);
        }
        else{
            throw new UnauthorizedException("권한이 없습니다.");
        }
    }

    public Work updateWork(Work work, String subject, String retentionPeriod, String securityLevel){
        if(!work.getDraftState().equals(WorkState.PENDING)){
            if(work.getDraftState().equals(WorkState.HANDLING)){
                throw new UnauthorizedException("결재 중인 문서입니다.");
            }
            else if(work.getDraftState().equals(WorkState.COMPLETED)){
                throw new UnauthorizedException("결재 완료된 문서입니다.");
            }
            else {
                throw new UnauthorizedException("기타 사유로 수정할 수 없습니다.(사유 : " + work.getDraftState().getCode() + ")");
            }
        }
        work.setSubject(subject);
        work.setRetentionPeriod(retentionPeriod);
        work.setSecurityLevel(securityLevel);
        return workRepository.save(work);
    }

    // 결재
    public Work approvalWork(Work work, Staff approvalStaff, ApprovalState approvalState){
        boolean checkAuthority = checkApprovalAuthority(work, approvalStaff, approvalState);

        if(checkAuthority){
            List<Approver> approvers = work.getApprovers();

            Approver approver = new Approver();
            approver.setApprovalStaff(approvalStaff);
            approver.setApprovalTime(LocalDateTime.now());
            approver.setApprovalState(approvalState);
            approvers.add(approverRepository.save(approver));

            boolean isTopLevelApproval = approvalStaff.getPosition().getPositionRank() >= 80 && approvalState.equals(ApprovalState.PROXY);

            boolean isHandling = approvalState.equals(ApprovalState.APPROVE) || approvalState.equals(ApprovalState.DEFER) || approvalState.equals(ApprovalState.PROXY);

            boolean finalCheck = isTopLevelApproval || !isHandling;

            work.setDraftState(finalCheck ? WorkState.COMPLETED : WorkState.HANDLING);
            work.setApprovers(approvers);
            return workRepository.save(work);
        }
        else{
            if(work.getDraftState().equals(WorkState.COMPLETED) || work.getDraftState().equals(WorkState.DELETE)
                    || work.getDraftState().equals(WorkState.DISPLAY) || work.getDraftState().equals(WorkState.OVERDUE)){
                throw new IllegalStateException("결재할 수 없는 문서입니다. 사유 : " + work.getDraftState().getCode());
            }
            else {
                throw new UnauthorizedException("결재 권한이 없습니다.");
            }
        }
    }

    public Work approveWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.APPROVE);
    }

    public Work deferWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.DEFER);
    }

    public Work rejectWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.REJECT);
    }

    public Work arbitraryWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.ARBITRARY);
    }

    public Work proxyWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.PROXY);
    }

    public Work arbitraryProxyWork(Work work, Staff approvalStaff){
        return approvalWork(work, approvalStaff, ApprovalState.ARBITRARY_PROXY);
    }


    public void displayDraftWork(Work work){
        System.out.println("-----");
        System.out.println("업무 이름 : " + work.getWorkType().getWorkName());
        System.out.println("업무 유형 : " + work.getWorkType().getRole().getRoleName());
        System.out.println("-----");
        System.out.println("제목 : " + work.getSubject());
        System.out.println("보존년한 : " + work.getRetentionPeriod());
        System.out.println("보안등급 : " + work.getSecurityLevel());
        System.out.println("-----");
        System.out.println("-----");
        System.out.println("기안자명 : " + work.getDraftStaff().getStaffName());
        System.out.println("부 : " + work.getDraftStaff().getDepartment().getDepartmentName());
        System.out.println("과 : " + work.getDraftStaff().getSection().getSectionName());
        System.out.println("직위 : " + work.getDraftStaff().getPosition().getPositionName());
        System.out.println("직무 : " + work.getDraftStaff().getRole().getRoleName());
        System.out.println("기안 날짜 : " + work.getDraftDate().format(DateTimeFormatter.ofPattern("y-M-d h:m:s")));
        System.out.println("결재 상태 : " + work.getDraftState().getCode());
        System.out.println("-----");
    }

    public void displayApprovalWork(Work work){
        displayDraftWork(work);
        List<Approver> approvers = work.getApprovers();
        for(int i = 0; i < work.getApprovers().size(); i++){
            System.out.println((i+1) + "차 결재자명 : " + approvers.get(i).getApprovalStaff().getStaffName());
            System.out.println("부 : " + approvers.get(i).getApprovalStaff().getDepartment().getDepartmentName());
            System.out.println("과 : " + approvers.get(i).getApprovalStaff().getSection().getSectionName());
            System.out.println("직위 : " + approvers.get(i).getApprovalStaff().getPosition().getPositionName());
            System.out.println("직무 : " + approvers.get(i).getApprovalStaff().getRole().getRoleName());
            System.out.println("결재 날짜 : " + approvers.get(i).getApprovalTime().format(DateTimeFormatter.ofPattern("y-M-d h:m:s")));
            System.out.println("결재 상태 : " + approvers.get(i).getApprovalState().getCode());
            System.out.println("-----");
        }
    }

    // 기안서
    @DisplayName("기안서 작성 권한 테스트")
    @Test
    @Transactional
    public void checkDraftAuthority(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        System.out.println("기안서 작성자 직급 : " + staff.getPosition().getPositionName() + ", 번호 : " +  staff.getPosition().getPositionId());
        assertNotNull(staff, "직원이 생성되지 않았습니다.");
        assertTrue(staff.getPosition().getPositionId() != 1, "권한이 없습니다.");
    }

    @DisplayName("기안서 작성 테스트1 : 사원이 기안서 작성")
    @Test
    @Transactional
    public void createDraft1(){
        // 아래 주석은 STAFF_INFO 배열에 들어있는 정보
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Work work = createWork(workType, staff, "제목", "보존년한", "보안등급");
        displayDraftWork(work);
        ApprovalLine al = work.getApprovalLine();
        System.out.println("결재권자 직급 리스트");
        for(ApprovalPosition position : al.getApprovalPositions()){
            System.out.println(position);
        }
    }

    @DisplayName("기안서 작성 테스트2 : 과장이 기안서 작성")
    @Test
    @Transactional
    public void createDraft2(){
        // 아래 주석은 STAFF_INFO 배열에 들어있는 정보
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[5]);
        Work work = createWork(workType, staff, "제목", "보존년한", "보안등급");
        displayDraftWork(work);
        ApprovalLine al = work.getApprovalLine();
        System.out.println("결재권자 직급 리스트");
        for(ApprovalPosition position : al.getApprovalPositions()){
            System.out.println(position);
        }
    }

    @DisplayName("기안서 작성 테스트3 : 부장이 작성")
    @Test
    @Transactional
    public void createDraft3(){
        // 아래 주석은 STAFF_INFO 배열에 들어있는 정보
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[3]);
        Work work = createWork(workType, staff, "제목", "보존년한", "보안등급");
        displayDraftWork(work);
        ApprovalLine al = work.getApprovalLine();
        System.out.println("결재권자 직급 리스트");
        for(ApprovalPosition position : al.getApprovalPositions()){
            System.out.println(position);
        }
    }

    @DisplayName("기안서 작성 테스트4 : 상무이사가 작성")
    @Test
    @Transactional
    public void createDraft4(){
        // 아래 주석은 STAFF_INFO 배열에 들어있는 정보
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[2]);
        Work work = createWork(workType, staff, "제목", "보존년한", "보안등급");
        displayDraftWork(work);
        ApprovalLine al = work.getApprovalLine();
        System.out.println("결재권자 직급 리스트");
        for(ApprovalPosition position : al.getApprovalPositions()){
            System.out.println(position);
        }
    }

    @DisplayName("기안서 작성 테스트5")
    @Test
    @Transactional
    public void createDraft5(){
        // 아래 주석은 STAFF_INFO 배열에 들어있는 정보
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, staff, "제목", "보존년한", "보안등급");
        displayDraftWork(work);
    }

    @DisplayName("기안서 작성 테스트6")
    @Test
    @Transactional
    public void createDraft6(){
        // 아래 주석은 STAFF_INFO 배열에 들어있는 정보
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[0]);
        Work work = createWork(workType, staff, "제목", "보존년한", "보안등급");
        displayDraftWork(work);
    }


    @DisplayName("기안서 수정 테스트")
    @Test
    @Transactional
    public void updateDraft(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff staff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Work work = createWork(workType, staff, "제목", "보존년한", "보안등급");

        updateWork(work, "제목 수정", "보존년한 수정", "보안등급 수정");

        work = workRepository.findByWorkId(work.getWorkId());
        displayDraftWork(work);
    }

    // 1차 결재

    @DisplayName("1차 결재 권한 테스트1")
    @Test
    @Transactional
    public void checkFirstLevelAuthority1(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[6]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("1차 결재 권한 여부 : " + checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.APPROVE));
        assertFalse(checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.APPROVE), "결재 권한이 있습니다.");
    }

    @DisplayName("1차 결재 권한 테스트2")
    @Test
    @Transactional
    public void checkFirstLevelAuthority2(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("1차 결재 권한 여부 : " + checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.APPROVE));
        assertTrue(checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.APPROVE), "결재 권한이 없습니다.");
    }

    @DisplayName("1차 결재 권한 테스트3")
    @Test
    @Transactional
    public void checkFirstLevelAuthority3(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[4]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("1차 결재 권한 여부 : " + checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.APPROVE));
        assertFalse(checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.APPROVE), "결재 권한이 있습니다.");
    }

    @DisplayName("1차 결재 권한 테스트4")
    @Test
    @Transactional
    public void checkFirstLevelAuthority4(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[6]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.PROXY.getCode());
        System.out.println("1차 결재 권한 여부 : " + checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.PROXY));
        assertTrue(checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.PROXY), "권한이 없습니다.");
    }

    @DisplayName("1차 결재 권한 테스트5")
    @Test
    @Transactional
    public void checkFirstLevelAuthority5(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[7]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.PROXY.getCode());
        System.out.println("1차 결재 권한 여부 : " + checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.PROXY));
        assertTrue(checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.PROXY), "권한이 없습니다.");
    }
    @DisplayName("1차 결재 권한 테스트6")
    @Test
    @Transactional
    public void checkFirstLevelAuthority6(){
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[8]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.PROXY.getCode());
        System.out.println("1차 결재 권한 여부 : " + checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.PROXY));
        assertFalse(checkApprovalAuthority(work, firstApprovalStaff, ApprovalState.PROXY), "권한이 있습니다.");
    }
    
    @DisplayName("1차 결재 테스트1")
    @Test
    @Transactional
    public void firstLevelApproval1(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        try {
            work = approveWork(work, firstApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            if (massage.equals("결재 권한이 없습니다.")) {
                int index = work.getApprovers().size();
                System.out.println("결재자 권한 : " + firstApprovalStaff.getPosition().getPositionName());
            } else {
                System.out.println("기타 예외");
            }
        }
        finally {
            assertNotNull(work, "작업이 완료되지 않았습니다.");
        }
    }

    @DisplayName("1차 결재 테스트2")
    @Test
    @Transactional
    public void firstLevelApproval2(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        try {
            work = deferWork(work, firstApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            if (massage.equals("결재 권한이 없습니다.")) {
                int index = work.getApprovers().size();
                System.out.println("결재자 권한 : " + firstApprovalStaff.getPosition().getPositionName());
            } else {
                System.out.println("기타 예외");
            }
        }
        finally {
            assertNotNull(work, "작업이 완료되지 않았습니다.");
        }
    }
    @DisplayName("1차 결재 테스트3")
    @Test
    @Transactional
    public void firstLevelApproval3(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        try {
            work = rejectWork(work, firstApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            if (massage.equals("결재 권한이 없습니다.")) {
                int index = work.getApprovers().size();
                System.out.println("결재자 권한 : " + firstApprovalStaff.getPosition().getPositionName());
            } else {
                System.out.println("기타 예외");
            }
        }
        finally {
            assertNotNull(work, "작업이 완료되지 않았습니다.");
        }
    }

    @DisplayName("1차 결재 테스트4")
    @Test
    @Transactional
    public void firstLevelApproval4(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        try {
            work = arbitraryWork(work, firstApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            if (massage.equals("결재 권한이 없습니다.")) {
                int index = work.getApprovers().size();
                System.out.println("결재자 권한 : " + firstApprovalStaff.getPosition().getPositionName());
            } else {
                System.out.println("기타 예외");
            }
        }
        finally {
            assertNotNull(work, "작업이 완료되지 않았습니다.");
        }
    }

    @DisplayName("1차 결재 테스트5")
    @Test
    @Transactional
    public void firstLevelApproval5(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[6]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        try {
            work = proxyWork(work, firstApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            if (massage.equals("결재 권한이 없습니다.")) {
                int index = work.getApprovers().size();
                System.out.println("결재자 권한 : " + firstApprovalStaff.getPosition().getPositionName());
            } else {
                System.out.println("기타 예외");
            }
        }
        finally {
            assertNotNull(work, "작업이 완료되지 않았습니다.");
        }
    }

    @DisplayName("1차 결재 테스트6")
    @Test
    @Transactional
    public void firstLevelApproval6(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        try {
            work = arbitraryProxyWork(work, firstApprovalStaff);
            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            if (massage.equals("결재 권한이 없습니다.")) {
                int index = work.getApprovers().size();
                System.out.println("결재자 권한 : " + firstApprovalStaff.getPosition().getPositionName());
            } else {
                System.out.println("기타 예외");
            }
        }
        finally {
            assertNotNull(work, "작업이 완료되지 않았습니다.");
        }
    }

    @DisplayName("1차 결재 테스트7")
    @Test
    @Transactional
    public void firstLevelApproval7(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[6]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        try {
            work = approveWork(work, firstApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            if (massage.equals("결재 권한이 없습니다.")) {
                int index = work.getApprovers().size();
                System.out.println("결재하려는 사람의 권한 : " + firstApprovalStaff.getPosition().getPositionName());
            } else {
                System.out.println("기타 예외");
            }
        }
    }

    @DisplayName("1차 결재된 문서는 수정이 되는가?")
    @Test
    @Transactional
    public void updateFirstLevelApprovalWork(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");

        work = approveWork(work, firstApprovalStaff);

        displayApprovalWork(work);

        try {
            work = updateWork(work, "제목 수정", "보존년한 수정", "보안등급 수정");
            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            String massage = e.getMessage();
            System.out.println("수정할 수 없는 문서입니다.");
            System.out.println("사유 : (" + massage + ")");
        }
    }

    // 2차 결재
    @DisplayName("2차 결재 권한 테스트1")
    @Test
    @Transactional
    public void checkSecondLevelAuthority1(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[3]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);
        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE));
        assertTrue(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE), "결재 권한이 없습니다.");
    }

    @DisplayName("2차 결재 권한 테스트2")
    @Test
    @Transactional
    public void checkSecondLevelAuthority2(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[5]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[3]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[2]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE));
        assertTrue(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE), "결재 권한이 없습니다.");
    }

    @DisplayName("2차 결재 권한 테스트3")
    @Test
    @Transactional
    public void checkSecondLevelAuthority3(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[5]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[3]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE));
        assertFalse(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE), "결재 권한이 있습니다.");
    }

    @DisplayName("2차 결재 권한 테스트4")
    @Test
    @Transactional
    public void checkSecondLevelAuthority4(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[3]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[2]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE));
        assertTrue(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE), "결재 권한이 없습니다.");
    }

    @DisplayName("2차 결재 권한 테스트5")
    @Test
    @Transactional
    public void checkSecondLevelAuthority5(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[3]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[2]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[0]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE));
        assertFalse(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.APPROVE), "결재 권한이 있습니다.");
    }

    @DisplayName("2차 결재 권한 테스트6")
    @Test
    @Transactional
    public void checkSecondLevelAuthority6(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[3]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[2]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[0]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.PROXY.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY));
        assertTrue(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY), "결재 권한이 없습니다.");
    }

    @DisplayName("2차 결재 권한 테스트7")
    @Test
    @Transactional
    public void checkSecondLevelAuthority7(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[7]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[0]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.PROXY.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY));
        assertFalse(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY), "결재 권한이 있습니다.");
    }

    @DisplayName("2차 결재 권한 테스트8")
    @Test
    @Transactional
    public void checkSecondLevelAuthority8(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[3]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[2]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[0]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.PROXY.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY));
        assertTrue(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY), "결재 권한이 없습니다.");
    }

    @DisplayName("2차 결재 권한 테스트9")
    @Test
    @Transactional
    public void checkSecondLevelAuthority9(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[2]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[1]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 번호 : " +  draftStaff.getPosition().getPositionId());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  firstApprovalStaff.getPosition().getPositionId());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 번호 : " +  secondApprovalStaff.getPosition().getPositionId());
        System.out.println("결재 예정 상태 : " + ApprovalState.PROXY.getCode());
        System.out.println("2차 결재 권한 여부 : " + checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY));
        assertTrue(checkApprovalAuthority(work, secondApprovalStaff, ApprovalState.PROXY), "결재 권한이 없습니다.");
    }

    @DisplayName("2차 결재 테스트 1")
    @Test
    @Transactional
    public void secondLevelApproval1(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[7]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[3]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = approveWork(work, secondApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("주임"), "주임이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("과장"), "과장이 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장이 아닙니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("2차 결재 테스트 2")
    @Test
    @Transactional
    public void secondLevelApproval2(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[7]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[2]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = approveWork(work, secondApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("주임"), "주임이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("과장"), "과장이 아닙니다.");
            assertNotEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장입니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("2차 결재 테스트 3")
    @Test
    @Transactional
    public void secondLevelApproval3(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[7]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = proxyWork(work, secondApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("주임"), "주임이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("과장"), "과장이 아닙니다.");
            assertNotEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장입니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("2차 결재 테스트 4")
    @Test
    @Transactional
    public void secondLevelApproval4(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[3]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[2]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = approveWork(work, secondApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("상무이사"), "상무이사가 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부사장"), "부사장이 아닙니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("2차 결재 테스트 5")
    @Test
    @Transactional
    public void secondLevelApproval5(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[2]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[1]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[0]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = approveWork(work, secondApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("상무이사"), "상무이사가 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("부사장"), "부사장이 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("사장"), "사장이 아닙니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("2차 결재 테스트 6")
    @Test
    @Transactional
    public void secondLevelApproval6(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[2]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[1]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = proxyWork(work, secondApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("상무이사"), "상무이사가 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("부사장"), "부사장이 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부사장"), "부사장이 아닙니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("2차 결재 테스트 7")
    @Test
    @Transactional
    public void secondLevelApproval7(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[5]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[3]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = proxyWork(work, secondApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("과장"), "과장이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장이 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부사장"), "부사장이 아닙니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("3차 결재 권한 테스트1")
    @Test
    @Transactional
    public void checkFinalLevelAuthority1(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[3]);
        Staff finalApprovalStaff = makeStaff("asica6", "홍대장", Gender.MALE, STAFF_INFO[2]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);
        work = approveWork(work, secondApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 우선순위 : " +  draftStaff.getPosition().getPositionRank());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  firstApprovalStaff.getPosition().getPositionRank());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  secondApprovalStaff.getPosition().getPositionRank());
        System.out.println("2차 결재 상태 : " + work.getApprovers().get(1).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("3차 결재자 직급 : " + finalApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  finalApprovalStaff.getPosition().getPositionRank());
        System.out.println("3차 결재 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("3차 결재 권한 여부 : " + checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE));
        assertTrue(checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE), "결재 권한이 없습니다.");
    }

    @DisplayName("3차 결재 권한 테스트2")
    @Test
    @Transactional
    public void checkFinalLevelAuthority2(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[5]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[3]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[2]);
        Staff finalApprovalStaff = makeStaff("asica6", "홍대장", Gender.MALE, STAFF_INFO[1]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);
        work = approveWork(work, secondApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 우선순위 : " +  draftStaff.getPosition().getPositionRank());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  firstApprovalStaff.getPosition().getPositionRank());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  secondApprovalStaff.getPosition().getPositionRank());
        System.out.println("2차 결재 상태 : " + work.getApprovers().get(1).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("3차 결재자 직급 : " + finalApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  finalApprovalStaff.getPosition().getPositionRank());
        System.out.println("3차 결재 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("3차 결재 권한 여부 : " + checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE));
        assertTrue(checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE), "결재가 불가능합니다.");
    }

    @DisplayName("3차 결재 권한 테스트3")
    @Test
    @Transactional
    public void checkFinalLevelAuthority3(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[4]);
        Staff finalApprovalStaff = makeStaff("asica6", "홍대장", Gender.MALE, STAFF_INFO[1]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);
        work = proxyWork(work, secondApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 우선순위 : " +  draftStaff.getPosition().getPositionRank());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  firstApprovalStaff.getPosition().getPositionRank());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  secondApprovalStaff.getPosition().getPositionRank());
        System.out.println("2차 결재 상태 : " + work.getApprovers().get(1).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("3차 결재자 직급 : " + finalApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  finalApprovalStaff.getPosition().getPositionRank());
        System.out.println("3차 결재 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("3차 결재 권한 여부 : " + checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE));
        assertFalse(checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE), "결재 권한이 있습니다.");
    }

    @DisplayName("3차 결재 권한 테스트4")
    @Test
    @Transactional
    public void checkFinalLevelAuthority4(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[5]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[3]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Staff finalApprovalStaff = makeStaff("asica6", "홍대장", Gender.MALE, STAFF_INFO[0]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);
        work = proxyWork(work, secondApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 우선순위 : " +  draftStaff.getPosition().getPositionRank());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  firstApprovalStaff.getPosition().getPositionRank());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  secondApprovalStaff.getPosition().getPositionRank());
        System.out.println("2차 결재 상태 : " + work.getApprovers().get(1).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("3차 결재자 직급 : " + finalApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  finalApprovalStaff.getPosition().getPositionRank());
        System.out.println("3차 결재 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("3차 결재 권한 여부 : " + checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE));
        assertFalse(checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE), "결재가 가능합니다.");
    }

    @DisplayName("3차 결재 권한 테스트5")
    @Test
    @Transactional
    public void checkFinalLevelAuthority5(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[8]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[3]);
        Staff thirdApprovalStaff = makeStaff("asica6", "홍이사", Gender.MALE, STAFF_INFO[2]);
        Staff finalApprovalStaff = makeStaff("asica7", "홍부사장", Gender.MALE, STAFF_INFO[1]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        work = approveWork(work, firstApprovalStaff);
        work = approveWork(work, secondApprovalStaff);
        work = approveWork(work, thirdApprovalStaff);

        System.out.println("기안서 작성자 직급 : " + draftStaff.getPosition().getPositionName() + ", 우선순위 : " +  draftStaff.getPosition().getPositionRank());
        System.out.println("-----");
        System.out.println("1차 결재자 직급 : " + firstApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  firstApprovalStaff.getPosition().getPositionRank());
        System.out.println("1차 결재 상태 : " + work.getApprovers().get(0).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("2차 결재자 직급 : " + secondApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  secondApprovalStaff.getPosition().getPositionRank());
        System.out.println("2차 결재 상태 : " + work.getApprovers().get(1).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("3차 결재자 직급 : " + thirdApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  thirdApprovalStaff.getPosition().getPositionRank());
        System.out.println("3차 결재 상태 : " + work.getApprovers().get(2).getApprovalState().getCode());
        System.out.println("-----");
        System.out.println("4차 결재자 직급 : " + finalApprovalStaff.getPosition().getPositionName() + ", 우선순위 : " +  finalApprovalStaff.getPosition().getPositionRank());
        System.out.println("4차 결재 상태 : " + ApprovalState.APPROVE.getCode());
        System.out.println("4차 결재 권한 여부 : " + checkApprovalAuthority(work, thirdApprovalStaff, ApprovalState.APPROVE));
        assertFalse(checkApprovalAuthority(work, finalApprovalStaff, ApprovalState.APPROVE), "결재 권한이 있습니다.");
    }

    @DisplayName("3차 결재 테스트 1")
    @Test
    @Transactional
    public void thirdLevelApproval1(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[7]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[5]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[3]);
        Staff thirdApprovalStaff = makeStaff("asica6", "홍경영", Gender.MALE, STAFF_INFO[2]);
        
        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = approveWork(work, secondApprovalStaff);
            work = approveWork(work, thirdApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("주임"), "주임이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("과장"), "과장이 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장이 아닙니다.");
            assertEquals(thirdApprovalStaff.getPosition(), positionRepository.findByPositionName("상무이사"), "상무이사가 아닙니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("3차 결재 테스트 2")
    @Test
    @Transactional
    public void thirdLevelApproval2(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[4]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[3]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[2]);
        Staff thirdApprovalStaff = makeStaff("asica6", "홍경영", Gender.MALE, STAFF_INFO[1]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = approveWork(work, secondApprovalStaff);
            work = approveWork(work, thirdApprovalStaff);

            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("차장"), "차장이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장이 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("상무이사"), "상무이사가 아닙니다.");
            assertEquals(thirdApprovalStaff.getPosition(), positionRepository.findByPositionName("부사장"), "부사장이 아닙니다.");
            assertNotNull(work);
        }
    }

    @DisplayName("3차 결재 테스트 3")
    @Test
    @Transactional
    public void thirdLevelApproval3(){
        // STAFF_INFO 인덱스에 해당하는 직위
        // 0 : 사장, 1 : 부사장, 2 : 상무이사
        // 3 : 부장, 4 : 차장, 5 : 과장, 6 : 대리, 7 : 주임, 8 : 사원, 9 : 인턴
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Staff draftStaff = makeStaff("asica3", "홍길동", Gender.MALE, STAFF_INFO[3]);
        Staff firstApprovalStaff = makeStaff("asica4", "홍길순", Gender.FEMALE, STAFF_INFO[2]);
        Staff secondApprovalStaff = makeStaff("asica5", "홍길강", Gender.MALE, STAFF_INFO[1]);
        Staff thirdApprovalStaff = makeStaff("asica6", "홍경영", Gender.MALE, STAFF_INFO[0]);

        Work work = createWork(workType, draftStaff, "제목", "보존년한", "보안등급");
        try {
            work = approveWork(work, firstApprovalStaff);
            work = approveWork(work, secondApprovalStaff);
            boolean canApproval = checkApprovalAuthority(work, thirdApprovalStaff, ApprovalState.APPROVE);

            if(canApproval){
                work = approveWork(work, thirdApprovalStaff);
            }
            else {
                System.out.println("이미 결재가 완료되었습니다.");
            }
            displayApprovalWork(work);
        }
        catch (UnauthorizedException e){
            System.out.println(e.getMessage());
        }
        finally {
            assertEquals(draftStaff.getPosition(), positionRepository.findByPositionName("부장"), "부장이 아닙니다.");
            assertEquals(firstApprovalStaff.getPosition(), positionRepository.findByPositionName("상무이사"), "상무이사가 아닙니다.");
            assertEquals(secondApprovalStaff.getPosition(), positionRepository.findByPositionName("부사장"), "부사장가 아닙니다.");
            assertEquals(thirdApprovalStaff.getPosition(), positionRepository.findByPositionName("사장"), "사장이 아닙니다.");
            assertNotNull(work);
        }
    }
}