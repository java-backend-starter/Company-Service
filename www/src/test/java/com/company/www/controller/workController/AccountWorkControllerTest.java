package com.company.www.controller.workController;

import com.company.www.constant.staff.Gender;
import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.account.*;
import com.company.www.exception.UnauthorizedException;
import com.company.www.repository.belong.*;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.account.*;
import com.company.www.repository.work.sales.CreditRepository;
import com.company.www.repository.work.sales.PatronRepository;
import com.company.www.repository.work.sales.UnitRepository;
import com.company.www.service.staff.StaffInfoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@SpringBootTest
class AccountWorkControllerTest { 
    
    // 원가중심점, 내부오더, 세무 테스트 : 추후 구현함에 따라 테스트 예정
    // 실제원가 테스트 : 영업부 업무인 견적서와 연계해서 구현 예정
    
    @Autowired
    StaffRepository staffRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    StaffInfoService staffInfoService;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    WorkTypeRepository workTypeRepository;
    @Autowired
    WorkRepository workRepository;

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    PropertyRepository propertyRepository;
    @Autowired
    BalanceSheetRepository balanceSheetRepository;
    @Autowired
    IncomeStatementRepository incomeStatementRepository;
    @Autowired
    ComprehensiveIncomeStatementRepository comprehensiveIncomeStatementRepository;
    @Autowired
    EquityChangeStatementRepository equityChangeStatementRepository;
    @Autowired
    EquityChangeRepository equityChangeRepository;
    @Autowired
    CashFlowStatementRepository cashFlowStatementRepository;
    @Autowired
    CashFlowRepository cashFlowRepository;
    @Autowired
    BudgetRepository budgetRepository;
    @Autowired
    BudgetPlanRepository budgetPlanRepository;
    @Autowired
    BudgetItemRepository budgetItemRepository;
    @Autowired
    AdditionalBudgetRepository additionalBudgetRepository;
    @Autowired
    AdditionalBudgetItemRepository additionalBudgetItemRepository;
    @Autowired
    DebtRepository debtRepository;
    @Autowired
    DebtHistoryRepository debtHistoryRepository;
    @Autowired
    AssetTypeRepository assetTypeRepository;
    @Autowired
    AssetRepository assetRepository;
    @Autowired
    AssetHistoryRepository assetHistoryRepository;
    @Autowired
    StandardCostRepository standardCostRepository;
    @Autowired
    ActualCostRepository actualCostRepository;
    @Autowired
    UnitRepository unitRepository;
    @Autowired
    CostCenterRepository costCenterRepository;
    @Autowired
    InnerOrderRepository innerOrderRepository;
    @Autowired
    ProfitabilityAnalysisRepository profitabilityAnalysisRepository;

    @Autowired
    PatronRepository patronRepository; // 단위 테스트에서 미사용, 통합 테스트할 때 사용 예정
    @Autowired
    CreditRepository creditRepository; // 단위 테스트에서 미사용, 통합 테스트할 때 사용 예정

    private final static List<String> propertyType = List.of("자산", "부채", "자본");

    private final static List<String> assetType = List.of(
            "현금", "유가증권", "외상매출금", "미수금", "기타 유동자산", "장기성예금",
            "투자주식", "투자부동산", "토지", "건물", "기계장치", "비품", "제품", "기타 비유동자산"
    );
    private final static List<String> liabilityType = List.of(
            "단기차입금", "외상매입금", "미지급금", "선수금", "기타 유동부채",
            "사채", "장기차입금", "퇴직금급여충당부채", "기타 비유동부채"
    );
    private final static List<String> capitalType = List.of(
            "자본금", "자본잉여금", "자본조정", "미처분이익잉여금", "기타포괄손익누계액"
    );

    private final static String [][] staffInfo = {
            { "경영진", "사장", "경영진", "경영진" },
            { "경영진", "부사장", "경영진", "경영진" },
            { "경영진", "전무이사", "경영진", "경영진" },
            { "재무회계", "상무이사", "경영진", "경영진" },
            { "재무회계", "이사", "경영진", "경영진" },
            { "재무회계", "부장", "재무회계부", "미정" },
            { "재무회계", "차장", "재무회계부", "미정" },
            { "재무회계", "과장", "재무회계부", "재무과" },
            { "재무회계", "대리", "재무회계부", "재무과" },
            { "재무회계", "주임", "재무회계부", "재무과" },
            { "재무회계", "사원", "재무회계부", "재무과" },
            { "재무회계", "인턴", "재무회계부", "재무과" }
    };

    private final static String [][] human_resource = {
            { "인사", "부장", "인사부", "미정" },
            { "인사", "차장", "인사부", "미정" },
            { "인사", "과장", "인사부", "인사 1과" },
            { "인사", "대리", "인사부", "인사 1과" },
            { "인사", "주임", "인사부", "인사 1과" },
            { "인사", "사원", "인사부", "인사 1과" },
            { "인사", "인턴", "인사부", "인사 1과" }
    };

    public List<WorkType> filter(WorkTypeRepository workTypeRepository, String ... types){
        List<WorkType> filtered = new ArrayList<>();
        for(String type : types ){
            filtered.add(workTypeRepository.findByWorkName(type));
        }
        return filtered;
    }

    public Staff makeStaff(String id, String name, Gender gender, String [] info){
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

        staff.setRole(roleRepository.findByRoleName(info[0]));
        staff.setPosition(positionRepository.findByPositionName(info[1]));
        staff.setDepartment(departmentRepository.findByDepartmentName(info[2]));
        staff.setSection(sectionRepository.findBySectionName(info[3]));

        return staffRepository.save(staff);
    }

    public Work createWork(Staff staff, WorkType workType){
        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        return workRepository.save(work);
    }



    public void displayWork(Work work){

    }

    public boolean checkAuthorization(Staff staff, Work work){
        String requestStaff = work.getDraftStaff().getUserId();
        String responseStaff = staff.getUserId();

        if(requestStaff.equals(responseStaff)){
            return false;
        }

        long requestStaffPosition = work.getDraftStaff().getPosition().getPositionId();
        long responseStaffPosition = staff.getPosition().getPositionId();

        // 1 : 미정, 2 : 사장, 3 : 부사장, 4 : 전무이사, 5 : 전무이사, 6 : 이사,
        // 7 : 부장, 8 : 차장, 9 : 과장, 10 : 대리, 11 : 주임, 12 : 사원,  13 : 인턴
        boolean isNotAuthority = (requestStaffPosition == 1 || responseStaffPosition == 1);
        boolean isPositionEqual = (requestStaffPosition == responseStaffPosition); // 동일 직급 확인
        boolean isDirectorRequest = (requestStaffPosition >= 2 && requestStaffPosition <= 6);
        boolean isDirectorResponse = (responseStaffPosition >= 2 && responseStaffPosition <= 6);

        if(isNotAuthority || isPositionEqual){
            return false;
        }
        else if(isDirectorRequest){
            return isDirectorResponse;
        }
        else if(requestStaffPosition >= 12 ){
            return (responseStaffPosition > 1 && responseStaffPosition < 12);
        }
        else {
            return (requestStaffPosition > responseStaffPosition);
        }
    }

    public Work changeWorkState(Staff staff, Work work, ApprovalState state){
        if(checkAuthorization(staff, work)){
            return  workRepository.save(work);
        }
        else{
            throw new UnauthorizedException("결재할 권한이 없습니다.");
        }
    }

    public Long approveWork(Staff staff, Work work){
        return changeWorkState(staff, work, ApprovalState.APPROVE).getWorkId();
    }

    public Long deferWork(Staff staff, Work work){
        return changeWorkState(staff, work, ApprovalState.DEFER).getWorkId();
    }

    public Long rejectWork(Staff staff, Work work){
        return changeWorkState(staff, work, ApprovalState.REJECT).getWorkId();
    }

    public void displayInfo(Staff staff){
        System.out.println("-----");
        System.out.println("이름 : " + staff.getStaffName());
        System.out.println("성별 : " + staff.getGender().getCode());
        System.out.println("-----");
        System.out.println("직책 : " + staff.getRole().getRoleName());
        System.out.println("직위 : " + staff.getPosition().getPositionName());
        System.out.println("부서 : " + staff.getDepartment().getDepartmentName());
        System.out.println("과 : " + staff.getSection().getSectionName());
        System.out.println("-----");
    }

    @DisplayName("결재권한 확인 : 경영진 | 경영진")
    @Test
    @Transactional
    public void checkAuthority1(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]); // 사장
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[1]); // 부사장
        displayInfo(staff1);
        displayInfo(staff2);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Work work = createWork(staff1, workType);

        System.out.println("동일인 여부 : " + staff1.getUserId().equals(staff2.getUserId()));
        System.out.println("결재 권한 존재? : " + checkAuthorization(staff2, work));
    }

    @DisplayName("결재권한 확인 : 경영진 | 부장 이하")
    @Test
    @Transactional
    public void checkAuthority2(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]); // 사장
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[3]); // 차장
        displayInfo(staff1);
        displayInfo(staff2);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Work work = createWork(staff1, workType);

        System.out.println("동일인 여부 : " + staff1.getUserId().equals(staff2.getUserId()));
        System.out.println("결재 권한 존재? : " + checkAuthorization(staff2, work));
    }

    @DisplayName("결재권한 확인 : 프리랜서 | 경영진")
    @Test
    @Transactional
    public void checkAuthority3(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[10]); // 프리랜서
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]); // 사장
        displayInfo(staff1);
        displayInfo(staff2);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Work work = createWork(staff1, workType);

        System.out.println("동일인 여부 : " + staff1.getUserId().equals(staff2.getUserId()));
        System.out.println("결재 권한 존재? : " + checkAuthorization(staff2, work));
    }

    @DisplayName("결재권한 확인 : 프리랜서 | 부장이하")
    @Test
    @Transactional
    public void checkAuthority4(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[10]); // 프리랜서
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[6]); // 주임
        displayInfo(staff1);
        displayInfo(staff2);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Work work = createWork(staff1, workType);

        System.out.println("동일인 여부 : " + staff1.getUserId().equals(staff2.getUserId()));
        System.out.println("결재 권한 존재? : " + checkAuthorization(staff2, work));
    }

    @DisplayName("결재권한 확인 : 부장 이하 | 부장이하")
    @Test
    @Transactional
    public void checkAuthority5(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[3]); // 차장
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[2]); // 부장
        displayInfo(staff1);
        displayInfo(staff2);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Work work = createWork(staff1, workType);

        System.out.println("동일인 여부 : " + staff1.getUserId().equals(staff2.getUserId()));
        System.out.println("결재 권한 존재? : " + checkAuthorization(staff2, work));
    }

    @DisplayName("결재권한 확인 : 부장 이하 | 부장이하, 결재하려는 사람이 하급자인 경우")
    @Test
    @Transactional
    public void checkAuthority6(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]); // 부장
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[3]); // 차장
        displayInfo(staff1);
        displayInfo(staff2);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Work work = createWork(staff1, workType);

        // approveWork(staff2, work);
        System.out.println("동일인 여부 : " + staff1.getUserId().equals(staff2.getUserId()));
        System.out.println("결재 권한 존재? : " + checkAuthorization(staff2, work));
    }

    @DisplayName("결재권한 확인 : 부장 이하(사원, 수습사원, 인턴) | 부장이하")
    @Test
    @Transactional
    public void checkAuthority7(){
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[8]); // 수습 사원
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[3]); // 차장
        displayInfo(staff1);
        displayInfo(staff2);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Work work = createWork(staff1, workType);

        approveWork(staff2, work);
        System.out.println("동일인 여부 : " + staff1.getUserId().equals(staff2.getUserId()));
        System.out.println("결재 권한 존재? : " + checkAuthorization(staff2, work));
    }

    @DisplayName("업무 분류 출력")
    @Test
    @Transactional
    public void accountWork(){
        List<WorkType> workTypes = workTypeRepository.findAllByRole(roleRepository.findByRoleName("재무회계"));
        workTypes.removeAll(filter(workTypeRepository,
                        "재무회계부 업무", "거래", "자산", "부채", "자본", "재무제표", "손익계산서", "포괄손익계산서", "자본변동표", "현금흐름표",
                        "예산 계획", "추가 예산 계획", "법인세", "근로소득세", "부가가치세", "채권 상환", "채무 변제", "고정자산이력",
                        "표준원가", "실제원가", "원가중심점", "내부오더"
                )
        );

        for(WorkType workType : workTypes){
            System.out.println("name : " + workType.getWorkName() + ", role : " + workType.getRole().getRoleName() + ", link : " + workType.getWorkLink());
        }
    }


    @DisplayName("세부 업무 출력")
    @Test
    @Transactional
    public void detailedWork(){
        Map<String, String> works = new LinkedHashMap<>();
        works.put("거래", "/financial/transaction");
        works.put("자산", "/financial/asset");
        works.put("부채", "/financial/liability");
        works.put("자본", "/financial/capital");
        works.put("재산목록", "/financial/list");
        works.put("재무제표", "/financial/balance_sheet");
        works.put("손익계산서", "/financial/income");
        works.put("포괄손익계산서", "/financial/comprehensive_income");
        works.put("자본변동표", "/financial/equity_change");
        works.put("현금흐름표", "/financial/cash_flow");

        for(String work : works.keySet()){
            System.out.println("세부 업무 : " + work);
        }

        for(String link : works.values()){
            System.out.println("링크 : " + link);
        }
    }

    // 거래 테스트
    public Transaction createTransaction(Staff staff, WorkType workType, String item, LocalDate localDate){
        Work work = createWork(staff, workType);

        Transaction transaction = new Transaction();

        transaction.setWork(work);
        transaction.setItem(item);
        transaction.setDate(localDate);
        transaction.setDebitType("차변");
        transaction.setDebit(10000);
        transaction.setDebitStatement("차변 테스트");
        transaction.setCreditType("대변");
        transaction.setCredit(10000);
        transaction.setCreditStatement("대변 태스트");
        transaction.setStatement("거래 테스트");
        return transactionRepository.save(transaction);
    }

    public void updateTransaction(Staff staff, Transaction transaction, String item, LocalDate localDate){
        if(staff.getUserId().equals(transaction.getWork().getDraftStaff().getUserId())){
            transaction.setItem(item);
            transaction.setDate(localDate);
            transaction.setDebitType("차변");
            transaction.setDebit(10000);
            transaction.setDebitStatement("차변 테스트");
            transaction.setCreditType("대변");
            transaction.setCredit(10000);
            transaction.setCreditStatement("대변 태스트");
            transaction.setStatement("거래 테스트");
            transactionRepository.save(transaction);
        }
    }

    public void displayTransaction(Transaction transaction){
        System.out.println("날짜 : " + transaction.getDate());
        System.out.println("항목 : " + transaction.getItem());
        System.out.println("----------");
    }

    @DisplayName("거래 데이터 출력")
    @Test
    @Transactional
    public void transaction(){
        WorkType workType = workTypeRepository.findByWorkName("거래");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);


    }

    @DisplayName("거래 데이터 수정")
    @Test
    @Transactional
    public void updateTransaction(){
        WorkType workType = workTypeRepository.findByWorkName("거래");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Transaction created = createTransaction(staff, workType, item, date);

        Transaction transaction = transactionRepository.findByWork(created.getWork());
        updateTransaction(staff, transaction, "항목 수정", date.plusDays(5));

        transaction = transactionRepository.findByWork(created.getWork());
        displayTransaction(transaction);
    }

    @DisplayName("거래 데이터 승인&출력")
    @Test
    @Transactional
    public void allowTransaction(){
        WorkType workType = workTypeRepository.findByWorkName("거래");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Transaction created = createTransaction(staff1, workType, item, date);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = approveWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayTransaction(transactionRepository.findByWork(work));
    }

    @DisplayName("거래 데이터 보류&출력")
    @Test
    @Transactional
    public void delayTransaction(){
        WorkType workType = workTypeRepository.findByWorkName("거래");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Transaction created = createTransaction(staff1, workType, item, date);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = deferWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayTransaction(transactionRepository.findByWork(work));
    }

    @DisplayName("거래 데이터 거절&출력")
    @Test
    @Transactional
    public void rejectTransaction(){
        WorkType workType = workTypeRepository.findByWorkName("거래");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Transaction created = createTransaction(staff1, workType, item, date);
        Work work1 = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work1);

        Long workId = rejectWork(staff2, work1);
        Work work2 = workRepository.findByWorkId(workId);
        Transaction transaction = transactionRepository.findByWork(work2);

        displayWork(work2);
        displayTransaction(transaction);
    }

    // 재산 테스트
    public Property createProperty(Work work, String name, String type, String detailedType){
        if(type.equals("자산") || type.equals("자본") || type.equals("부채")) {
            LocalDate start = LocalDate.of(2024, 8, 1);
            LocalDate end = LocalDate.of(2024, 8, 31);
            Property property = new Property();
            property.setWork(work);
            property.setName(name);
            property.setType(type);
            property.setDetailedType(detailedType);
            property.setClosingStart(start);
            property.setClosingEnd(end);
            property.setStatement("입력완료");
            return propertyRepository.save(property);
        }
        else {
            return null;
        }
    }

    public Property updateProperty(Property property, String name, String type, String detailedType){
        if(type.equals("자산") || type.equals("자본") || type.equals("부채")) {
            LocalDate start = LocalDate.of(2024, 8, 1);
            LocalDate end = LocalDate.of(2024, 8, 31);
            
            property.setName(name);
            property.setType(type);
            property.setDetailedType(detailedType);
            property.setAmount(property.getAmount() + 10000);
            property.setClosingStart(start);
            property.setClosingEnd(end);
            property.setStatement("수정완료");
            return propertyRepository.save(property);
        }
        else {
            return null;
        }
    }

    public Work deleteProperty(Property property, Work work){
        if(property != null){
            propertyRepository.delete(property);
            work.setDraftState(WorkState.DELETE);
            return workRepository.save(work);
        }
        return null;
    }

    public void displayProperty(Property property){
        System.out.println("이름 : " + property.getName());
        System.out.println("유형 : " + property.getType());
        System.out.println("세부 유형 : " + property.getDetailedType());
        System.out.println("금액 : " + property.getAmount());
        System.out.println("시작 : " + property.getClosingStart());
        System.out.println("종료 : " + property.getClosingEnd());
    }

    @DisplayName("재산(자산, 자본, 부채) 추가")
    @Test
    @Transactional
    public void createProperty(){
        WorkType workType = workTypeRepository.findByWorkName("자산");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();
        Work work;
        List<Property> properties = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            String item = "항목" + (i+1);
            work = createWork(staff, workType);
            properties.add(createProperty(work, ("이름" + (i+1)), workType.getWorkName(), "세부 타입"));
        }

        List<Work> works = workRepository.findAll();
        properties = new ArrayList<>();
        for(Work w : works){
            properties.add(propertyRepository.findByWork(w));
        }
        System.out.println("검색이 안되었는지 ? " + properties.isEmpty() + " 검색 건수 : " + properties.size());
        for(Property property : properties){
            displayProperty(property);
            displayWork(property.getWork());
        }
    }

    @DisplayName("재산(자산, 자본, 부채) 데이터 수정")
    @Test
    @Transactional
    public void updateProperty(){
        WorkType workType = workTypeRepository.findByWorkName("자산");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Property created = createProperty(createWork(staff, workType), item, workType.getWorkName(), "세부 타입");

        Property property = propertyRepository.findByWork(created.getWork());
        updateProperty(property, "항목 수정", created.getType(), "세부 항목 수정");

        property = propertyRepository.findByWork(created.getWork());
        displayProperty(property);
    }

    @DisplayName("재산(자산, 자본, 부채) 데이터 승인&출력")
    @Test
    @Transactional
    public void allowProperty(){
        WorkType workType = workTypeRepository.findByWorkName("자산");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Property created = createProperty(createWork(staff1, workType), item, workType.getWorkName(), "세부 항목");
        Work work = created.getWork();
        displayWork(work);

        Long workId = approveWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayProperty(propertyRepository.findByWork(work));
    }

    @DisplayName("재산(자산, 자본, 부채) 데이터 보류&출력")
    @Test
    @Transactional
    public void delayProperty(){
        WorkType workType = workTypeRepository.findByWorkName("자산");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Property created = createProperty(createWork(staff1, workType), item, workType.getWorkName(), "세부 항목");
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = deferWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayProperty(propertyRepository.findByWork(work));
    }

    @DisplayName("재산(자산, 자본, 부채) 데이터 거절&출력")
    @Test
    @Transactional
    public void rejectProperty(){
        WorkType workType = workTypeRepository.findByWorkName("자산");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        String item = "항목" + (1);
        Property created = createProperty(createWork(staff1, workType), item, workType.getWorkName(), "세부 항목");
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = rejectWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayProperty(propertyRepository.findByWork(work));
    }

    // 제무재표 테스트
    public BalanceSheet createBalanceSheet(Work work, long addition){
        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setWork(work);
        balanceSheet.setName("제무재표");
        LocalDate bsDate = LocalDate.now();
        balanceSheet.setDate(bsDate);

        balanceSheet.setCash(1000000 + addition);
        balanceSheet.setSecurities(1000000 + addition);
        balanceSheet.setReceivable(1000000 + addition);
        balanceSheet.setReceivableAccount(1000000 + addition);
        balanceSheet.setOtherLiquidAsset(1000000 + addition);

        balanceSheet.setLongTermDeposit(1000000 + addition);
        balanceSheet.setStock(1000000 + addition);
        balanceSheet.setProperty(1000000 + addition);

        balanceSheet.setGround(1000000 + addition);
        balanceSheet.setBuilding(1000000 + addition);
        balanceSheet.setMachine(1000000 + addition);
        balanceSheet.setEquipment(1000000 + addition);

        balanceSheet.setProduct(1000000 + addition);
        balanceSheet.setOtherFixedAsset(1000000 + addition);

        balanceSheet.setShortTermBorrowing(1000000 + addition);
        balanceSheet.setPayable(1000000 + addition);
        balanceSheet.setPayableAccount(1000000 + addition);
        balanceSheet.setAdvanceReceived(1000000 + addition);
        balanceSheet.setOtherLiquidLiability(1000000 + addition);

        balanceSheet.setPrivateLoan(1000000 + addition);
        balanceSheet.setLongTermBorrowing(1000000 + addition);
        balanceSheet.setSeverancePay(1000000 + addition);
        balanceSheet.setOtherFixedLiability(1000000 + addition);

        balanceSheet.setCapital(1000000 + addition);
        balanceSheet.setSurplus(1000000 + addition);
        balanceSheet.setAdjustment(1000000 + addition);
        balanceSheet.setUnappropriated(1000000 + addition);
        balanceSheet.setComprehensive(1000000 + addition);

        return balanceSheetRepository.save(balanceSheet);
    }

    public BalanceSheet updateBalanceSheet(BalanceSheet balanceSheet, String name, long addition){
        balanceSheet.setName(name);
        LocalDate bsDate = LocalDate.now();
        balanceSheet.setDate(bsDate);

        balanceSheet.setCash(1500000 + addition);
        balanceSheet.setSecurities(1500000 + addition);
        balanceSheet.setReceivable(1500000 + addition);
        balanceSheet.setReceivableAccount(1500000 + addition);
        balanceSheet.setOtherLiquidAsset(1500000 + addition);

        balanceSheet.setLongTermDeposit(1500000 + addition);
        balanceSheet.setStock(1500000 + addition);
        balanceSheet.setProperty(1500000 + addition);

        balanceSheet.setGround(1500000 + addition);
        balanceSheet.setBuilding(1500000 + addition);
        balanceSheet.setMachine(1500000 + addition);
        balanceSheet.setEquipment(1500000 + addition);

        balanceSheet.setProduct(1500000 + addition);
        balanceSheet.setOtherFixedAsset(1500000 + addition);

        balanceSheet.setShortTermBorrowing(1500000 + addition);
        balanceSheet.setPayable(1500000 + addition);
        balanceSheet.setPayableAccount(1500000 + addition);
        balanceSheet.setAdvanceReceived(1500000 + addition);
        balanceSheet.setOtherLiquidLiability(1500000 + addition);

        balanceSheet.setPrivateLoan(1500000 + addition);
        balanceSheet.setLongTermBorrowing(1500000 + addition);
        balanceSheet.setSeverancePay(1500000 + addition);
        balanceSheet.setOtherFixedLiability(1500000 + addition);

        balanceSheet.setCapital(1500000 + addition);
        balanceSheet.setSurplus(1500000 + addition);
        balanceSheet.setAdjustment(1500000 + addition);
        balanceSheet.setUnappropriated(1500000 + addition);
        balanceSheet.setComprehensive(1500000 + addition);

        return balanceSheetRepository.save(balanceSheet);
    }

    public Work deleteBalanceSheet(BalanceSheet balanceSheet, Work work){
        if(balanceSheet != null){
            balanceSheetRepository.delete(balanceSheet);
            work.setDraftState(WorkState.DELETE);
            return workRepository.save(work);
        }
        return null;
    }

    public void displayBalanceSheet(BalanceSheet balanceSheet){
        System.out.println("이름 : " + balanceSheet.getName());
        System.out.println("-----");
        System.out.println("유동자산 : " + balanceSheet.getGrossLiquidAsset());
        System.out.println("고정자산 : " + balanceSheet.getGrossFixedAsset());
        System.out.println("자산: " + balanceSheet.getGrossAsset());
        System.out.println("-----");
        System.out.println("유동부채 : " + balanceSheet.getGrossFixedLiability());
        System.out.println("고정부채 : " + balanceSheet.getGrossLiquidLiability());
        System.out.println("부채 : " + balanceSheet.getGrossLiability());
        System.out.println("-----");
        System.out.println("자본 : " + balanceSheet.getGrossCapital());
        System.out.println("-----");
        System.out.println("총재산 : " + balanceSheet.getGrossProperty());
        boolean isTrue = balanceSheet.getGrossProperty() == balanceSheet.getGrossAsset() &&
                balanceSheet.getGrossAsset() == (balanceSheet.getGrossCapital() + balanceSheet.getGrossLiability());
        System.out.println("총재산 = 자산 = 부채 + 자본 ? " + isTrue);
    }

    @DisplayName("재무재표 추가")
    @Test
    @Transactional
    public void createBalanceSheet(){
        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();
        Work work;
        List<BalanceSheet> balanceSheets = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            work = createWork(staff, workType);
            balanceSheets.add(createBalanceSheet(work, (i*1000000)));
        }
        List<Work> works = workRepository.findAll();
        balanceSheets = new ArrayList<>();
        for(Work w : works){
            balanceSheets.add(balanceSheetRepository.findByWork(w));
        }
        System.out.println("검색이 안되었는지 ? " + balanceSheets.isEmpty() + " 검색 건수 : " + balanceSheets.size());
        for(BalanceSheet balanceSheet : balanceSheets){
            displayBalanceSheet(balanceSheet);
            displayWork(balanceSheet.getWork());
        }
    }

    @DisplayName("재무재표 수정")
    @Test
    @Transactional
    public void updateBalanceSheet(){
        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        BalanceSheet created = createBalanceSheet(createWork(staff, workType), 1000000);

        BalanceSheet balanceSheet = balanceSheetRepository.findByWork(created.getWork());
        updateBalanceSheet(balanceSheet, "제무재표 수정", 50000000);

        balanceSheet = balanceSheetRepository.findByWork(created.getWork());
        displayBalanceSheet(balanceSheet);
    }

    @DisplayName("재무제표 승인&출력")
    @Test
    @Transactional
    public void allowBalanceSheet(){
        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        BalanceSheet created = createBalanceSheet(createWork(staff1, workType), 1000000);
        Work work = created.getWork();
        displayWork(work);

        Long workId = approveWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayBalanceSheet(balanceSheetRepository.findByWork(work));
    }

    @DisplayName("재무제표 보류&출력")
    @Test
    @Transactional
    public void delayBalanceSheet(){
        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        BalanceSheet created = createBalanceSheet(createWork(staff1, workType), 1000000);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = deferWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayBalanceSheet(balanceSheetRepository.findByWork(work));
    }

    @DisplayName("재무제표 거절&출력")
    @Test
    @Transactional
    public void rejectBalanceSheet(){
        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        BalanceSheet created = createBalanceSheet(createWork(staff1, workType), 1000000);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = rejectWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayBalanceSheet(balanceSheetRepository.findByWork(work));
    }

    @DisplayName("재무제표 삭제")
    @Test
    @Transactional
    public void deleteBalanceSheet(){
        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        BalanceSheet created = createBalanceSheet(createWork(staff, workType), 1000000);

        BalanceSheet balanceSheet = balanceSheetRepository.findByWork(created.getWork());
        Work work = deleteBalanceSheet(balanceSheet, balanceSheet.getWork());
        displayWork(work);
    }

    // 손익계산서 테스트
    public IncomeStatement createIncomeStatement(Work work, long s, long sc, long oe, long fer, long fex, long ct){
        LocalDate start = LocalDate.of(2024, 8, 1);
        LocalDate end = LocalDate.of(2024, 8, 31);

        IncomeStatement incomeStatement = new IncomeStatement();
        incomeStatement.setWork(work);
        incomeStatement.setStartDate(start);
        incomeStatement.setEndDate(end);
        incomeStatement.setSales(s);
        incomeStatement.setSalesCost(sc);
        incomeStatement.setOperatingExpense(oe);
        incomeStatement.setFinancialEarning(fer);
        incomeStatement.setFinancialExpense(fex);
        incomeStatement.setCorporateTax(ct);

        return incomeStatementRepository.save(incomeStatement);
    }

    public IncomeStatement updateIncomeStatement(IncomeStatement incomeStatement, long s, long sc, long oe, long fer, long fex, long ct){
        incomeStatement.setSales(s);
        incomeStatement.setSalesCost(sc);
        incomeStatement.setOperatingExpense(oe);
        incomeStatement.setFinancialEarning(fer);
        incomeStatement.setFinancialExpense(fex);
        incomeStatement.setCorporateTax(ct);

        return incomeStatementRepository.save(incomeStatement);
    }

    public Work deleteIncomeStatement(IncomeStatement incomeStatement, Work work){
        if(incomeStatement != null){
            incomeStatementRepository.delete(incomeStatement);
            work.setDraftState(WorkState.DELETE);
            return workRepository.save(work);
        }
        return null;
    }

    public void displayIncomeStatement(IncomeStatement incomeStatement){
        System.out.println("-----");
        System.out.println("결산 시작일 : " + incomeStatement.getStartDate());
        System.out.println("결산 종료일 : " + incomeStatement.getEndDate());
        System.out.println("-----");
        System.out.println("매출액 : " + incomeStatement.getSales());
        System.out.println("매출비용 : " + incomeStatement.getSalesCost());
        System.out.println("매출총이익 : " + incomeStatement.getGrossSales());
        System.out.println("-----");
        System.out.println("영업비용 : " + incomeStatement.getOperatingExpense());
        System.out.println("영업이익 : " + incomeStatement.getOperatingProfit());
        System.out.println("-----");
        System.out.println("금융수익 : " + incomeStatement.getFinancialEarning());
        System.out.println("금융비용 : " + incomeStatement.getFinancialExpense());
        System.out.println("법인세 차감전 순이익 : " + incomeStatement.getGrossProfit());
        System.out.println("-----");
        System.out.println("법인세 : " + incomeStatement.getCorporateTax());
        System.out.println("당기순이익 : " + incomeStatement.getNetIncome());
        System.out.println("-----");
    }

    @DisplayName("손익계산서 추가")
    @Test
    @Transactional
    public void createIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();
        Work work;
        List<IncomeStatement> incomeStatements = new ArrayList<>();
        for(int i = 1; i <= 20; i++){
            work = createWork(staff, workType);
            incomeStatements.add(createIncomeStatement(work, 10000 * i, 8000 * i, 1200 * i, 40 * i, 200 * i, 140 * i));
        }
        List<Work> works = workRepository.findAll();
        incomeStatements = new ArrayList<>();
        for(Work w : works){
            incomeStatements.add(incomeStatementRepository.findByWork(w));
        }
        System.out.println("검색이 안되었는지 ? " + incomeStatements.isEmpty() + " 검색 건수 : " + incomeStatements.size());
        for(IncomeStatement incomeStatement : incomeStatements){
            displayIncomeStatement(incomeStatement);
            displayWork(incomeStatement.getWork());
        }
    }

    @DisplayName("손익계산서 수정")
    @Test
    @Transactional
    public void updateIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        IncomeStatement created = createIncomeStatement(createWork(staff, workType), 10000, 8000, 1200, 40, 200, 140);

        IncomeStatement incomeStatement = incomeStatementRepository.findByWork(created.getWork());
        updateIncomeStatement(incomeStatement, 20000, 16000, 2000, 500, 250, 200);

        incomeStatement = incomeStatementRepository.findByWork(created.getWork());
        displayIncomeStatement(incomeStatement);
    }

    @DisplayName("손익계산서 승인&출력")
    @Test
    @Transactional
    public void allowIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        IncomeStatement created = createIncomeStatement(createWork(staff1, workType), 10000, 8000, 1200, 40, 200, 140);
        Work work = created.getWork();
        displayWork(work);

        Long workId = approveWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayIncomeStatement(incomeStatementRepository.findByWork(work));
    }

    @DisplayName("손익계산서 보류&출력")
    @Test
    @Transactional
    public void delayIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        IncomeStatement created = createIncomeStatement(createWork(staff1, workType), 10000, 8000, 1200, 40, 200, 140);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = deferWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayIncomeStatement(incomeStatementRepository.findByWork(work));
    }

    @DisplayName("손익계산서 거절&출력")
    @Test
    @Transactional
    public void rejectIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        IncomeStatement created = createIncomeStatement(createWork(staff1, workType), 10000, 8000, 1200, 40, 200, 140);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = rejectWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayIncomeStatement(incomeStatementRepository.findByWork(work));
    }

    @DisplayName("손익계산서 삭제")
    @Test
    @Transactional
    public void deleteIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        IncomeStatement created = createIncomeStatement(createWork(staff, workType), 10000, 8000, 1200, 40, 200, 140);

        IncomeStatement incomeStatement = incomeStatementRepository.findByWork(created.getWork());
        Work work = deleteIncomeStatement(incomeStatement, incomeStatement.getWork());
        displayWork(work);
    }

    // 포괄손익계산서 테스트
    public ComprehensiveIncomeStatement createComprehensiveIncomeStatement(Work work, long netIncome, long otherIncome){
        LocalDate start = LocalDate.of(2024, 8, 1);
        LocalDate end = LocalDate.of(2024, 8, 31);

        ComprehensiveIncomeStatement comprehensiveIncomeStatement = new ComprehensiveIncomeStatement();
        comprehensiveIncomeStatement.setWork(work);
        comprehensiveIncomeStatement.setStartDate(start);
        comprehensiveIncomeStatement.setEndDate(end);
        comprehensiveIncomeStatement.setNetIncome(netIncome);
        comprehensiveIncomeStatement.setOtherIncome(otherIncome);
        comprehensiveIncomeStatement.setTotalIncome(netIncome + otherIncome);

        return comprehensiveIncomeStatementRepository.save(comprehensiveIncomeStatement);
    }

    public ComprehensiveIncomeStatement updateComprehensiveIncomeStatement(ComprehensiveIncomeStatement comprehensiveIncomeStatement, long netIncome, long otherIncome){
        comprehensiveIncomeStatement.setNetIncome(netIncome);
        comprehensiveIncomeStatement.setOtherIncome(otherIncome);
        comprehensiveIncomeStatement.setTotalIncome(netIncome + otherIncome);

        return comprehensiveIncomeStatementRepository.save(comprehensiveIncomeStatement);
    }

    public Work deleteComprehensiveIncomeStatement(ComprehensiveIncomeStatement comprehensiveIncomeStatement, Work work){
        if(comprehensiveIncomeStatement != null){
            comprehensiveIncomeStatementRepository.delete(comprehensiveIncomeStatement);
            work.setDraftState(WorkState.DELETE);
            return workRepository.save(work);
        }
        return null;
    }

    public void displayComprehensiveIncomeStatement(ComprehensiveIncomeStatement comprehensiveIncomeStatement){
        System.out.println("-----");
        System.out.println("결산 시작일 : " + comprehensiveIncomeStatement.getStartDate());
        System.out.println("결산 종료일 : " + comprehensiveIncomeStatement.getEndDate());
        System.out.println("-----");
        System.out.println("당기순이익 : " + comprehensiveIncomeStatement.getNetIncome());
        System.out.println("기타 포괄손익 : " + comprehensiveIncomeStatement.getOtherIncome());
        System.out.println("총 포괄손익 : " + comprehensiveIncomeStatement.getTotalIncome());
        System.out.println("-----");
    }

    @DisplayName("포괄손익계산서 추가")
    @Test
    @Transactional
    public void createComprehensiveIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();
        Work work;
        List<ComprehensiveIncomeStatement> comprehensiveIncomeStatements = new ArrayList<>();
        for(int i = 1; i <= 20; i++){
            work = createWork(staff, workType);
            comprehensiveIncomeStatements.add(createComprehensiveIncomeStatement(work, 100000 * i, 10000 * i));
        }
        List<Work> works = workRepository.findAll();
        comprehensiveIncomeStatements = new ArrayList<>();
        for(Work w : works){
            comprehensiveIncomeStatements.add(comprehensiveIncomeStatementRepository.findByWork(w));
        }
        System.out.println("검색이 안되었는지 ? " + comprehensiveIncomeStatements.isEmpty() + " 검색 건수 : " + comprehensiveIncomeStatements.size());
        for(ComprehensiveIncomeStatement comprehensiveIncomeStatement : comprehensiveIncomeStatements){
            displayComprehensiveIncomeStatement(comprehensiveIncomeStatement);
            displayWork(comprehensiveIncomeStatement.getWork());
        }
    }

    @DisplayName("포괄손익계산서 수정")
    @Test
    @Transactional
    public void updateComprehensiveIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        ComprehensiveIncomeStatement created = createComprehensiveIncomeStatement(createWork(staff, workType), 100000, 10000);

        ComprehensiveIncomeStatement comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByWork(created.getWork());
        updateComprehensiveIncomeStatement(comprehensiveIncomeStatement, 150000, 50000);

        comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByWork(created.getWork());
        displayComprehensiveIncomeStatement(comprehensiveIncomeStatement);
    }

    @DisplayName("포괄손익계산서가 검색되지 않는 경우")
    @Test
    @Transactional
    public void failSearchComprehensiveIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        ComprehensiveIncomeStatement created = createComprehensiveIncomeStatement(createWork(staff, workType), 100000, 10000);

        LocalDate start = LocalDate.of(2024, 9, 1);
        LocalDate end = LocalDate.of(2024, 9, 30);

        ComprehensiveIncomeStatement comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByStartDateAndEndDate(start, end);
        if(comprehensiveIncomeStatement != null){
            displayComprehensiveIncomeStatement(comprehensiveIncomeStatement);
        }
        else{
            System.out.println("데이터가 없습니다.");
        }
    }

    @DisplayName("포괄손익계산서 승인&출력")
    @Test
    @Transactional
    public void allowComprehensiveIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        ComprehensiveIncomeStatement created = createComprehensiveIncomeStatement(createWork(staff1, workType), 100000, 10000);
        Work work = created.getWork();
        displayWork(work);

        Long workId = approveWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayComprehensiveIncomeStatement(comprehensiveIncomeStatementRepository.findByWork(work));
    }

    @DisplayName("포괄손익계산서 보류&출력")
    @Test
    @Transactional
    public void delayComprehensiveIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        ComprehensiveIncomeStatement created = createComprehensiveIncomeStatement(createWork(staff1, workType), 100000, 10000);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = deferWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayComprehensiveIncomeStatement(comprehensiveIncomeStatementRepository.findByWork(work));
    }

    @DisplayName("포괄손익계산서 거절&출력")
    @Test
    @Transactional
    public void rejectComprehensiveIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        ComprehensiveIncomeStatement created = createComprehensiveIncomeStatement(createWork(staff1, workType), 100000, 10000);
        Work work = workRepository.findByWorkId(created.getWork().getWorkId());
        displayWork(work);

        Long workId = rejectWork(staff2, work);
        work = workRepository.findByWorkId(workId);
        displayWork(work);
        displayComprehensiveIncomeStatement(comprehensiveIncomeStatementRepository.findByWork(work));
    }

    @DisplayName("포괄손익계산서 삭제")
    @Test
    @Transactional
    public void deleteComprehensiveIncomeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        LocalDate date = LocalDate.now();

        ComprehensiveIncomeStatement created = createComprehensiveIncomeStatement(createWork(staff, workType), 100000, 10000);

        ComprehensiveIncomeStatement comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByWork(created.getWork());
        Work work = deleteComprehensiveIncomeStatement(comprehensiveIncomeStatement, comprehensiveIncomeStatement.getWork());
        displayWork(work);
    }

    // 자본변동표 테스트
    public EquityChangeStatement createEquityChangeStatement(Work work){
        LocalDate start = LocalDate.of(2024, 8, 1);
        LocalDate end = LocalDate.of(2024, 8, 31);

        EquityChangeStatement equityChangeStatement = new EquityChangeStatement();
        equityChangeStatement.setWork(work);
        equityChangeStatement.setStartDate(start);
        equityChangeStatement.setEndDate(end);
        equityChangeStatement.setEquityChange(new ArrayList<>());

        return equityChangeStatementRepository.save(equityChangeStatement);
    }

    public EquityChange createEquityChange(EquityChangeStatement equityChangeStatement, String type, long capital, long surplus, long adjustment,
                                                    long unappropriated, long comprehensive){
        EquityChange equityChange = new EquityChange();
        equityChange.setType(type);

        equityChange.setCapital(capital);
        equityChange.setSurplus(surplus);
        equityChange.setAdjustment(adjustment);
        equityChange.setUnappropriated(unappropriated);
        equityChange.setComprehensive(comprehensive);

        long total = equityChange.getCapital() + equityChange.getSurplus() + equityChange.getAdjustment() + equityChange.getUnappropriated() + equityChange.getComprehensive();
        equityChange.setTotal(total);
        equityChange.setEquityChangeStatement(equityChangeStatement);
        return equityChangeRepository.save(equityChange);
    }

    public EquityChange updateEquityChange(EquityChange equityChange, String type, long capital, long surplus, long adjustment,
                                           long unappropriated, long comprehensive){
        equityChange.setType(type);
        equityChange.setCapital(capital);
        equityChange.setSurplus(surplus);
        equityChange.setAdjustment(adjustment);
        equityChange.setUnappropriated(unappropriated);
        equityChange.setComprehensive(comprehensive);

        long total = equityChange.getCapital() + equityChange.getSurplus() + equityChange.getAdjustment() + equityChange.getUnappropriated() + equityChange.getComprehensive();
        equityChange.setTotal(total);
        return equityChangeRepository.save(equityChange);
    }

    public void deleteEquityChange(Long equityChangeId){
        EquityChange equityChange = equityChangeRepository.findByEquityChangeId(equityChangeId);
        if(equityChange != null){
            EquityChangeStatement equityChangeStatement = equityChange.getEquityChangeStatement();
            equityChangeStatement.getEquityChange().remove(equityChange);
            equityChangeStatementRepository.save(equityChangeStatement);
            equityChangeRepository.delete(equityChange);
        }
        else {
            System.out.println("존재하지 않는 데이터입니다.");
        }
    }

    public Work deleteEquityChangeStatement(Work work, EquityChangeStatement equityChangeStatement){
        if(equityChangeStatement != null){
            equityChangeStatementRepository.delete(equityChangeStatement);
            work.setDraftState(WorkState.DELETE);
            return workRepository.save(work);
        }
        else {
            return null;
        }
    }

    public void displayEquityChangeStatement(EquityChangeStatement equityChangeStatement){
        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        System.out.println("-----");
        System.out.println("결산 시작일 : " + equityChangeStatement.getStartDate());
        System.out.println("결산 종료일 : " + equityChangeStatement.getEndDate());
        System.out.println("-----");
        for(EquityChange equityChange : equityChanges){
            System.out.println("- - -");
            System.out.println("유형 : " + equityChange.getType());
            System.out.println("자본금 : " + equityChange.getCapital());
            System.out.println("자본잉여금 : " + equityChange.getSurplus());
            System.out.println("자본조정 : " + equityChange.getAdjustment());
            System.out.println("미처분이익잉여금 : " + equityChange.getUnappropriated());
            System.out.println("기타포괄손익누계액 : " + equityChange.getComprehensive());
            System.out.println("소계 : " + equityChange.getTotal());
            System.out.println("- - -");
        }
        System.out.println("-----");
    }

    @DisplayName("자본변동표 추가")
    @Test
    @Transactional
    public void createEquityChangeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        EquityChangeStatement equityChangeStatement = createEquityChangeStatement(work);
        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        for(int i = 1; i <= 20; i++){
            EquityChange equityChange = createEquityChange(equityChangeStatement, "항목 " + i, 100 * i, 100 * i, 100 * i, 100 * i, 100 * i);
            equityChanges.add(equityChange);
        }
        equityChangeStatementRepository.save(equityChangeStatement);

        work = workRepository.findByWorkId(work.getWorkId());
        equityChangeStatement = equityChangeStatementRepository.findByWork(work);

        System.out.println("검색이 안되었는지 ? " + (equityChangeStatement == null));
        displayWork(work);
        displayEquityChangeStatement(equityChangeStatement);
    }

    @DisplayName("자본변동표 수정")
    @Test
    @Transactional
    public void updateEquityChangeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        EquityChangeStatement equityChangeStatement = createEquityChangeStatement(work);
        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        for(int i = 1; i <= 20; i++){
            EquityChange equityChange = createEquityChange(equityChangeStatement, "항목 " + i, 100 * i, 100 * i, 100 * i, 100 * i, 100 * i);
            equityChanges.add(equityChange);
        }
        equityChangeStatementRepository.save(equityChangeStatement);

        // equityChanges = new ArrayList<>();
        for(long i = 0; i < equityChangeStatement.getEquityChange().size(); i++){
            EquityChange equityChange = equityChangeStatement.getEquityChange().get((int) i);
            // equityChanges.add(updateEquityChange(equityChange, "항목 수정", 150 * (i+1), 150 * (i+1), 150 * (i+1), 150 * (i+1), 150 * (i+1)));
            updateEquityChange(equityChange, "항목 수정", 150 * (i+1), 150 * (i+1), 150 * (i+1), 150 * (i+1), 150 * (i+1));
        }
        //equityChangeStatementRepository.save(equityChangeStatement);

        work = workRepository.findByWorkId(work.getWorkId());
        equityChangeStatement = equityChangeStatementRepository.findByWork(work);

        displayWork(equityChangeStatement.getWork());
        displayEquityChangeStatement(equityChangeStatement);
    }

    @DisplayName("자본변동표 승인&출력")
    @Test
    @Transactional
    public void allowEquityChangeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work work =  createWork(staff1, workType);
        EquityChangeStatement equityChangeStatement = createEquityChangeStatement(work);
        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        for(int i = 1; i <= 5; i++){
            EquityChange equityChange = createEquityChange(equityChangeStatement, "항목 " + i, 100 * i, 100 * i, 100 * i, 100 * i, 100 * i);
            equityChanges.add(equityChange);
        }
        equityChangeStatementRepository.save(equityChangeStatement);

        Long workId = approveWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        equityChangeStatement = equityChangeStatementRepository.findByWork(work);
        displayWork(work);
        displayEquityChangeStatement(equityChangeStatement);
    }

    @DisplayName("자본변동표 보류&출력")
    @Test
    @Transactional
    public void delayEquityChangeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work work =  createWork(staff1, workType);
        EquityChangeStatement equityChangeStatement = createEquityChangeStatement(work);
        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        for(int i = 1; i <= 5; i++){
            EquityChange equityChange = createEquityChange(equityChangeStatement, "항목 " + i, 100 * i, 100 * i, 100 * i, 100 * i, 100 * i);
            equityChanges.add(equityChange);
        }
        equityChangeStatementRepository.save(equityChangeStatement);

        deferWork(staff2, work);

        work = workRepository.findByWorkId(work.getWorkId());
        equityChangeStatement = equityChangeStatementRepository.findByWork(work);
        displayWork(work);
        displayEquityChangeStatement(equityChangeStatement);
    }

    @DisplayName("자본변동표 거절&출력")
    @Test
    @Transactional
    public void rejectEquityChangeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work work =  createWork(staff1, workType);
        EquityChangeStatement equityChangeStatement = createEquityChangeStatement(work);
        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        for(int i = 1; i <= 5; i++){
            EquityChange equityChange = createEquityChange(equityChangeStatement, "항목 " + i, 100 * i, 100 * i, 100 * i, 100 * i, 100 * i);
            equityChanges.add(equityChange);
        }
        equityChangeStatementRepository.save(equityChangeStatement);

        rejectWork(staff2, work);

        work = workRepository.findByWorkId(work.getWorkId());
        equityChangeStatement = equityChangeStatementRepository.findByWork(work);
        displayWork(work);
        displayEquityChangeStatement(equityChangeStatement);
    }

    @DisplayName("자본변동표 삭제")
    @Test
    @Transactional
    public void deleteEquityChangeStatement(){
        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);

        Work work =  createWork(staff, workType);
        EquityChangeStatement equityChangeStatement = createEquityChangeStatement(work);
        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        for(int i = 1; i <= 5; i++){
            EquityChange equityChange = createEquityChange(equityChangeStatement, "항목 " + i, 100 * i, 100 * i, 100 * i, 100 * i, 100 * i);
            equityChanges.add(equityChange);
        }
        equityChangeStatementRepository.save(equityChangeStatement);


        EquityChangeStatement equityChangeStatement2 = equityChangeStatementRepository.findByWork(equityChangeStatement.getWork());
        work = deleteEquityChangeStatement(work, equityChangeStatement2);
        displayWork(work);
    }

    // 현금흐름표 테스트
    public CashFlowStatement createCashFlowStatement(Work work){
        LocalDate start = LocalDate.of(2024, 8, 1);
        LocalDate end = LocalDate.of(2024, 8, 31);

        CashFlowStatement cashFlowStatement = new CashFlowStatement();
        cashFlowStatement.setWork(work);
        cashFlowStatement.setStartDate(start);
        cashFlowStatement.setEndDate(end);
        cashFlowStatement.setCashFlow(new ArrayList<>());
        cashFlowStatement.setStatement("현금흐름표 작성");

        return cashFlowStatementRepository.save(cashFlowStatement);
    }

    public CashFlow createCashFlow(CashFlowStatement cashFlowStatement, String type, String detailedType, long amount){
        if(type.equals("영업활동") || type.equals("투자활동") || type.equals("재무활동")){
            CashFlow cashFlow = new CashFlow();
            cashFlow.setType(type);
            cashFlow.setDetailedType(detailedType);
            cashFlow.setAmount(amount);
            cashFlow.setCashFlowStatement(cashFlowStatement);
            return cashFlowRepository.save(cashFlow);
        }
        else{
            return null;
        }
    }

    public CashFlow updateCashFlow(CashFlow cashFlow, String type, String detailedType, long amount){
        cashFlow.setType(type);
        cashFlow.setDetailedType(detailedType);
        cashFlow.setAmount(amount);
        return cashFlowRepository.save(cashFlow);
    }

    public void deleteCashFlow(Long cashFlowId){
        CashFlow cashFlow = cashFlowRepository.findByCashFlowId(cashFlowId);
        if(cashFlow != null){
            CashFlowStatement cashFlowStatement = cashFlow.getCashFlowStatement();
            List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
            cashFlows.remove(cashFlow);
            cashFlowStatement.setCashFlow(cashFlows);
            cashFlowStatementRepository.save(cashFlowStatement);
            cashFlowRepository.delete(cashFlow);
        }
        else {
            System.out.println("존재하지 않는 데이터입니다.");
        }
    }

    public Work deleteCashFlowStatement(Work work, CashFlowStatement cashFlowStatement){
        if(cashFlowStatement != null){
            cashFlowStatementRepository.delete(cashFlowStatement);
            work.setDraftState(WorkState.DELETE);
            return workRepository.save(work);
        }
        else {
            return null;
        }
    }

    public void displayCashFlowStatement(CashFlowStatement cashFlowStatement){
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        System.out.println("-----");
        System.out.println("결산 시작일 : " + cashFlowStatement.getStartDate());
        System.out.println("결산 종료일 : " + cashFlowStatement.getEndDate());
        System.out.println("-----");
        for(CashFlow cashFlow : cashFlows){
            System.out.println("- - -");
            System.out.println("유형 : " + cashFlow.getType());
            System.out.println("세부 유형 : " + cashFlow.getDetailedType());
            System.out.println("금액 : " + cashFlow.getAmount());
            System.out.println("- - -");
        }
        System.out.println("-----");
    }

    @DisplayName("현금흐름표 추가")
    @Test
    @Transactional
    public void createCashFlowStatement(){
        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        CashFlowStatement cashFlowStatement = createCashFlowStatement(work);
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for(int i = 1; i <= 20; i++){
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동" + i), 10000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);

        work = workRepository.findByWorkId(work.getWorkId());
        cashFlowStatement = cashFlowStatementRepository.findByWork(work);

        System.out.println("검색이 안되었는지 ? " + (cashFlowStatement == null));
        displayWork(cashFlowStatement.getWork());
        displayCashFlowStatement(cashFlowStatement);
    }

    @DisplayName("현금흐름표 수정")
    @Test
    @Transactional
    public void updateCashFlowStatement(){
        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        CashFlowStatement cashFlowStatement = createCashFlowStatement(work);
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for(int i = 1; i <= 20; i++){
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동" + i), 10000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);

        cashFlows = new ArrayList<>();
        for(int i = 1; i <= 20; i++){
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동 수정" + i), 20000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);


        work = workRepository.findByWorkId(work.getWorkId());
        cashFlowStatement = cashFlowStatementRepository.findByWork(work);

        displayWork(cashFlowStatement.getWork());
        displayCashFlowStatement(cashFlowStatement);
    }

    @DisplayName("현금흐름표 승인&출력")
    @Test
    @Transactional
    public void allowCashFlowStatement(){
        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        CashFlowStatement cashFlowStatement = createCashFlowStatement(work);
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for(int i = 1; i <= 20; i++){
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동" + i), 10000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);

        Long workId = approveWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        cashFlowStatement = cashFlowStatementRepository.findByWork(work);

        displayWork(cashFlowStatement.getWork());
        displayCashFlowStatement(cashFlowStatement);
    }

    @DisplayName("현금흐름표 보류&출력")
    @Test
    @Transactional
    public void delayCashFlowStatement(){
        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        CashFlowStatement cashFlowStatement = createCashFlowStatement(work);
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for(int i = 1; i <= 20; i++){
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동" + i), 10000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);

        Long workId = deferWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        cashFlowStatement = cashFlowStatementRepository.findByWork(work);

        displayWork(cashFlowStatement.getWork());
        displayCashFlowStatement(cashFlowStatement);
    }

    @DisplayName("현금흐름표 거절&출력")
    @Test
    @Transactional
    public void rejectCashFlowStatement() {
        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work = createWork(staff1, workType);

        CashFlowStatement cashFlowStatement = createCashFlowStatement(work);
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for (int i = 1; i <= 20; i++) {
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동" + i), 10000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);

        Long workId = rejectWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        cashFlowStatement = cashFlowStatementRepository.findByWork(work);

        displayWork(cashFlowStatement.getWork());
        displayCashFlowStatement(cashFlowStatement);
    }
    @DisplayName("현금흐름표 삭제")
    @Test
    @Transactional
    public void deleteCashFlowStatement(){
        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work = createWork(staff, workType);

        CashFlowStatement cashFlowStatement = createCashFlowStatement(work);
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for (int i = 1; i <= 20; i++) {
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동" + i), 10000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);

        CashFlowStatement cashFlowStatement1 = cashFlowStatementRepository.findByWork(work);
        work = deleteCashFlowStatement(work, cashFlowStatement1);
        displayWork(work);
    }

    // 예산 테스트
    public Budget createBudget(Work work, String department, String section, long initial){
        LocalDate start = LocalDate.of(2024, 8, 1);
        LocalDate end = LocalDate.of(2024, 8, 31);

        Budget budget = new Budget();
        budget.setWork(work);
        budget.setStartDate(start);
        budget.setEndDate(end);
        budget.setDepartment(departmentRepository.findByDepartmentName(department));
        if(section != null){
            budget.setSection(sectionRepository.findBySectionName(section));
        }
        budget.setInitial(initial);
        budget.setAdditional(0);
        String statement = department + " " + (section == null ? "" : (section + " ")) + "예산";
        budget.setStatement(statement);
        budget.setBudgetPlan(new ArrayList<>());
        budget.setAdditionalBudget(new ArrayList<>());
        return budgetRepository.save(budget);
    }

    public BudgetPlan createBudgetPlan(Work work, Budget budget, Staff staff){
        LocalDate start = LocalDate.of(2024, 8, 1);
        LocalDate end = LocalDate.of(2024, 8, 31);

        BudgetPlan budgetPlan = new BudgetPlan();
        budgetPlan.setWork(work);
        budgetPlan.setBudget(budget);
        budgetPlan.setDepartment(budget.getDepartment());
        budgetPlan.setSection(budget.getSection());
        budgetPlan.setStaff(staff);
        budgetPlan.setStartDate(start);
        budgetPlan.setEndDate(end);
        budgetPlan.setBudgetItem(new ArrayList<>());
        budgetPlan.setTotal(0);
        return budgetPlanRepository.save(budgetPlan);
    }

    public BudgetItem createBudgetItem(BudgetPlan budgetPlan, String type, String item, long required, String statement){
        BudgetItem budgetItem = new BudgetItem();
        budgetItem.setBudgetPlan(budgetPlan);
        budgetItem.setType(type);
        budgetItem.setItem(item);
        budgetItem.setRequired(required);
        budgetItem.setStatement(statement);
        return budgetItemRepository.save(budgetItem);
    }

    public boolean checkBudgetPlanDepartment(BudgetPlan budgetPlan, Staff staff){
        if(budgetPlan.getDepartment() == null){
            return staff.getDepartment().getDepartmentName().equals("미정");
        }
        return budgetPlan.getDepartment().getDepartmentName().equals(staff.getDepartment().getDepartmentName());
    }

    public boolean checkBudgetPlanSection(BudgetPlan budgetPlan, Staff staff){
        if(budgetPlan.getSection() == null){
            return staff.getSection().getSectionName().equals("미정");
        }
        return budgetPlan.getSection().getSectionName().equals(staff.getSection().getSectionName());
    }


    public AdditionalBudget createAdditionalBudget(Work work, Budget budget, Staff staff){
        AdditionalBudget additionalBudget = new AdditionalBudget();
        additionalBudget.setWork(work);
        additionalBudget.setBudget(budget);
        additionalBudget.setDepartment(budget.getDepartment());
        additionalBudget.setSection(budget.getSection());
        additionalBudget.setStaff(staff);
        additionalBudget.setAdditionalBudgetItem(new ArrayList<>());
        return additionalBudgetRepository.save(additionalBudget);
    }

    public boolean checkAdditionalBudgetDepartment(AdditionalBudget additionalBudget, Staff staff){
        if(additionalBudget.getDepartment() == null){
            return staff.getDepartment().getDepartmentName().equals("미정");
        }
        return additionalBudget.getDepartment().getDepartmentName().equals(staff.getDepartment().getDepartmentName());
    }
    public boolean checkAdditionalBudgetSection(AdditionalBudget additionalBudget, Staff staff){
        if(additionalBudget.getSection() == null){
            return staff.getSection().getSectionName().equals("미정");
        }
        return additionalBudget.getSection().getSectionName().equals(staff.getSection().getSectionName());
    }

    public AdditionalBudgetItem createAdditionalBudgetItem(AdditionalBudget additionalBudget, String type, String item, long addition, String reason){
        AdditionalBudgetItem additionalBudgetItem = new AdditionalBudgetItem();
        additionalBudgetItem.setAdditionalBudget(additionalBudget);
        additionalBudgetItem.setItem(item);
        additionalBudgetItem.setType(type);
        additionalBudgetItem.setAddition(addition);
        additionalBudgetItem.setReason(reason);
        return additionalBudgetItemRepository.save(additionalBudgetItem);
    }

    public Budget changeBudgetAdditionalTotal(Budget budget, AdditionalBudget additionalBudget){
        if(!additionalBudget.getAdditionalBudgetItem().isEmpty()){
            long total = 0;
            for(AdditionalBudgetItem additionalBudgetItem : additionalBudget.getAdditionalBudgetItem()){
                total += additionalBudgetItem.getAddition();
            }
            budget.setAdditional(budget.getAdditional() + total);
        }
        return budgetRepository.save(budget);
    }

    public BudgetPlan changeBudgetPlanTotal(BudgetPlan budgetPlan){
        if(!budgetPlan.getBudgetItem().isEmpty()){
            long total = 0;
            for(BudgetItem budgetItem : budgetPlan.getBudgetItem()){
                total += budgetItem.getRequired();
            }
            budgetPlan.setTotal(total);
        }
        return budgetPlanRepository.save(budgetPlan);
    }

    public boolean checkOverBudget(Budget budget, BudgetPlan budgetPlan){
        long total = budget.getInitial();
        long used = 0;
        for(BudgetPlan b : budget.getBudgetPlan()){
            used += b.getTotal();
        }
        return (total < (used + budgetPlan.getTotal()));
    }

    public Budget updateBudget(Budget budget, String department, String section, long initial){
        budget.setDepartment(departmentRepository.findByDepartmentName(department));
        if(section != null){
            budget.setSection(sectionRepository.findBySectionName(section));
        }
        budget.setInitial(initial);
        return budgetRepository.save(budget);
    }

    public AdditionalBudget updateAdditionalBudget(AdditionalBudget additionalBudget, Budget budget, Staff staff){
        if(budget != null){
            additionalBudget.setDepartment(budget.getDepartment());
            additionalBudget.setSection(budget.getSection());
        }
        if(staff != null){
            additionalBudget.setStaff(staff);
        }
        return additionalBudgetRepository.save(additionalBudget);
    }

    public BudgetPlan updateBudgetPlan(BudgetPlan budgetPlan, String [] type, String [] item, long [] required, String [] statement){
        List<BudgetItem> budgetItems = budgetPlan.getBudgetItem();
        List<BudgetItem> updated = new ArrayList<>();
        for(int i = 0; i < budgetItems.size(); i++){
            updated.add(updateBudgetItem(budgetItems.get(i), type[i], item[i], required[i], statement[i]));
        }
        if(budgetItems.size() < type.length){
            for(int i = budgetItems.size(); i < type.length; i++){
                updated.add(createBudgetItem(budgetPlan, type[i], item[i], required[i], statement[i]));
            }
        }
        budgetPlan.setBudgetItem(updated);
        return budgetPlanRepository.save(budgetPlan);
    }

    public BudgetItem updateBudgetItem(BudgetItem budgetItem, String type, String item, long required, String statement){
        budgetItem.setType(type);
        budgetItem.setItem(item);
        budgetItem.setRequired(required);
        budgetItem.setStatement(statement);
        return budgetItemRepository.save(budgetItem);
    }

    public AdditionalBudget updateAdditionalBudget(AdditionalBudget additionalBudget, String [] type, String [] item, long [] addition, String [] reason){
        List<AdditionalBudgetItem> additionalBudgetItems = additionalBudget.getAdditionalBudgetItem();
        List<AdditionalBudgetItem> updated = new ArrayList<>();
        for(int i = 0; i < additionalBudgetItems.size(); i++){
            updated.add(updateAdditionalBudgetItem(additionalBudgetItems.get(i), type[i], item[i], addition[i], reason[i]));
        }
        if(additionalBudgetItems.size() < type.length){
            for(int i = additionalBudgetItems.size(); i < type.length; i++){
                updated.add(createAdditionalBudgetItem(additionalBudget, type[i], item[i], addition[i], reason[i]));
            }
        }
        additionalBudget.setAdditionalBudgetItem(updated);
        return additionalBudgetRepository.save(additionalBudget);
    }

    public AdditionalBudgetItem updateAdditionalBudgetItem(AdditionalBudgetItem additionalBudgetItem, String type, String item, long addition, String reason){
        additionalBudgetItem.setType(type);
        additionalBudgetItem.setItem(item);
        additionalBudgetItem.setReason(reason);
        additionalBudgetItem.setAddition(addition);
        return additionalBudgetItemRepository.save(additionalBudgetItem);
    }

    /*
    예산 삭제에 대해서 추후 테스트 예정

    public Work deleteBudget(Work work, Budget budget){
        if(budget != null){
            work.setState(State.DELETED);
            work.setStatement("데이터가 삭제되었습니다.");
            budgetRepository.delete(budget);
            return workRepository.save(work);
        }
        else {
            System.out.println("존재하지 않는 데이터입니다.");
            return null;
        }
    }

    public Work deleteBudgetPlan(Work work, BudgetPlan budgetPlan){
        if(budgetPlan != null){
            work.setState(State.DELETED);
            work.setStatement("데이터가 삭제되었습니다.");
            budgetPlanRepository.delete(budgetPlan);
            return workRepository.save(work);
        }
        else {
            System.out.println("존재하지 않는 데이터입니다.");
            return null;
        }
    }

    public Work deleteAdditionalBudget(Work work, AdditionalBudget additionalBudget){
        if(additionalBudget != null){
            work.setState(State.DELETED);
            work.setStatement("데이터가 삭제되었습니다.");
            additionalBudgetRepository.delete(additionalBudget);
            return workRepository.save(work);
        }
        else {
            System.out.println("존재하지 않는 데이터입니다.");
            return null;
        }
    }
     */

    public BudgetPlan deleteBudgetItem(BudgetPlan budgetPlan, BudgetItem budgetItem){
        if(budgetItem != null){
            List<BudgetItem> budgetItems = budgetPlan.getBudgetItem();
            budgetItems.remove(budgetItem);
            budgetPlan.setBudgetItem(budgetItems);
            budgetItemRepository.delete(budgetItem);
            return budgetPlanRepository.save(budgetPlan);
        }
        else {
            System.out.println("존재하지 않는 데이터입니다.");
            return null;
        }
    }

    public AdditionalBudget deleteAdditionalBudgetItem(AdditionalBudget additionalBudget, AdditionalBudgetItem additionalBudgetItem){
        if(additionalBudgetItem != null){
            List<AdditionalBudgetItem> additionalBudgetItems = additionalBudget.getAdditionalBudgetItem();
            additionalBudgetItems.remove(additionalBudgetItem);
            additionalBudget.setAdditionalBudgetItem(additionalBudgetItems);
            additionalBudgetItemRepository.delete(additionalBudgetItem);
            return additionalBudgetRepository.save(additionalBudget);
        }
        else {
            System.out.println("존재하지 않는 데이터입니다.");
            return null;
        }
    }

    public void displayBudget(Budget budget){
        System.out.println("-----");
        System.out.println("부서 : " + budget.getDepartment().getDepartmentName());
        System.out.println("과 : " + (budget.getSection() == null ? "" : budget.getSection().getSectionName()));
        System.out.println("-----");
        System.out.println("할당된 예산 : " + budget.getInitial());
        System.out.println("추가 예산 : " + budget.getAdditional());
        System.out.println("-----");
        System.out.println("예산 계획 개수 : " + budget.getBudgetPlan().size());
        System.out.println("추가 예산 요청 횟수 : " + budget.getAdditionalBudget().size());
        System.out.println("-----");
    }

    public void displayBudgetPlan(BudgetPlan budgetPlan){
        System.out.println("-----");
        System.out.println("예산 게획 신청자 : " + budgetPlan.getStaff().getStaffName());
        System.out.println("예산 사용 시작일 : " + budgetPlan.getStartDate());
        System.out.println("에산 사용 종료일 : " + budgetPlan.getEndDate());
        System.out.println("예산 사용 부서 : " + budgetPlan.getDepartment().getDepartmentName());
        System.out.println("예산 사용 과 : " + (budgetPlan.getSection() == null ? "" : budgetPlan.getSection().getSectionName()));
        System.out.println("-----");
        System.out.println("예산 초과 여부 : " + checkOverBudget(budgetPlan.getBudget(), budgetPlan));
        System.out.println("신청자의 부서 == 에산 사용 부서 : " + checkBudgetPlanDepartment(budgetPlan, budgetPlan.getStaff()));
        System.out.println("신청자의 과 == 에산 사용 과 : " + checkBudgetPlanSection(budgetPlan, budgetPlan.getStaff()));
        System.out.println("-----");
        if(budgetPlan.getBudgetItem().isEmpty()){
            System.out.println("데이터가 없습니다.");
        }
        else {
            for(BudgetItem item : budgetPlan.getBudgetItem()){
                displayBudgetItem(item);
            }
        }
        System.out.println("-----");
    }
    public void displayBudgetItem(BudgetItem budgetItem){
        System.out.println("- - -");
        System.out.println("유형 : " + budgetItem.getType());
        System.out.println("항목 : " + budgetItem.getItem());
        System.out.println("필요 예산 : " + budgetItem.getRequired());
        System.out.println("비고 : " + budgetItem.getStatement());
        System.out.println("- - -");
    }

    public void displayAdditionalBudget(AdditionalBudget additionalBudget){
        System.out.println("-----");
        System.out.println("추가 예산 게획 신청자 : " + additionalBudget.getStaff().getStaffName());
        System.out.println("추가 예산 사용 부서 : " + additionalBudget.getDepartment().getDepartmentName());
        System.out.println("추가 예산 사용 과 : " + (additionalBudget.getSection() == null ? "" : additionalBudget.getSection().getSectionName()));
        System.out.println("-----");
        System.out.println("신청자의 부서 == 추가 예산 사용 부서 : " + checkAdditionalBudgetDepartment(additionalBudget, additionalBudget.getStaff()));
        System.out.println("신청자의 과 == 추가 예산 사용 과 : " + checkAdditionalBudgetSection(additionalBudget, additionalBudget.getStaff()));
        System.out.println("-----");
        if(additionalBudget.getAdditionalBudgetItem().isEmpty()){
            System.out.println("데이터가 없습니다.");
        }
        else {
            for(AdditionalBudgetItem item : additionalBudget.getAdditionalBudgetItem()){
                displayAdditionalBudgetItem(item);
            }
        }
        System.out.println("-----");
    }
    public void displayAdditionalBudgetItem(AdditionalBudgetItem additionalBudgetItem){
        System.out.println("- - -");
        System.out.println("유형 : " + additionalBudgetItem.getType());
        System.out.println("항목 : " + additionalBudgetItem.getItem());
        System.out.println("필요 추가 예산 : " + additionalBudgetItem.getAddition());
        System.out.println("이유 : " + additionalBudgetItem.getReason());
        System.out.println("- - -");
    }

    @DisplayName("예산 추가")
    @Test
    @Transactional
    public void createBudget(){
        WorkType workType = workTypeRepository.findByWorkName("예산");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Budget created = createBudget(work, "인사부", "인사 1과", 10000000);

        work = workRepository.findByWorkId(work.getWorkId());
        Budget budget = budgetRepository.findByWork(work);

        displayWork(budget.getWork());
        displayBudget(budget);
    }

    @DisplayName("예산 수정")
    @Test
    @Transactional
    public void updateBudget(){
        WorkType workType = workTypeRepository.findByWorkName("예산");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Budget created = createBudget(work, "인사부", "인사 1과", 10000000);

        work = workRepository.findByWorkId(work.getWorkId());
        Budget budget = updateBudget(created, "인사부", "인사 2과", 20000000);

        displayWork(work);
        displayBudget(budget);
    }

    @DisplayName("예산 승인&출력")
    @Test
    @Transactional
    public void allowBudget(){
        WorkType workType = workTypeRepository.findByWorkName("예산");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Budget created = createBudget(work, "인사부", "인사 1과", 10000000);

        Long workId = approveWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Budget budget = budgetRepository.findByWork(work);

        displayWork(budget.getWork());
        displayBudget(budget);
    }

    @DisplayName("예산 보류&출력")
    @Test
    @Transactional
    public void delayBudget(){
        WorkType workType = workTypeRepository.findByWorkName("예산");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Budget created = createBudget(work, "인사부", "인사 1과", 10000000);

        Long workId = deferWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Budget budget = budgetRepository.findByWork(work);

        displayWork(budget.getWork());
        displayBudget(budget);
    }

    @DisplayName("예산 거절&출력")
    @Test
    @Transactional
    public void rejectBudget() {
        WorkType workType = workTypeRepository.findByWorkName("예산");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work = createWork(staff1, workType);

        Budget created = createBudget(work, "인사부", "인사 1과", 10000000);

        Long workId = rejectWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Budget budget = budgetRepository.findByWork(work);

        displayWork(budget.getWork());
        displayBudget(budget);
    }

    /*
    예산 테스트 예정 항목 1
    @DisplayName("예산 삭제")
    @Test
    @Transactional
    public void deleteBudget(){
        WorkType workType = workTypeRepository.findByWorkName("예산");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work = createWork(staff, workType);

        Budget created = createBudget(work, "인사부", "인사 1과", "인사 1팀", 10000000);

        work = deleteBudget(work, created);

        Budget budget = budgetRepository.findByWork(work);
        System.out.println("예산이 삭제되었는지? " + (budget == null));
        displayWork(work);
    }
     */

    @DisplayName("예산 계획 생성")
    @Test
    @Transactional
    public void createBudgetPlan(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Staff staff2 = makeStaff("ascia44", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff2);
        List<BudgetPlan> budgetPlans = new ArrayList<>();
        budgetPlans.add(createdBudgetPlan);

        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        displayBudget(budget);
        budgetPlans = budget.getBudgetPlan();
        for(BudgetPlan plan : budgetPlans){
            displayBudgetPlan(plan);
        }
    }

    @DisplayName("예산 계획 항목 생성 1")
    @Test
    @Transactional
    public void createBudgetPlanItem1(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Staff staff2 = makeStaff("ascia44", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff2);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 100000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        displayBudget(budget);
        budgetPlans = budget.getBudgetPlan();
        for(BudgetPlan plan : budgetPlans){
            displayBudgetPlan(plan);
        }
    }

    @DisplayName("예산 계획 항목 생성 2")
    @Test
    @Transactional
    public void createBudgetPlanItem2(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Staff staff2 = makeStaff("ascia44", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff2);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 1000000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        displayBudget(budget);
        budgetPlans = budget.getBudgetPlan();
        for(BudgetPlan plan : budgetPlans){
            displayBudgetPlan(plan);
        }
    }

    @DisplayName("예산 계획 항목 생성 3")
    @Test
    @Transactional
    public void createBudgetPlanItem3(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Staff staff2 = makeStaff("ascia44", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff2);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), (1000000 + i), ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        displayBudget(budget);
        budgetPlans = budget.getBudgetPlan();
        for(BudgetPlan plan : budgetPlans){
            displayBudgetPlan(plan);
        }
    }

    @DisplayName("예산 계획 수정")
    @Test
    @Transactional
    public void updateBudgetPlan(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff2);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 100000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        String [] type = new String[] { "유형 수정 1", "유형 수정 2", "유형 수정 3", "유형 수정 4", "유형 수정 5", "유형 수정 6", "유형 수정 7", "유형 수정 8" };
        String [] item = new String[] { "항목 수정 1", "항목 수정 2", "항목 수정 3", "항목 수정 4", "항목 수정 5", "항목 수정 6", "항목 수정 7", "항목 수정 8" };
        long [] required = new long[] { 200000L, 200000L, 200000L, 200000L, 200000L, 200000L, 200000L, 200000L };
        String [] statement = new String[] { "비고 수정 1", "비고 수정 2", "비고 수정 3", "비고 수정 4", "비고 수정 5", "비고 수정 6", "비고 수정 7", "비고 수정 8" };

        BudgetPlan budgetPlan = budgetPlanRepository.findByWork(planWork);
        System.out.println("수정 전");
        displayBudgetPlan(budgetPlan);

        updateBudgetPlan(budgetPlan, type, item, required, statement);

        BudgetPlan updated = budgetPlanRepository.findByWork(planWork);
        System.out.println("\n수정 후");
        displayBudgetPlan(updated);

    }
    @DisplayName("예산 계획 승인&출력")
    @Test
    @Transactional
    public void allowBudgetPlan(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Staff staff3 = makeStaff("ascia55", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff3);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 100000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        Long budgetWorkId = approveWork(staff2, budgetWork);
        Long planWorkId = approveWork(staff2, planWork);

        budgetWork = workRepository.findByWorkId(budgetWorkId);
        planWork = workRepository.findByWorkId(planWorkId);

        displayWork(budgetWork);
        displayWork(planWork);
    }

    @DisplayName("예산 계획 보류&출력")
    @Test
    @Transactional
    public void delayBudgetPlan(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Staff staff3 = makeStaff("ascia55", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff3);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 100000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        Long budgetWorkId = approveWork(staff2, budgetWork);
        Long planWorkId = deferWork(staff2, planWork);

        budgetWork = workRepository.findByWorkId(budgetWorkId);
        planWork = workRepository.findByWorkId(planWorkId);

        displayWork(budgetWork);
        displayWork(planWork);
    }

    @DisplayName("예산 계획 거절&출력")
    @Test
    @Transactional
    public void rejectBudgetPlan() {
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Staff staff3 = makeStaff("ascia55", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff3);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 100000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        Long budgetWorkId = approveWork(staff2, budgetWork);
        Long planWorkId = rejectWork(staff2, planWork);

        budgetWork = workRepository.findByWorkId(budgetWorkId);
        planWork = workRepository.findByWorkId(planWorkId);

        displayWork(budgetWork);
        displayWork(planWork);
    }
    @DisplayName("예산 계획의 항목 삭제")
    @Test
    @Transactional
    public void deleteBudgetItem(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Staff staff2 = makeStaff("ascia44", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과",10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff2);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 100000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        BudgetPlan budgetPlan = budget.getBudgetPlan().get(0);
        deleteBudgetItem(budgetPlan, budgetPlan.getBudgetItem().get(0));

        budget = budgetRepository.findByWork(budgetWork);

        displayBudget(budget);
        budgetPlans = budget.getBudgetPlan();
        for(BudgetPlan plan : budgetPlans){
            displayBudgetPlan(plan);
        }
    }

    /*
    예산 테스트 에정 항목 2
    @DisplayName("예산 계획 삭제")
    @Test
    @Transactional
    public void deleteBudgetPlan(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType planType = workTypeRepository.findByWorkName("예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Staff staff2 = makeStaff("ascia44", "홍길강", Gender.MALE, human_resource[2]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work planWork =  createWork(staff1, planType);

        Budget createdBudget = createBudget(budgetWork, "인사부", "인사 1과", null, 10000000);
        BudgetPlan createdBudgetPlan = createBudgetPlan(planWork, createdBudget, staff2);

        List<BudgetPlan> budgetPlans = new ArrayList<>();
        List<BudgetItem> budgetItems = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            budgetItems.add(createBudgetItem(createdBudgetPlan, ("유형 " + i), ("항목 " + i), 100000, ("비고" + i)));
        }
        createdBudgetPlan.setBudgetItem(budgetItems);
        budgetPlans.add(budgetPlanRepository.save(changeBudgetPlanTotal(createdBudgetPlan)));
        createdBudget.setBudgetPlan(budgetPlans);
        Budget budget = budgetRepository.save(createdBudget);

        deleteBudgetPlan(planWork, createdBudgetPlan);

        Budget budget1 = budgetRepository.findByWork(budgetWork);

        System.out.println("예산 계획이 삭제되었는지? " + (budget1.getBudgetPlan().isEmpty()));
        displayBudget(budget1);
        displayWork(planWork);
    }
     */

    @DisplayName("추가 예산 계획 추가")
    @Test
    @Transactional
    public void createAdditionalBudget(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType additionalType = workTypeRepository.findByWorkName("추가 예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, human_resource[0]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work additionalWork =  createWork(staff1, additionalType);

        Budget createdBudget = createBudget(budgetWork, "인사부", null, 10000000);
        AdditionalBudget createdAdditionalBudget = createAdditionalBudget(additionalWork, createdBudget, staff2);
        List<AdditionalBudget> additionalBudgets = createdBudget.getAdditionalBudget();

        additionalBudgets.add(createdAdditionalBudget);
        createdBudget.setAdditionalBudget(additionalBudgets);

        Budget budget = budgetRepository.save(createdBudget);

        displayBudget(budget);
        displayAdditionalBudget(budget.getAdditionalBudget().get(0));
    }

    @DisplayName("추가 예산 계획 항목 추가")
    @Test
    @Transactional
    public void createAdditionalBudgetItem(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType additionalType = workTypeRepository.findByWorkName("추가 예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, human_resource[0]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work additionalWork = createWork(staff1, additionalType);

        Budget budget = createBudget(budgetWork, "인사부", null, 1000000);
        AdditionalBudget createdAdditionalBudget = createAdditionalBudget(additionalWork, budget, staff2);

        List<AdditionalBudgetItem> additionalBudgetItems = new ArrayList<>();
        List<AdditionalBudget> additionalBudgets = new ArrayList<>();

        for(int i = 1; i <= 5; i++){
            additionalBudgetItems.add(createAdditionalBudgetItem(createdAdditionalBudget, ("유형 " + i), ("항목 " + i), 10000, ("사유 " + i)));
        }
        createdAdditionalBudget.setAdditionalBudgetItem(additionalBudgetItems);

        AdditionalBudget additionalBudget = additionalBudgetRepository.save(createdAdditionalBudget);
        additionalBudgets.add(additionalBudget);
        budget.setAdditionalBudget(additionalBudgets);

        budget = changeBudgetAdditionalTotal(budget, additionalBudget);

        displayBudget(budget);
        displayAdditionalBudget(budget.getAdditionalBudget().get(0));
    }

    @DisplayName("추가 예산 계획 승인&출력")
    @Test
    @Transactional
    public void allowAdditionalBudget(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType additionalType = workTypeRepository.findByWorkName("추가 예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[1]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, human_resource[0]);
        Staff staff3 = makeStaff("ascia55", "홍길강", Gender.MALE, staffInfo[0]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work additionalWork =  createWork(staff1, additionalType);

        Budget createdBudget = createBudget(budgetWork, "인사부", null, 10000000);
        AdditionalBudget createdAdditionalBudget = createAdditionalBudget(additionalWork, createdBudget, staff2);
        List<AdditionalBudget> additionalBudgets = createdBudget.getAdditionalBudget();

        additionalBudgets.add(createdAdditionalBudget);
        createdBudget.setAdditionalBudget(additionalBudgets);

        Budget budget = budgetRepository.save(createdBudget);

        Long budgetWorkId = approveWork(staff3, budgetWork);
        Long additionalWorkId = approveWork(staff3, additionalWork);

        budgetWork = workRepository.findByWorkId(budgetWorkId);
        additionalWork = workRepository.findByWorkId(additionalWorkId);

        displayWork(budgetWork);
        displayWork(additionalWork);
    }

    @DisplayName("추가 예산 계획 보류&출력")
    @Test
    @Transactional
    public void delayAdditionalBudget(){
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType additionalType = workTypeRepository.findByWorkName("추가 예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[1]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, human_resource[0]);
        Staff staff3 = makeStaff("ascia55", "홍길강", Gender.MALE, staffInfo[0]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work additionalWork =  createWork(staff1, additionalType);

        Budget createdBudget = createBudget(budgetWork, "인사부", null, 10000000);
        AdditionalBudget createdAdditionalBudget = createAdditionalBudget(additionalWork, createdBudget, staff2);
        List<AdditionalBudget> additionalBudgets = createdBudget.getAdditionalBudget();

        additionalBudgets.add(createdAdditionalBudget);
        createdBudget.setAdditionalBudget(additionalBudgets);

        Budget budget = budgetRepository.save(createdBudget);

        Long budgetWorkId = deferWork(staff3, budgetWork);
        Long additionalWorkId = deferWork(staff3, additionalWork);

        budgetWork = workRepository.findByWorkId(budgetWorkId);
        additionalWork = workRepository.findByWorkId(additionalWorkId);

        displayWork(budgetWork);
        displayWork(additionalWork);
    }

    @DisplayName("추가 예산 계획 거절&출력")
    @Test
    @Transactional
    public void rejectAdditionalBudget() {
        WorkType budgetType = workTypeRepository.findByWorkName("예산");
        WorkType additionalType = workTypeRepository.findByWorkName("추가 예산 계획");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[1]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, human_resource[0]);
        Staff staff3 = makeStaff("ascia55", "홍길강", Gender.MALE, staffInfo[0]);

        Work budgetWork =  createWork(staff1, budgetType);
        Work additionalWork =  createWork(staff1, additionalType);

        Budget createdBudget = createBudget(budgetWork, "인사부", null,  10000000);
        AdditionalBudget createdAdditionalBudget = createAdditionalBudget(additionalWork, createdBudget, staff2);
        List<AdditionalBudget> additionalBudgets = createdBudget.getAdditionalBudget();

        additionalBudgets.add(createdAdditionalBudget);
        createdBudget.setAdditionalBudget(additionalBudgets);

        Budget budget = budgetRepository.save(createdBudget);

        Long budgetWorkId = rejectWork(staff3, budgetWork);
        Long additionalWorkId = rejectWork(staff3, additionalWork);

        budgetWork = workRepository.findByWorkId(budgetWorkId);
        additionalWork = workRepository.findByWorkId(additionalWorkId);

        displayWork(budgetWork);
        displayWork(additionalWork);
    }

    /*
    예산 테스트 예정 항목 3
    @DisplayName("추가 예산 계획 삭제")
    @Test
    @Transactional
    public void deleteAdditionalBudget(){
        WorkType workType = workTypeRepository.findByWorkName("추가 예산 계획");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work = createWork(staff, workType);

        CashFlowStatement cashFlowStatement = createCashFlowStatement(work);
        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for (int i = 1; i <= 20; i++) {
            CashFlow cashFlow = createCashFlow(cashFlowStatement, "영업활동", ("세부 활동" + i), 10000);
            cashFlows.add(cashFlow);
        }
        cashFlowStatement.setCashFlow(cashFlows);
        cashFlowStatementRepository.save(cashFlowStatement);

        CashFlowStatement cashFlowStatement1 = cashFlowStatementRepository.findByWork(work);
        work = deleteCashFlowStatement(work, cashFlowStatement1);
        displayWork(work);
    }
     */


    // 채권 & 채무 테스트, patron과 credit은 차후에 통합테스트 예정
    public Debt createDebt(Work work, String debtType, String item, String type, long total){
        LocalDate start = LocalDate.now();
        LocalDate expire = start.plusDays(100);

        Debt debt = new Debt();
        debt.setWork(work);
        debt.setDebtType(debtType);
        debt.setItem(item);
        debt.setType(type);
        debt.setTotal(total);
        debt.setStart(start);
        debt.setExpire(expire);
        debt.setStatement("비고");
        debt.setPatron(null);
        debt.setCredit(null);
        debt.setDebtHistory(new ArrayList<>());
        return debtRepository.save(debt);
    }

    public Debt createReceivable(Work work, String item, String type, long total){
        return createDebt(work, "채권", item, type, total);
    }

    public Debt createPayable(Work work, String item, String type, long total){
        return createDebt(work, "채무", item, type, total);
    }

    public Debt updateDebt(Debt debt, String debtType, String item, String type, long total){
        debt.setItem(item);
        debt.setType(type);
        debt.setTotal(total);
        debt.setStatement("비고 수정");
        debt.setPatron(null);
        debt.setCredit(null);
        return debtRepository.save(debt);
    }

    public Debt updateReceivable(Debt debt, String item, String type, long total){
        return updateDebt(debt, "채권", item, type, total);
    }

    public Debt updatePayable(Debt debt, String item, String type, long total){
        return updateDebt(debt, "채무", item, type, total);
    }

    public Long deleteDebt(Work work, Debt debt){
        if(debt != null){
            work.setDraftState(WorkState.DELETE);
            workRepository.save(work);
            debtRepository.delete(debt);
            return work.getWorkId();
        }
        else {
            throw new NullPointerException();
        }
    }

    public DebtHistory createDebtHistory(Work work, Debt debt, LocalDate date, long amount, String status){
        DebtHistory debtHistory = new DebtHistory();
        debtHistory.setWork(work);
        debtHistory.setDebt(debt);
        debtHistory.setDate(date);
        debtHistory.setAmount(amount);
        debtHistory.setStatus(status);
        debtHistory.setStatement("비고");
        return debtHistoryRepository.save(debtHistory);
    }

    public List<DebtHistory> createDebtHistories(Staff staff, Debt debt, LocalDate [] date, long [] amount, String [] status){
        List<DebtHistory> debtHistories = new ArrayList<>();
        String workName = (debt.getDebtType().equals("채권") ? "채권 상환" : "채무 변제");
        for(int i = 0; i < date.length; i++){
            debtHistories.add(createDebtHistory(createWork(staff, workTypeRepository.findByWorkName(workName)), debt, date[i], amount[i],status[i]));
        }
        return debtHistories;
    }

    public boolean checkRestZero(Debt debt){
        return debt.getRest() == 0;
    }

    public String checkCompletelyReceived(Debt debt){
        return checkRestZero(debt) ? "전액 변제받음" : (debt.getRest() == debt.getTotal() ? "변제 받기 전" : "변제 받는 중");
    }

    public String checkCompletelyPaid(Debt debt){
        return checkRestZero(debt) ? "전액 변제 완료" : (debt.getRest() == debt.getTotal() ? "변제 전" : "변제 중");
    }

    public String checkDebt(Debt debt){
        String debtType = debt.getDebtType();
        switch(debtType){
            case "채권" -> {
                return checkCompletelyReceived(debt);
            }
            case "채무" -> {
                return checkCompletelyPaid(debt);
            }
            default -> throw new NullPointerException();

        }
    }

    public DebtHistory updateDebtHistory(DebtHistory debtHistory, LocalDate date, long amount, String status){
        debtHistory.setDate(date);
        debtHistory.setAmount(amount);
        debtHistory.setStatus(status);
        debtHistory.setStatement("비고 수정");
        return debtHistoryRepository.save(debtHistory);
    }

    public List<DebtHistory> updateDebtHistories(Staff staff, Debt debt, LocalDate [] date, long [] amount, String [] status){
        List<DebtHistory> updated = debt.getDebtHistory();
        for(int i = 0; i < updated.size(); i++){
            updateDebtHistory(updated.get(i), date[i], amount[i], status[i]);
        }
        if(date.length > updated.size()){
            for(int i = updated.size(); i < date.length; i++){
                updated.add(createDebtHistory(createWork(staff, workTypeRepository.findByWorkName("채권 상환")), debt, date[i], amount[i], status[i]));
            }
        }
        return updated;
    }

    public Long deleteDebtHistory(Work work, DebtHistory debtHistory){
        if(debtHistory != null){
            Debt debt = debtHistory.getDebt();
            List<DebtHistory> debtHistories = debt.getDebtHistory();
            work.setDraftState(WorkState.DELETE);
            workRepository.save(work);

            debtHistories.remove(debtHistory);
            debt.setDebtHistory(debtHistories);

            debtRepository.save(debt);
            debtHistoryRepository.delete(debtHistory);
            return work.getWorkId();
        }
        else {
            throw new NullPointerException();
        }
    }

    public void displayDebt(Debt debt){
        System.out.println("-----");
        System.out.println("고객 정보");
        System.out.println("-----");
        System.out.println("고객 신용 정보");
        System.out.println("-----");
        System.out.println("항목 : " + debt.getItem());
        System.out.println("유형 : " + debt.getDebtType());
        System.out.println("세부 유형 : " + debt.getType());
        System.out.println("채권(채무)액 : " + debt.getTotal());
        System.out.println("잔금 : " + debt.getRest());
        System.out.println("변제 여부 : " + checkDebt(debt));
        System.out.println("거래일 : " + debt.getStart());
        System.out.println("만료일 : " + debt.getExpire());
        System.out.println("-----");
        if(!debt.getDebtHistory().isEmpty()) {
            for(DebtHistory history : debt.getDebtHistory()){
                displayDebtHistory(history);
            }
            System.out.println("-----");
        }
    }
    public void displayDebtHistory(DebtHistory debtHistory){
        System.out.println("- - -");
        System.out.println("상환금액 : " + debtHistory.getAmount());
        System.out.println("상환일자 : " + debtHistory.getDate());
        System.out.println("비고 : " + debtHistory.getStatement());
        System.out.println("- - -");
    }

    // 채권 테스트
    @DisplayName("채권 추가")
    @Test
    @Transactional
    public void createReceivable(){
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Debt receivable = createReceivable(work, "항목", "타입", 100000);

        System.out.println("생성되었는지? " + (receivable != null));
        displayWork(work);
        displayDebt(receivable);
    }

    @DisplayName("채권 수정")
    @Test
    @Transactional
    public void updateReceivable(){
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Debt receivable = createReceivable(work, "항목", "타입", 100000);

        receivable = updateReceivable(receivable, "항목 수정", "유형 수정", 130000);

        displayWork(work);
        displayDebt(receivable);
    }

    @DisplayName("채권 승인&출력")
    @Test
    @Transactional
    public void allowReceivable(){
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Debt created = createReceivable(work, "항목", "타입", 100000);

        Long workId = approveWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Debt receivable = debtRepository.findByWork(work);

        displayWork(receivable.getWork());
        displayDebt(receivable);
    }

    @DisplayName("채권 보류&출력")
    @Test
    @Transactional
    public void delayReceivable(){
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Debt created = createReceivable(work, "항목", "타입", 100000);

        Long workId = deferWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Debt receivable = debtRepository.findByWork(work);

        displayWork(receivable.getWork());
        displayDebt(receivable);
    }

    @DisplayName("채권 거절&출력")
    @Test
    @Transactional
    public void rejectReceivable() {
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Debt created = createReceivable(work, "항목", "타입", 100000);

        Long workId = rejectWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Debt receivable = debtRepository.findByWork(work);

        displayWork(receivable.getWork());
        displayDebt(receivable);
    }
    @DisplayName("채권 삭제")
    @Test
    @Transactional
    public void deleteReceivable(){
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work = createWork(staff, workType);

        Debt created = createReceivable(work, "항목", "타입", 100000);

        work = workRepository.findByWorkId(deleteDebt(work, created));
        Debt debt = debtRepository.findByWork(work);
        displayWork(work);
        System.out.println("삭제 여부 : " + (debt == null));
    }

    // 채권 상환 테스트
    @DisplayName("채권 상환 추가")
    @Test
    @Transactional
    public void createReceivableHistory(){
        WorkType receivableType = workTypeRepository.findByWorkName("채권");
        WorkType historyType = workTypeRepository.findByWorkName("채권 상환");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, receivableType);

        Debt receivable = createReceivable(work, "항목", "타입", 100000);

        LocalDate [] date = new LocalDate[] {
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2024, 9, 1)
        };
        long [] amount = new long[] {
                10000,
                10000,
                10000,
                10000,
                10000
        };
        String [] status = new String[] {
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
        };
        List<DebtHistory> debtHistories = createDebtHistories(staff, receivable, date, amount, status);
        receivable.setDebtHistory(debtHistories);

        receivable = debtRepository.save(receivable);

        displayDebt(receivable);
    }

    @DisplayName("채권 상환 수정")
    @Test
    @Transactional
    public void updateReceivableHistory(){
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Debt receivable = createReceivable(work, "항목", "타입", 100000);
        LocalDate [] date = new LocalDate[] {
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2024, 9, 1)
        };
        long [] amount = new long[] {
                10000,
                10000,
                10000,
                10000,
                10000
        };
        String [] status = new String[] {
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
        };
        List<DebtHistory> debtHistories = createDebtHistories(staff, receivable, date, amount, status);
        receivable.setDebtHistory(debtHistories);

        receivable = debtRepository.save(receivable);

        LocalDate [] uDate = new LocalDate[] {
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2024, 9, 1)
        };
        long [] uAmount = new long[] {
                10000,
                10000,
                10000,
                10000,
                10000,
                10000,
                10000,
                10000
        };
        String [] uStatus = new String[] {
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
        };
        debtHistories = updateDebtHistories(staff, receivable, uDate, uAmount, uStatus);
        receivable.setDebtHistory(debtHistories);

        receivable = debtRepository.save(receivable);

        displayWork(work);
        displayDebt(receivable);
    }

    @DisplayName("채권 상환 승인&출력")
    @Test
    @Transactional
    public void allowReceivableHistory(){
        WorkType receivableType = workTypeRepository.findByWorkName("채권");
        WorkType historyType = workTypeRepository.findByWorkName("채권 상환");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work receivableWork =  createWork(staff1, receivableType);
        Work historyWork = createWork(staff1, historyType);

        Debt receivable = createReceivable(receivableWork, "항목", "타입", 100000);
        DebtHistory history = createDebtHistory(historyWork, receivable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = receivable.getDebtHistory();
        debtHistories.add(history);
        receivable.setDebtHistory(debtHistories);

        debtRepository.save(receivable);

        Long receivableWorkId = approveWork(staff2, receivableWork);
        Long historyWorkId = approveWork(staff2, historyWork);

        receivableWork = workRepository.findByWorkId(receivableWorkId);
        historyWork = workRepository.findByWorkId(historyWorkId);

        displayWork(receivableWork);
        displayWork(historyWork);
    }

    @DisplayName("채권 상환 보류&출력")
    @Test
    @Transactional
    public void delayReceivableHistory(){
        WorkType receivableType = workTypeRepository.findByWorkName("채권");
        WorkType historyType = workTypeRepository.findByWorkName("채권 상환");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work receivableWork =  createWork(staff1, receivableType);
        Work historyWork = createWork(staff1, historyType);

        Debt receivable = createReceivable(receivableWork, "항목", "타입", 100000);
        DebtHistory history = createDebtHistory(historyWork, receivable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = receivable.getDebtHistory();
        debtHistories.add(history);
        receivable.setDebtHistory(debtHistories);

        debtRepository.save(receivable);

        Long receivableWorkId = approveWork(staff2, receivableWork);
        Long historyWorkId = deferWork(staff2, historyWork);

        receivableWork = workRepository.findByWorkId(receivableWorkId);
        historyWork = workRepository.findByWorkId(historyWorkId);

        displayWork(receivableWork);
        displayWork(historyWork);
    }

    @DisplayName("채권 상환 거절&출력")
    @Test
    @Transactional
    public void rejectReceivableHistory() {
        WorkType receivableType = workTypeRepository.findByWorkName("채권");
        WorkType historyType = workTypeRepository.findByWorkName("채권 상환");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work receivableWork =  createWork(staff1, receivableType);
        Work historyWork = createWork(staff1, historyType);

        Debt receivable = createReceivable(receivableWork, "항목", "타입", 100000);
        DebtHistory history = createDebtHistory(historyWork, receivable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = receivable.getDebtHistory();
        debtHistories.add(history);
        receivable.setDebtHistory(debtHistories);

        debtRepository.save(receivable);

        Long receivableWorkId = approveWork(staff2, receivableWork);
        Long historyWorkId = rejectWork(staff2, historyWork);

        receivableWork = workRepository.findByWorkId(receivableWorkId);
        historyWork = workRepository.findByWorkId(historyWorkId);

        displayWork(receivableWork);
        displayWork(historyWork);
    }
    @DisplayName("채권 항목 삭제")
    @Test
    @Transactional
    public void
    deleteReceivableHistory(){
        WorkType receivableType = workTypeRepository.findByWorkName("채권");
        WorkType historyType = workTypeRepository.findByWorkName("채권 상환");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Work receivableWork =  createWork(staff1, receivableType);
        Work historyWork = createWork(staff1, historyType);

        Debt receivable = createReceivable(receivableWork, "항목", "타입", 100000);
        DebtHistory created = createDebtHistory(historyWork, receivable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = receivable.getDebtHistory();
        debtHistories.add(created);
        receivable.setDebtHistory(debtHistories);

        debtRepository.save(receivable);

        DebtHistory history = debtHistoryRepository.findByWork(historyWork);
        Long workId = deleteDebtHistory(historyWork, history);

        historyWork = workRepository.findByWorkId(workId);
        history = debtHistoryRepository.findByWork(historyWork);

        displayWork(historyWork);
        System.out.println("삭제 여부 : " + (history == null));
    }

    // 채무 테스트
    @DisplayName("채무 추가")
    @Test
    @Transactional
    public void createPayable(){
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Debt payable = createPayable(work, "항목", "타입", 100000);

        System.out.println("생성되었는지? " + (payable != null));
        displayWork(work);
        displayDebt(payable);
    }

    @DisplayName("채무 수정")
    @Test
    @Transactional
    public void updatePayable(){
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Debt payable = createPayable(work, "항목", "타입", 100000);

        payable = updateReceivable(payable, "항목 수정", "유형 수정", 130000);

        displayWork(work);
        displayDebt(payable);
    }

    @DisplayName("채무 승인&출력")
    @Test
    @Transactional
    public void allowPayable(){
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Debt created = createPayable(work, "항목", "타입", 100000);

        Long workId = approveWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Debt payable = debtRepository.findByWork(work);

        displayWork(payable.getWork());
        displayDebt(payable);
    }

    @DisplayName("채무 보류&출력")
    @Test
    @Transactional
    public void delayPayable(){
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Debt created = createPayable(work, "항목", "타입", 100000);

        Long workId = deferWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Debt payable = debtRepository.findByWork(work);

        displayWork(payable.getWork());
        displayDebt(payable);
    }

    @DisplayName("채무 거절&출력")
    @Test
    @Transactional
    public void rejectPayable() {
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        Debt created = createPayable(work, "항목", "타입", 100000);

        Long workId = rejectWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        Debt payable = debtRepository.findByWork(work);

        displayWork(payable.getWork());
        displayDebt(payable);
    }
    @DisplayName("채무 삭제")
    @Test
    @Transactional
    public void deletePayable(){
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work = createWork(staff, workType);

        Debt created = createPayable(work, "항목", "타입", 100000);
        displayDebt(created);

        work = workRepository.findByWorkId(deleteDebt(work, created));
        Debt debt = debtRepository.findByWork(work);
        displayWork(work);
        System.out.println("삭제 여부 : " + (debt == null));
    }

    // 채무 상환 테스트
    @DisplayName("채무 상환 추가")
    @Test
    @Transactional
    public void createPayableHistory(){
        WorkType receivableType = workTypeRepository.findByWorkName("채무");
        WorkType historyType = workTypeRepository.findByWorkName("채무 변제");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, receivableType);

        Debt payable = createPayable(work, "항목", "타입", 100000);

        LocalDate [] date = new LocalDate[] {
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2024, 9, 1)
        };
        long [] amount = new long[] {
                10000,
                10000,
                10000,
                10000,
                10000
        };
        String [] status = new String[] {
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환"
        };
        List<DebtHistory> debtHistories = createDebtHistories(staff, payable, date, amount, status);
        payable.setDebtHistory(debtHistories);

        payable = debtRepository.save(payable);

        displayDebt(payable);
    }

    @DisplayName("채무 상환 수정")
    @Test
    @Transactional
    public void updatePayableHistory(){
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        Debt payable = createPayable(work, "항목", "타입", 100000);
        LocalDate [] date = new LocalDate[] {
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2024, 9, 1)
        };
        long [] amount = new long[] {
                10000,
                10000,
                10000,
                10000,
                10000
        };
        String [] status = new String[] {
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
        };
        List<DebtHistory> debtHistories = createDebtHistories(staff, payable, date, amount, status);
        payable.setDebtHistory(debtHistories);

        payable = debtRepository.save(payable);

        LocalDate [] uDate = new LocalDate[] {
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 8, 1),
                LocalDate.of(2024, 9, 1)
        };
        long [] uAmount = new long[] {
                10000,
                10000,
                10000,
                10000,
                10000,
                10000,
                10000,
                10000
        };
        String [] uStatus = new String[] {
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
                "중도 상환",
        };
        debtHistories = updateDebtHistories(staff, payable, uDate, uAmount, uStatus);
        payable.setDebtHistory(debtHistories);

        payable = debtRepository.save(payable);

        displayWork(work);
        displayDebt(payable);
    }

    @DisplayName("채무 상환 승인&출력")
    @Test
    @Transactional
    public void allowPayableHistory(){
        WorkType payableType = workTypeRepository.findByWorkName("채무");
        WorkType historyType = workTypeRepository.findByWorkName("채무 변제");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work payableWork =  createWork(staff1, payableType);
        Work historyWork = createWork(staff1, historyType);

        Debt payable = createReceivable(payableWork, "항목", "타입", 100000);
        DebtHistory history = createDebtHistory(historyWork, payable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = payable.getDebtHistory();
        debtHistories.add(history);
        payable.setDebtHistory(debtHistories);

        debtRepository.save(payable);

        Long receivableWorkId = approveWork(staff2, payable.getWork());
        Long historyWorkId = approveWork(staff2, history.getWork());

        payableWork = workRepository.findByWorkId(receivableWorkId);
        historyWork = workRepository.findByWorkId(historyWorkId);

        displayWork(payableWork);
        displayWork(historyWork);
    }

    @DisplayName("채무 상환 보류&출력")
    @Test
    @Transactional
    public void delayPayableHistory(){
        WorkType payableType = workTypeRepository.findByWorkName("채무");
        WorkType historyType = workTypeRepository.findByWorkName("채무 변제");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work payableWork =  createWork(staff1, payableType);
        Work historyWork = createWork(staff1, historyType);

        Debt payable = createReceivable(payableWork, "항목", "타입", 100000);
        DebtHistory history = createDebtHistory(historyWork, payable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = payable.getDebtHistory();
        debtHistories.add(history);
        payable.setDebtHistory(debtHistories);

        debtRepository.save(payable);

        Long receivableWorkId = approveWork(staff2, payableWork);
        Long historyWorkId = deferWork(staff2, historyWork);

        payableWork = workRepository.findByWorkId(receivableWorkId);
        historyWork = workRepository.findByWorkId(historyWorkId);

        displayWork(payableWork);
        displayWork(historyWork);
    }

    @DisplayName("채무 상환 거절&출력")
    @Test
    @Transactional
    public void rejectPayableHistory() {
        WorkType payableType = workTypeRepository.findByWorkName("채무");
        WorkType historyType = workTypeRepository.findByWorkName("채무 변제");

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work payableWork =  createWork(staff1, payableType);
        Work historyWork = createWork(staff1, historyType);

        Debt payable = createReceivable(payableWork, "항목", "타입", 100000);
        DebtHistory history = createDebtHistory(historyWork, payable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = payable.getDebtHistory();
        debtHistories.add(history);
        payable.setDebtHistory(debtHistories);

        debtRepository.save(payable);

        Long receivableWorkId = approveWork(staff2, payableWork);
        Long historyWorkId = rejectWork(staff2, historyWork);

        payableWork = workRepository.findByWorkId(receivableWorkId);
        historyWork = workRepository.findByWorkId(historyWorkId);

        displayWork(payableWork);
        displayWork(historyWork);
    }
    @DisplayName("채무 항목 삭제")
    @Test
    @Transactional
    public void
    deletePayableHistory() {
        WorkType payableType = workTypeRepository.findByWorkName("채무");
        WorkType historyType = workTypeRepository.findByWorkName("채무 변제");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Work payableWork = createWork(staff1, payableType);
        Work historyWork = createWork(staff1, historyType);

        Debt payable = createReceivable(payableWork, "항목", "타입", 100000);
        DebtHistory created = createDebtHistory(historyWork, payable, LocalDate.of(2024, 5, 1), 10000, "중도상환");

        List<DebtHistory> debtHistories = payable.getDebtHistory();
        debtHistories.add(created);
        payable.setDebtHistory(debtHistories);

        debtRepository.save(payable);

        DebtHistory history = debtHistoryRepository.findByWork(historyWork);
        Long workId = deleteDebtHistory(historyWork, history);

        historyWork = workRepository.findByWorkId(workId);
        history = debtHistoryRepository.findByWork(historyWork);

        displayWork(historyWork);
        System.out.println("삭제 여부 : " + (history == null));
    }

    // 자산관리 테스트
    public AssetType createAssetType(Work work, String superCode, String type, int usefulYear, String depreciationMethod){
        AssetType assetType = new AssetType();
        assetType.setWork(work);
        assetType.setSuperCode(superCode);
        assetType.setType(type);
        assetType.setUsefulYear(usefulYear);
        assetType.setDepreciationMethod(depreciationMethod);
        assetType.setAsset(new ArrayList<>());
        return assetTypeRepository.save(assetType);
    }

    public AssetType updateAssetType(AssetType assetType, String superCode, String type, int usefulYear, String depreciationMethod){
        assetType.setSuperCode(superCode);
        assetType.setType(type);
        assetType.setUsefulYear(usefulYear);
        assetType.setDepreciationMethod(depreciationMethod);
        return assetTypeRepository.save(assetType);
    }

    public Long deleteAssetType(Work work, AssetType assetType){
        if(assetType != null){
            work.setDraftState(WorkState.DELETE);
            assetTypeRepository.delete(assetType);
            return workRepository.save(work).getWorkId();
        }
        else{
            throw new NullPointerException("삭제할 데이터가 존재하지 않습니다.");
        }
    }

    public Asset createAsset(Work work, AssetType assetType, String name, int quantity, long price, String acquisitionMethod, String department){
        Asset asset = new Asset();
        asset.setWork(work);
        asset.setAssetType(assetType);
        asset.setName(name);
        asset.setDate(LocalDate.now());
        asset.setQuantity(quantity);
        asset.setPrice(price);
        asset.setAcquisitionMethod(acquisitionMethod);
        asset.setResidualValue((long) Math.floor(price / 10.0));
        asset.setDepreciation(calculateDepreciation(assetType.getDepreciationMethod(), asset.getPrice(), assetType.getUsefulYear(), asset.getResidualValue()));
        asset.setDepartment(departmentRepository.findByDepartmentName(department));
        asset.setStatus("사용");
        asset.setSubCode("자산코드(서브)");
        asset.setAssetHistory(new ArrayList<>());
        return assetRepository.save(asset);
    }

    public double calculateDepreciation(String depreciationMethod, double price, double usefulYear, double residualValue){
        double percent = -1;
        if (depreciationMethod.equals("정액법")){
            percent = (price - residualValue) / usefulYear;
        }
        else if(depreciationMethod.equals("정률법")){
            if(residualValue == 0){
                return percent;
            }
            double a = 1.0 / usefulYear;
            percent = 1.0 - Math.pow(residualValue/price, a);
        }
        return Math.round(percent*1000)/1000.0;
    }

    public Asset updateAsset(Asset asset, String name, int quantity, long price, String acquisitionMethod, String department){
        AssetType assetType = asset.getAssetType();
        asset.setName(name);
        asset.setQuantity(quantity);
        asset.setPrice(price);
        asset.setAcquisitionMethod(acquisitionMethod);
        asset.setResidualValue((long) Math.floor(price / 10.0));
        asset.setDepreciation(calculateDepreciation(assetType.getDepreciationMethod(), asset.getPrice(), assetType.getUsefulYear(), asset.getResidualValue()));
        asset.setDepartment(departmentRepository.findByDepartmentName(department));
        return assetRepository.save(asset);
    }

    public Long deleteAsset(Work work, Asset asset){
        if(asset != null){
            AssetType assetType = asset.getAssetType();
            List<Asset> assets = assetType.getAsset();
            work.setDraftState(WorkState.DELETE);

            assets.remove(asset);
            assetType.setAsset(assets);
            assetRepository.delete(asset);
            assetTypeRepository.save(assetType);
            return workRepository.save(work).getWorkId();
        }
        else{
            throw new NullPointerException("삭제할 데이터가 존재하지 않습니다.");
        }
    }

    public AssetHistory createAssetHistory(Work work, Asset asset, int month){
        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setWork(work);
        assetHistory.setAsset(asset);

        List<AssetHistory> history = asset.getAssetHistory();
        long formerAccumulatedDepreciation = (history.isEmpty() ? 0 : history.get(history.size()-1).getCurrentAccumulatedDepreciation());

        assetHistory.setDate(LocalDate.now());
        assetHistory.setMonth(month);
        assetHistory.setFormerAccumulatedDepreciation(formerAccumulatedDepreciation);
        assetHistory.setCurrentDepreciation(calculateCurrentDepreciation(asset, assetHistory, month));
        assetHistory.setCurrentAccumulatedDepreciation(assetHistory.getFormerAccumulatedDepreciation() + assetHistory.getCurrentDepreciation());
        assetHistory.setCurrentResidualValue(asset.getPrice()-assetHistory.getCurrentAccumulatedDepreciation());
        return assetHistoryRepository.save(assetHistory);
    }

    public long calculateCurrentDepreciation(Asset asset, AssetHistory assetHistory, int month){
        double currentDepreciation = 0.0;
        if (asset.getAssetType().getDepreciationMethod().equals("정액법")){
            currentDepreciation = asset.getDepreciation();
        }
        else if(asset.getAssetType().getDepreciationMethod().equals("정률법")){
            double target = asset.getPrice() - assetHistory.getFormerAccumulatedDepreciation();
            currentDepreciation = Math.ceil(target * asset.getDepreciation());
        }
        return month == 12 ? (long) currentDepreciation : (long) (currentDepreciation * (month / 12.0));
    }

    public AssetHistory updateAssetHistory(Work work, AssetHistory assetHistory, int month){
        List<AssetHistory> history = assetHistory.getAsset().getAssetHistory();
        long formerAccumulatedDepreciation = (history.isEmpty() ? 0 : history.get(history.size()-1).getCurrentAccumulatedDepreciation());

        assetHistory.setDate(LocalDate.now());
        assetHistory.setMonth(month);
        assetHistory.setFormerAccumulatedDepreciation(formerAccumulatedDepreciation);
        assetHistory.setCurrentDepreciation(calculateCurrentDepreciation(assetHistory.getAsset(), assetHistory, month));
        assetHistory.setCurrentAccumulatedDepreciation(assetHistory.getFormerAccumulatedDepreciation() + assetHistory.getCurrentDepreciation());
        assetHistory.setCurrentResidualValue(assetHistory.getAsset().getPrice()-assetHistory.getCurrentAccumulatedDepreciation());
        return assetHistoryRepository.save(assetHistory);
    }

    public Long deleteAssetHistory(Work work, AssetHistory assetHistory){
        if(assetHistory != null){
            Asset asset = assetHistory.getAsset();
            List<AssetHistory> assetHistories = asset.getAssetHistory();
            work.setDraftState(WorkState.DELETE);

            assetHistories.remove(assetHistory);
            asset.setAssetHistory(assetHistories);
            assetRepository.save(asset);

            assetHistoryRepository.delete(assetHistory);
            return workRepository.save(work).getWorkId();
        }
        else {
            throw new NullPointerException("삭제할 데이터가 존재하지 않습니다.");
        }
    }

    public void displayAssetType(AssetType assetType){
        System.out.println("-----");
        System.out.println("자산코드(대분류) : " + assetType.getSuperCode());
        System.out.println("자산분류 : " + assetType.getType());
        System.out.println("내용년수 : " + assetType.getUsefulYear());
        System.out.println("상각방법 : " + assetType.getDepreciationMethod());
        System.out.println("-----");
    }

    public void displayAsset(Asset asset){
        System.out.println("-----");
        System.out.println("자산이름 : " + asset.getName());
        System.out.println("취득일자 : " + asset.getDate());
        System.out.println("취득수량 : " + asset.getQuantity());
        System.out.println("취득가액 : " + asset.getPrice());
        System.out.println("잔존가치 : " + asset.getResidualValue());
        System.out.println("상각비 : " + asset.getDepreciation());
        System.out.println("사용부서 : " + asset.getDepartment().getDepartmentName());
        System.out.println("자산상태 : " + asset.getStatus());
        System.out.println("-----");
    }

    public void displayAssetHistory(AssetHistory assetHistory){
        System.out.println("-----");
        System.out.println("일자 : " + assetHistory.getDate());
        System.out.println("상각월수 : " + assetHistory.getMonth());
        System.out.println("전기말 감가상각 누계액 : " + assetHistory.getFormerAccumulatedDepreciation());
        System.out.println("당기 감가상각액 : " + assetHistory.getCurrentDepreciation());
        System.out.println("당기말 감가상각 누계액 : " + assetHistory.getCurrentAccumulatedDepreciation());
        System.out.println("당기 잔존가액 : " + assetHistory.getCurrentResidualValue());
        System.out.println("-----");
    }

    // 자산유형 테스트
    @DisplayName("자산유형 생성")
    @Test
    @Transactional
    public void createAssetType(){
        WorkType workType = workTypeRepository.findByWorkName("자산유형");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        AssetType created = createAssetType(work, "자산코드", "자산분류", 5, "정액법");

        AssetType assetType = assetTypeRepository.findByWork(created.getWork());

        System.out.println("생성되었는지? " + ( assetType != null));
        displayWork(work);
        displayAssetType(assetType);
    }

    @DisplayName("자산유형 수정")
    @Test
    @Transactional
    public void updateAssetType(){
        WorkType workType = workTypeRepository.findByWorkName("자산유형");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        AssetType created = createAssetType(work, "자산코드", "자산분류", 5, "정액법");

        updateAssetType(created, "자산코드 수정", "자산분류 수정", 5, "정률법");

        work = workRepository.findByWorkId(work.getWorkId());
        AssetType assetType = assetTypeRepository.findByWork(work);

        displayWork(work);
        displayAssetType(assetType);
    }

    @DisplayName("자산유형 승인&출력")
    @Test
    @Transactional
    public void allowAssetType(){
        WorkType workType = workTypeRepository.findByWorkName("자산유형");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        AssetType created = createAssetType(work, "자산코드", "자산분류", 5, "정액법");

        Long workId = approveWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        AssetType assetType = assetTypeRepository.findByWork(work);

        displayWork(work);
        displayAssetType(assetType);
    }

    @DisplayName("자산유형 보류&출력")
    @Test
    @Transactional
    public void delayAssetType(){
        WorkType workType = workTypeRepository.findByWorkName("자산유형");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        AssetType created = createAssetType(work, "자산코드", "자산분류", 5, "정액법");

        Long workId = deferWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        AssetType assetType = assetTypeRepository.findByWork(work);

        displayWork(work);
        displayAssetType(assetType);
    }

    @DisplayName("자산유형 거절&출력")
    @Test
    @Transactional
    public void rejectAssetType() {
        WorkType workType = workTypeRepository.findByWorkName("자산유형");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        AssetType created = createAssetType(work, "자산코드", "자산분류", 5, "정액법");

        Long workId = rejectWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        AssetType assetType = assetTypeRepository.findByWork(work);

        displayWork(work);
        displayAssetType(assetType);
    }
    @DisplayName("자산유형 삭제")
    @Test
    @Transactional
    public void deleteAssetType(){
        WorkType workType = workTypeRepository.findByWorkName("자산유형");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work = createWork(staff, workType);

        AssetType created = createAssetType(work, "자산코드", "자산분류", 5, "정액법");
        displayAssetType(created);

        work = workRepository.findByWorkId(deleteAssetType(work, created));
        AssetType assetType = assetTypeRepository.findByWork(work);
        displayWork(work);
        System.out.println("삭제 여부 : " + (assetType == null));
    }

    // 자산관리 테스트
    @DisplayName("자산관리 생성")
    @Test
    @Transactional
    public void createAsset(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리")
        };

        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff, assetWorkType[0]);
        Work assetWork = createWork(staff, assetWorkType[1]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        AssetType assetType = assetTypeRepository.findByWork(assetTypeWork);
        Asset asset = assetRepository.findByWork(assetWork);

        System.out.println("포함여부 : " + (assetType.getAsset().contains(asset)));
        displayWork(assetWork);
        displayAsset(asset);
    }

    @DisplayName("자산관리 수정")
    @Test
    @Transactional
    public void updateAsset(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리")
        };

        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff, assetWorkType[0]);
        Work assetWork = createWork(staff, assetWorkType[1]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000,"구입", "재무회계부");

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);
        
        updateAsset(createdAsset, "자산이름 수정", 10, 10000000,  "양도", "재무회계부");

        AssetType assetType = assetTypeRepository.findByWork(assetTypeWork);
        Asset asset = assetRepository.findByWork(assetWork);

        displayWork(assetWork);
        displayAsset(asset);
    }

    @DisplayName("자산관리 승인&출력")
    @Test
    @Transactional
    public void allowAsset(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리")
        };
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff1, assetWorkType[0]);
        Work assetWork = createWork(staff1, assetWorkType[1]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        Long assetTypeWorkId = approveWork(staff2, assetTypeWork);
        Long assetWorkId = approveWork(staff2, assetWork);

        assetTypeWork = workRepository.findByWorkId(assetTypeWorkId);
        assetWork = workRepository.findByWorkId(assetWorkId);
        Asset asset = assetRepository.findByWork(assetWork);

        displayWork(assetTypeWork);
        displayWork(assetWork);
        displayAsset(asset);
    }

    @DisplayName("자산관리 보류&출력")
    @Test
    @Transactional
    public void delayAsset(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리")
        };
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff1, assetWorkType[0]);
        Work assetWork = createWork(staff1, assetWorkType[1]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        Long assetTypeWorkId = approveWork(staff2, assetTypeWork);
        Long assetWorkId = deferWork(staff2, assetWork);

        assetTypeWork = workRepository.findByWorkId(assetTypeWorkId);
        assetWork = workRepository.findByWorkId(assetWorkId);
        Asset asset = assetRepository.findByWork(assetWork);

        displayWork(assetTypeWork);
        displayWork(assetWork);
        displayAsset(asset);
    }

    @DisplayName("자산관리 거절&출력")
    @Test
    @Transactional
    public void rejectAsset() {
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리")
        };
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff1, assetWorkType[0]);
        Work assetWork = createWork(staff1, assetWorkType[1]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        Long assetTypeWorkId = approveWork(staff2, assetTypeWork);
        Long assetWorkId = rejectWork(staff2, assetWork);

        assetTypeWork = workRepository.findByWorkId(assetTypeWorkId);
        assetWork = workRepository.findByWorkId(assetWorkId);
        Asset asset = assetRepository.findByWork(assetWork);

        displayWork(assetTypeWork);
        displayWork(assetWork);
        displayAsset(asset);
    }
    @DisplayName("자산관리 삭제")
    @Test
    @Transactional
    public void deleteAsset(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리")
        };

        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff, assetWorkType[0]);
        Work assetWork = createWork(staff, assetWorkType[1]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        Long assetWorkId = deleteAsset(assetWork, createdAsset);

        Work work = workRepository.findByWorkId(assetWorkId);
        AssetType assetType = assetTypeRepository.findByWork(assetTypeWork);
        displayWork(work);
        System.out.println("삭제 여부 : " + (assetType.getAsset().isEmpty()));
    }

    // 감가상각 테스트
    @DisplayName("감가상각 생성")
    @Test
    @Transactional
    public void createAssetHistory(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리"), workTypeRepository.findByWorkName("감가상각")
        };

        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff, assetWorkType[0]);
        Work assetWork = createWork(staff, assetWorkType[1]);
        Work assetHistoryWork = createWork(staff, assetWorkType[2]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");
        AssetHistory createdAssetHistory = createAssetHistory(assetHistoryWork, createdAsset, 12);

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        List<AssetHistory> assetHistories = createdAsset.getAssetHistory();
        assetHistories.add(createdAssetHistory);
        createdAsset.setAssetHistory(assetHistories);
        assetRepository.save(createdAsset);

        AssetType assetType = assetTypeRepository.findByWork(assetTypeWork);
        Asset asset = assetRepository.findByWork(assetWork);
        AssetHistory assetHistory = assetHistoryRepository.findByWork(assetHistoryWork);

        System.out.println("포함여부 : " + (asset.getAssetHistory().contains(assetHistory)));
        displayWork(assetHistoryWork);
        displayAssetType(assetType);
        displayAsset(asset);
        displayAssetHistory(assetHistory);
    }

    @DisplayName("감가상각 승인&출력")
    @Test
    @Transactional
    public void allowAssetHistory(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리"), workTypeRepository.findByWorkName("감가상각")
        };
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff1, assetWorkType[0]);
        Work assetWork = createWork(staff1, assetWorkType[1]);
        Work assetHistoryWork = createWork(staff1, assetWorkType[2]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");
        AssetHistory createdAssetHistory = createAssetHistory(assetHistoryWork, createdAsset, 12);

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        List<AssetHistory> assetHistories = createdAsset.getAssetHistory();
        assetHistories.add(createdAssetHistory);
        createdAsset.setAssetHistory(assetHistories);
        assetRepository.save(createdAsset);

        Long assetTypeWorkId = approveWork(staff2, assetTypeWork);
        Long assetWorkId = approveWork(staff2, assetWork);
        Long assetHistoryId = approveWork(staff2, assetHistoryWork);

        assetTypeWork = workRepository.findByWorkId(assetTypeWorkId);
        assetWork = workRepository.findByWorkId(assetWorkId);
        assetHistoryWork = workRepository.findByWorkId(assetHistoryId);

        displayWork(assetTypeWork);
        displayWork(assetWork);
        displayWork(assetHistoryWork);
    }

    @DisplayName("감가상각 보류&출력")
    @Test
    @Transactional
    public void delayAssetHistory(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리"), workTypeRepository.findByWorkName("감가상각")
        };
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff1, assetWorkType[0]);
        Work assetWork = createWork(staff1, assetWorkType[1]);
        Work assetHistoryWork = createWork(staff1, assetWorkType[2]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");
        AssetHistory createdAssetHistory = createAssetHistory(assetHistoryWork, createdAsset, 12);

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        List<AssetHistory> assetHistories = createdAsset.getAssetHistory();
        assetHistories.add(createdAssetHistory);
        createdAsset.setAssetHistory(assetHistories);
        assetRepository.save(createdAsset);

        Long assetTypeWorkId = approveWork(staff2, assetTypeWork);
        Long assetWorkId = approveWork(staff2, assetWork);
        Long assetHistoryId = deferWork(staff2, assetHistoryWork);

        assetTypeWork = workRepository.findByWorkId(assetTypeWorkId);
        assetWork = workRepository.findByWorkId(assetWorkId);
        assetHistoryWork = workRepository.findByWorkId(assetHistoryId);

        displayWork(assetTypeWork);
        displayWork(assetWork);
        displayWork(assetHistoryWork);
    }

    @DisplayName("감가상각 거절&출력")
    @Test
    @Transactional
    public void rejectAssetHistory() {
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리"), workTypeRepository.findByWorkName("감가상각")
        };
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff1, assetWorkType[0]);
        Work assetWork = createWork(staff1, assetWorkType[1]);
        Work assetHistoryWork = createWork(staff1, assetWorkType[2]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");
        AssetHistory createdAssetHistory = createAssetHistory(assetHistoryWork, createdAsset, 12);

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        List<AssetHistory> assetHistories = createdAsset.getAssetHistory();
        assetHistories.add(createdAssetHistory);
        createdAsset.setAssetHistory(assetHistories);
        assetRepository.save(createdAsset);

        Long assetTypeWorkId = approveWork(staff2, assetTypeWork);
        Long assetWorkId = approveWork(staff2, assetWork);
        Long assetHistoryId = rejectWork(staff2, assetHistoryWork);

        assetTypeWork = workRepository.findByWorkId(assetTypeWorkId);
        assetWork = workRepository.findByWorkId(assetWorkId);
        assetHistoryWork = workRepository.findByWorkId(assetHistoryId);

        displayWork(assetTypeWork);
        displayWork(assetWork);
        displayWork(assetHistoryWork);
    }
    @DisplayName("감가상각 삭제")
    @Test
    @Transactional
    public void deleteAssetHistory(){
        WorkType [] assetWorkType = new WorkType[] {
                workTypeRepository.findByWorkName("자산유형"), workTypeRepository.findByWorkName("자산관리"), workTypeRepository.findByWorkName("감가상각")
        };

        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);

        Work assetTypeWork = createWork(staff, assetWorkType[0]);
        Work assetWork = createWork(staff, assetWorkType[1]);
        Work assetHistoryWork = createWork(staff, assetWorkType[2]);

        AssetType createdAssetType = createAssetType(assetTypeWork, "자산코드", "자산분류", 5, "정률법");
        Asset createdAsset = createAsset(assetWork, createdAssetType, "자산이름", 1, 1000000, "구입", "재무회계부");
        AssetHistory createdAssetHistory = createAssetHistory(assetHistoryWork, createdAsset, 12);

        List<Asset> assets = createdAssetType.getAsset();
        assets.add(createdAsset);
        createdAssetType.setAsset(assets);
        assetTypeRepository.save(createdAssetType);

        List<AssetHistory> assetHistories = createdAsset.getAssetHistory();
        assetHistories.add(createdAssetHistory);
        createdAsset.setAssetHistory(assetHistories);
        assetRepository.save(createdAsset);

        Long assetHistoryWorkId = deleteAssetHistory(assetWork, createdAssetHistory);

        Work work = workRepository.findByWorkId(assetHistoryWorkId);
        Asset asset = assetRepository.findByWork(assetWork);
        displayWork(work);
        System.out.println("삭제 여부 : " + (asset.getAssetHistory().isEmpty()));
    }

    // 표준원가 테스트

    public StandardCost createStandardCost(Work work, String type, String item, String grade, long baseCost){
        StandardCost standardCost = new StandardCost();
        standardCost.setWork(work);
        standardCost.setType(type);
        standardCost.setItem(item);
        standardCost.setGrade(grade);
        standardCost.setBaseCost(baseCost);
        standardCost.setStatement("비고");
        standardCost.setDate(LocalDate.now());
        return standardCostRepository.save(standardCost);
    }

    public StandardCost updateStandardCost(StandardCost standardCost, String type, String item, String grade, long baseCost){
        standardCost.setType(type);
        standardCost.setItem(item);
        standardCost.setGrade(grade);
        standardCost.setBaseCost(baseCost);
        standardCost.setStatement("비고 수정");
        return standardCostRepository.save(standardCost);
    }

    public Long deleteStandardCost(Work work, StandardCost standardCost){
        if(standardCost != null){
            work.setDraftState(WorkState.DELETE);
            standardCostRepository.delete(standardCost);
            return workRepository.save(work).getWorkId();
        }
        else{
            throw new NullPointerException();
        }
    }

    public void displayStandardCost(StandardCost standardCost){
        System.out.println("-----");
        System.out.println("분류 : " + standardCost.getType());
        System.out.println("항목 : " + standardCost.getItem());
        System.out.println("등급 : " + standardCost.getGrade());
        System.out.println("단가 : " + standardCost.getBaseCost());
        System.out.println("적용일자 : " + standardCost.getDate());
        System.out.println("비고 : " + standardCost.getStatement());
        System.out.println("-----");
    }

    @DisplayName("표준원가 생성")
    @Test
    @Transactional
    public void createStandardCost(){
        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        StandardCost created = createStandardCost(work, "유형", "항목", "등급", 50000);

        StandardCost standardCost = standardCostRepository.findByWork(work);

        System.out.println("생성되었는지? " + ( standardCost != null));
        displayWork(standardCost.getWork());
        displayStandardCost(standardCost);
    }

    @DisplayName("표준원가 수정")
    @Test
    @Transactional
    public void updateStandardCost(){
        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work =  createWork(staff, workType);

        StandardCost created = createStandardCost(work, "유형", "항목", "등급", 50000);

        updateStandardCost(created, "유형 수정", "항목 수정", "등급 수정", 150000);

        work = workRepository.findByWorkId(work.getWorkId());
        StandardCost standardCost = standardCostRepository.findByWork(work);

        displayWork(work);
        displayStandardCost(standardCost);
    }

    @DisplayName("표준원가 승인&출력")
    @Test
    @Transactional
    public void allowStandardCost(){
        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        StandardCost created = createStandardCost(work, "유형", "항목", "등급", 50000);

        Long workId = approveWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        StandardCost standardCost = standardCostRepository.findByWork(work);

        displayWork(work);
        displayStandardCost(standardCost);
    }

    @DisplayName("표준원가 보류&출력")
    @Test
    @Transactional
    public void delayStandardCost(){
        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        StandardCost created = createStandardCost(work, "유형", "항목", "등급", 50000);

        Long workId = deferWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        StandardCost standardCost = standardCostRepository.findByWork(work);

        displayWork(work);
        displayStandardCost(standardCost);
    }

    @DisplayName("표준원가 거절&출력")
    @Test
    @Transactional
    public void rejectStandardCost() {
        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);
        Work work =  createWork(staff1, workType);

        StandardCost created = createStandardCost(work, "유형", "항목", "등급", 50000);

        Long workId = rejectWork(staff2, work);

        work = workRepository.findByWorkId(workId);
        StandardCost standardCost = standardCostRepository.findByWork(work);

        displayWork(work);
        displayStandardCost(standardCost);
    }
    @DisplayName("표준원가 삭제")
    @Test
    @Transactional
    public void deleteStandardCost() {
        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work work = createWork(staff, workType);

        StandardCost created = createStandardCost(work, "유형", "항목", "등급", 50000);
        displayStandardCost(created);

        work = workRepository.findByWorkId(deleteStandardCost(work, created));
        StandardCost standardCost = standardCostRepository.findByWork(work);
        displayWork(work);
        System.out.println("삭제 여부 : " + (standardCost == null));
    }

    // 실제원가 테스트

    // 원가중심점 테스트

    // 내부오더 테스트

    // 수익성분석 테스트
    public ProfitabilityAnalysis createProfitabilityAnalysis(Work work, IncomeStatement incomeStatement, long grossAsset, long ownerCapital){
        ProfitabilityAnalysis profitabilityAnalysis = new ProfitabilityAnalysis();
        profitabilityAnalysis.setWork(work);
        profitabilityAnalysis.setGrossAsset(grossAsset);
        profitabilityAnalysis.setOwnerCapital(ownerCapital);
        profitabilityAnalysis.setNetIncomeRate((double) incomeStatement.getNetIncome()/incomeStatement.getSales() * 100.0);
        profitabilityAnalysis.setGrossMarginRate((double) incomeStatement.getGrossSales()/incomeStatement.getSales() * 100.0);
        profitabilityAnalysis.setOperatingProfitRate((double) incomeStatement.getOperatingProfit()/incomeStatement.getSales() * 100.0);
        profitabilityAnalysis.setGrossCapitalRate((double) incomeStatement.getNetIncome()/profitabilityAnalysis.getGrossAsset() * 100.0);
        profitabilityAnalysis.setReturnOnEquity((double) incomeStatement.getNetIncome()/profitabilityAnalysis.getOwnerCapital() * 100.0);
        profitabilityAnalysis.setStartDate(LocalDate.of(2024, 1, 1));
        profitabilityAnalysis.setEndDate(LocalDate.of(2024, 6, 30));

        return profitabilityAnalysisRepository.save(profitabilityAnalysis);
    }
    public ProfitabilityAnalysis updateProfitabilityAnalysis(ProfitabilityAnalysis profitabilityAnalysis, IncomeStatement incomeStatement, long grossAsset, long ownerCapital){
        profitabilityAnalysis.setGrossAsset(grossAsset);
        profitabilityAnalysis.setOwnerCapital(ownerCapital);
        profitabilityAnalysis.setNetIncomeRate((double) incomeStatement.getNetIncome()/incomeStatement.getSales() * 100.0);
        profitabilityAnalysis.setGrossMarginRate((double) incomeStatement.getGrossSales()/incomeStatement.getSales() * 100.0);
        profitabilityAnalysis.setOperatingProfitRate((double) incomeStatement.getOperatingProfit()/incomeStatement.getSales() * 100.0);
        profitabilityAnalysis.setGrossCapitalRate((double) incomeStatement.getNetIncome()/profitabilityAnalysis.getGrossAsset() * 100.0);
        profitabilityAnalysis.setReturnOnEquity((double) incomeStatement.getNetIncome()/profitabilityAnalysis.getOwnerCapital() * 100.0);
        profitabilityAnalysis.setStartDate(LocalDate.of(2024, 1, 1));
        profitabilityAnalysis.setEndDate(LocalDate.of(2024, 6, 30));

        return profitabilityAnalysisRepository.save(profitabilityAnalysis);
    }

    public long deleteProfitabilityAnalysis(Work work, ProfitabilityAnalysis profitabilityAnalysis){
        if(profitabilityAnalysis != null){
            work.setDraftState(WorkState.DELETE);
            profitabilityAnalysisRepository.delete(profitabilityAnalysis);
            return workRepository.save(work).getWorkId();
        }
        else {
            throw new NullPointerException();
        }
    }

    public void displayProfitabilityAnalysis(ProfitabilityAnalysis profitabilityAnalysis){
        System.out.println("-----");
        System.out.println("결산시작일 : " + profitabilityAnalysis.getStartDate());
        System.out.println("결산종료일 : " + profitabilityAnalysis.getEndDate());
        System.out.println("-----");
        System.out.println("매출순이익률 : " + profitabilityAnalysis.getNetIncomeRate());
        System.out.println("매출총이익률 : " + profitabilityAnalysis.getGrossMarginRate());
        System.out.println("매출영업이익률 : " + profitabilityAnalysis.getOperatingProfitRate());
        System.out.println("총자산순이익률 : " + profitabilityAnalysis.getGrossCapitalRate());
        System.out.println("자기자본순이익률 : " + profitabilityAnalysis.getReturnOnEquity());
        System.out.println("-----");
    }

    @DisplayName("수익성 분석 생성")
    @Test
    @Transactional
    public void createProfitabilityAnalysis(){
        WorkType [] workType = new WorkType[] { workTypeRepository.findByWorkName("손익계산서"), workTypeRepository.findByWorkName("수익성 분석")};
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work ISwork =  createWork(staff, workType[0]);
        Work PAwork = createWork(staff, workType[1]);

        IncomeStatement incomeStatement = createIncomeStatement(ISwork, 1000000, 300000, 100000, 150000, 100000, 50000);
        ProfitabilityAnalysis created = createProfitabilityAnalysis(PAwork, incomeStatement, 10000000, 5000000);

        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(PAwork);

        System.out.println("생성되었는지? " + ( profitabilityAnalysis != null));
        displayWork(profitabilityAnalysis.getWork());
        displayProfitabilityAnalysis(profitabilityAnalysis);
    }

    @DisplayName("수익성 분석 수정")
    @Test
    @Transactional
    public void updateProfitabilityAnalysis(){
        WorkType [] workType = new WorkType[] { workTypeRepository.findByWorkName("손익계산서"), workTypeRepository.findByWorkName("수익성 분석")};
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work ISwork =  createWork(staff, workType[0]);
        Work PAwork = createWork(staff, workType[1]);

        IncomeStatement incomeStatement = createIncomeStatement(ISwork, 1000000, 300000, 100000, 150000, 100000, 50000);
        ProfitabilityAnalysis created = createProfitabilityAnalysis(PAwork, incomeStatement, 10000000, 5000000);

        ProfitabilityAnalysis profitabilityAnalysis = updateProfitabilityAnalysis(created, incomeStatement, 12000000, 6000000);

        displayWork(profitabilityAnalysis.getWork());
        displayProfitabilityAnalysis(profitabilityAnalysis);
    }

    @DisplayName("수익성 분석 승인&출력")
    @Test
    @Transactional
    public void allowProfitabilityAnalysis(){
        WorkType [] workType = new WorkType[] { workTypeRepository.findByWorkName("손익계산서"), workTypeRepository.findByWorkName("수익성 분석")};

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work ISwork =  createWork(staff1, workType[0]);
        Work PAwork = createWork(staff1, workType[1]);

        IncomeStatement incomeStatement = createIncomeStatement(ISwork, 1000000, 300000, 100000, 150000, 100000, 50000);
        ProfitabilityAnalysis created = createProfitabilityAnalysis(PAwork, incomeStatement, 10000000, 5000000);

        Long ISworkId = approveWork(staff2, ISwork);
        Long PAworkId = approveWork(staff2, PAwork);

        PAwork = workRepository.findByWorkId(PAworkId);
        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(PAwork);

        displayWork(ISwork);
        displayWork(PAwork);
    }

    @DisplayName("수익성 분석 보류&출력")
    @Test
    @Transactional
    public void delayProfitabilityAnalysis(){
        WorkType [] workType = new WorkType[] { workTypeRepository.findByWorkName("손익계산서"), workTypeRepository.findByWorkName("수익성 분석")};

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work ISwork =  createWork(staff1, workType[0]);
        Work PAwork = createWork(staff1, workType[1]);

        IncomeStatement incomeStatement = createIncomeStatement(ISwork, 1000000, 300000, 100000, 150000, 100000, 50000);
        ProfitabilityAnalysis created = createProfitabilityAnalysis(PAwork, incomeStatement, 10000000, 5000000);

        Long ISworkId = approveWork(staff2, ISwork);
        Long PAworkId = deferWork(staff2, PAwork);

        PAwork = workRepository.findByWorkId(PAworkId);
        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(PAwork);

        displayWork(ISwork);
        displayWork(PAwork);
    }

    @DisplayName("수익성 분석 거절&출력")
    @Test
    @Transactional
    public void rejectProfitabilityAnalysis() {
        WorkType [] workType = new WorkType[] { workTypeRepository.findByWorkName("손익계산서"), workTypeRepository.findByWorkName("수익성 분석")};

        Staff staff1 = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[2]);
        Staff staff2 = makeStaff("ascia44", "홍길순", Gender.FEMALE, staffInfo[0]);

        Work ISwork =  createWork(staff1, workType[0]);
        Work PAwork = createWork(staff1, workType[1]);

        IncomeStatement incomeStatement = createIncomeStatement(ISwork, 1000000, 300000, 100000, 150000, 100000, 50000);
        ProfitabilityAnalysis created = createProfitabilityAnalysis(PAwork, incomeStatement, 10000000, 5000000);

        Long ISworkId = approveWork(staff2, ISwork);
        Long PAworkId = rejectWork(staff2, PAwork);

        PAwork = workRepository.findByWorkId(PAworkId);
        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(PAwork);

        displayWork(ISwork);
        displayWork(PAwork);
    }
    @DisplayName("수익성 분석 삭제")
    @Test
    @Transactional
    public void deleteProfitabilityAnalysis() {
        WorkType [] workType = new WorkType[] { workTypeRepository.findByWorkName("손익계산서"), workTypeRepository.findByWorkName("수익성 분석")};
        Staff staff = makeStaff("ascia33", "홍길동", Gender.MALE, staffInfo[0]);
        Work ISwork =  createWork(staff, workType[0]);
        Work PAwork = createWork(staff, workType[1]);

        IncomeStatement incomeStatement = createIncomeStatement(ISwork, 1000000, 300000, 100000, 150000, 100000, 50000);
        ProfitabilityAnalysis created = createProfitabilityAnalysis(PAwork, incomeStatement, 10000000, 5000000);

        PAwork = workRepository.findByWorkId(deleteProfitabilityAnalysis(PAwork, created));
        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(PAwork);

        displayWork(PAwork);
        System.out.println("삭제 여부 : " + (profitabilityAnalysis == null));
    }

}