package com.company.www.controller.workController;

import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.dto.work.WorkDto;
import com.company.www.dto.work.account.*;
import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.account.*;
import com.company.www.entity.work.sales.Credit;
import com.company.www.entity.work.sales.Patron;
import com.company.www.repository.belong.DepartmentRepository;
import com.company.www.repository.belong.RoleRepository;
import com.company.www.repository.belong.SectionRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.account.*;
import com.company.www.repository.work.sales.CreditRepository;
import com.company.www.repository.work.sales.PatronRepository;
import com.company.www.service.staff.StaffInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AccountWorkController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    StaffInfoService staffInfoService;
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
    CostCenterRepository costCenterRepository;
    @Autowired
    InnerOrderRepository innerOrderRepository;
    @Autowired
    ProfitabilityAnalysisRepository profitabilityAnalysisRepository;

    @Autowired
    PatronRepository patronRepository;
    @Autowired
    CreditRepository creditRepository;


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

    // 717줄 : 재산목록 view가 안뜨는 현상
    @GetMapping(value="/account/work")
    public String accountWork(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        List<WorkType> workTypes = workTypeRepository.findAllByRole(roleRepository.findByRoleName("재무회계"));
        workTypes.removeAll(Converter.filter(workTypeRepository, 
                "재무회계부 업무", "거래", "자산", "부채", "자본", "재무제표", "손익계산서", "포괄손익계산서", "자본변동표", "현금흐름표",
                "예산 계획", "추가 예산 계획", "법인세", "근로소득세", "부가가치세", "채권 상환", "채무 변제", "고정자산이력",
                "표준원가", "실제원가", "원가중심점", "내부오더"
                )
        );
        model.addAttribute("headerTitle", "재무회계부 업무");
        model.addAttribute("backLink", "/");
        model.addAttribute("workType", workTypes);
        model.addAttribute("isWork", true);

        return "/work/work";
    }

    @GetMapping(value="/account/work/financial")
    public String financialIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

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

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/financial/transaction")
    public String allTransaction(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("거래");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-거래내역");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/financial/transaction/insert");
        model.addAttribute("linkName", "거래내역");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/transaction/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/transaction/insert")
    public String createTransactionPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "거래내역 추가");
        model.addAttribute("backLink", "/financial/transaction");
        model.addAttribute("link", "/financial/transaction/insert");
        model.addAttribute("isNew", true);

        return "/work/account/transaction";
    }

    @PostMapping(value="/financial/transaction/insert")
    public String createTransaction(TransactionDto transactionDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("거래");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if(transactionDto.getDebit() != transactionDto.getCredit()){
            attributes.addFlashAttribute("alertData", "차변과 대변이 일치하지 않습니다.");
            attributes.addFlashAttribute("transaction", transactionDto);
            return "redirect:/financial/transaction/insert";
        }

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Transaction transaction = new Transaction();
        transaction.setWork(work);
        transaction.setItem(transactionDto.getItem());
        transaction.setDate(transactionDto.getDate());
        transaction.setDebitType(transactionDto.getDebitType());
        transaction.setDebit(transactionDto.getDebit());
        transaction.setDebitStatement(transactionDto.getDebitStatement());
        transaction.setCreditType(transactionDto.getCreditType());
        transaction.setCredit(transactionDto.getCredit());
        transaction.setCreditStatement(transactionDto.getCreditStatement());
        transaction.setStatement(transactionDto.getStatement());
        transactionRepository.save(transaction);

        attributes.addFlashAttribute("alertData", "거래내역을 추가하였습니다.");
        return "redirect:/financial/transaction";
    }

    @GetMapping(value="/financial/transaction/workId={workId}")
    public String transaction(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Transaction transaction = transactionRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff draftStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "거래내역");
        model.addAttribute("work", work);
        model.addAttribute("transaction", transaction);
        model.addAttribute("backLink", "/financial/transaction");
        if(curStaff.getUserId().equals(draftStaff.getUserId())){
            model.addAttribute("link", "/financial/transaction/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/transaction/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/transaction";
    }
    @GetMapping(value="/financial/transaction/update/workId={workId}")
    public String updateTransactionPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Transaction transaction = transactionRepository.findByWork(work);

        model.addAttribute("headerTitle", "거래내역 수정");
        model.addAttribute("work", work);
        model.addAttribute("transaction", transaction);
        model.addAttribute("backLink", "/financial/transaction/workId=" + workId);
        model.addAttribute("link", "/financial/transaction/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/transaction";
    }
    @PostMapping(value="/financial/transaction/update/workId={workId}")
    public String updateTransaction(@PathVariable("workId") Long workId, TransactionDto transactionDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Transaction transaction = transactionRepository.findByWork(work);

        transaction.setItem(transactionDto.getItem());
        transaction.setDate(transactionDto.getDate());
        transaction.setDebitType(transactionDto.getDebitType());
        transaction.setDebit(transactionDto.getDebit());
        transaction.setDebitStatement(transactionDto.getDebitStatement());
        transaction.setCreditType(transactionDto.getCreditType());
        transaction.setCredit(transactionDto.getCredit());
        transaction.setCreditStatement(transactionDto.getCreditStatement());
        transaction.setStatement(transactionDto.getStatement());
        transactionRepository.save(transaction);
        attributes.addFlashAttribute("alertData", "거래내역을 수정하였습니다.");
        return "redirect:/financial/transaction/workId=" + workId;
    }

    @GetMapping(value="/financial/transaction/consider/workId={workId}")
    public String considerTransactionPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Transaction transaction = transactionRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("transaction", transaction);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/transaction/workId=" + workId);
        model.addAttribute("link", "/financial/transaction/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/transaction";
    }
    @PostMapping(value="/financial/transaction/consider/workId={workId}")
    public String considerTransaction(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/financial/transaction/workId=" + workId;
    }

    @GetMapping(value="/financial/asset")
    public String allAsset(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("자산");
        Page<Work> property = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-자산");
        model.addAttribute("paging", property);
        model.addAttribute("link", "/financial/asset/insert");
        model.addAttribute("linkName", "자산내역");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/asset/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/asset/insert")
    public String createAssetPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "거래내역 추가");
        model.addAttribute("backLink", "/financial/asset");
        model.addAttribute("link", "/financial/asset/insert");
        model.addAttribute("types", propertyType);
        model.addAttribute("detailedTypes", assetType);
        model.addAttribute("isNew", true);

        return "/work/account/property";
    }

    @PostMapping(value="/financial/asset/insert")
    public String createAsset(PropertyDto propertyDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("자산");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Property property = new Property();
        property.setWork(work);
        property.setName(propertyDto.getName());
        property.setType(propertyDto.getType());
        property.setDetailedType(propertyDto.getDetailedType());
        property.setAmount(propertyDto.getAmount());
        property.setStatement(propertyDto.getStatement());
        property.setClosingStart(propertyDto.getClosingStart());
        property.setClosingEnd(propertyDto.getClosingEnd());
        propertyRepository.save(property);

        attributes.addFlashAttribute("alertData", "자산을 추가하였습니다.");
        return "redirect:/financial/asset";
    }

    @GetMapping(value="/financial/asset/workId={workId}")
    public String asset(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "자산");
        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("backLink", "/financial/asset");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/asset/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/asset/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/property";
    }
    @GetMapping(value="/financial/asset/update/workId={workId}")
    public String updateAssetPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);

        model.addAttribute("headerTitle", "거래내역 수정");
        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("backLink", "/financial/asset/workId=" + workId);
        model.addAttribute("link", "/financial/asset/update/workId=" + workId);
        model.addAttribute("types", propertyType);
        model.addAttribute("detailedTypes", assetType);
        model.addAttribute("isUpdate", true);

        return "/work/account/property";
    }
    @PostMapping(value="/financial/asset/update/workId={workId}")
    public String updateAsset(@PathVariable("workId") Long workId, PropertyDto propertyDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);

        property.setName(propertyDto.getName());
        property.setType(propertyDto.getType());
        property.setDetailedType(propertyDto.getDetailedType());
        property.setAmount(propertyDto.getAmount());
        property.setStatement(propertyDto.getStatement());
        property.setClosingStart(propertyDto.getClosingStart());
        property.setClosingEnd(propertyDto.getClosingEnd());
        propertyRepository.save(property);
        
        attributes.addFlashAttribute("alertData", "자산내역을 수정하였습니다.");
        return "redirect:/financial/asset/workId=" + workId;
    }

    @GetMapping(value="/financial/asset/consider/workId={workId}")
    public String considerAssetPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/asset/workId=" + workId);
        model.addAttribute("link", "/financial/asset/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/property";
    }
    @PostMapping(value="/financial/asset/consider/workId={workId}")
    public String considerAsset(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        workRepository.save(work);

        return "redirect:/financial/asset/workId=" + workId;
    }
    
    @GetMapping(value="/financial/liability")
    public String allLiability(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("부채");
        Page<Work> property = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-부채");
        model.addAttribute("paging", property);
        model.addAttribute("link", "/financial/liability/insert");
        model.addAttribute("linkName", "부채내역");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/liability/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/liability/insert")
    public String createLiabilityPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "부채내역 추가");
        model.addAttribute("backLink", "/financial/liability");
        model.addAttribute("link", "/financial/liability/insert");
        model.addAttribute("types", propertyType);
        model.addAttribute("detailedTypes", liabilityType);
        model.addAttribute("isNew", true);

        return "/work/account/property";
    }

    @PostMapping(value="/financial/liability/insert")
    public String createLiability(PropertyDto propertyDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("부채");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Property property = new Property();
        property.setWork(work);
        property.setName(propertyDto.getName());
        property.setType(propertyDto.getType());
        property.setDetailedType(propertyDto.getDetailedType());
        property.setAmount(propertyDto.getAmount());
        property.setStatement(propertyDto.getStatement());
        property.setClosingStart(propertyDto.getClosingStart());
        property.setClosingEnd(propertyDto.getClosingEnd());
        propertyRepository.save(property);

        attributes.addFlashAttribute("alertData", "부채내역을 추가하였습니다.");
        return "redirect:/financial/liability";
    }

    @GetMapping(value="/financial/liability/workId={workId}")
    public String liability(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "부채내역");
        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("backLink", "/financial/liability");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/liability/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/liability/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/property";
    }
    @GetMapping(value="/financial/liability/update/workId={workId}")
    public String updateLiabilityPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);

        model.addAttribute("headerTitle", "부채내역 수정");
        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("backLink", "/financial/liability/workId=" + workId);
        model.addAttribute("link", "/financial/liability/update/workId=" + workId);
        model.addAttribute("types", propertyType);
        model.addAttribute("detailedTypes", liabilityType);
        model.addAttribute("isUpdate", true);

        return "/work/account/property";
    }
    @PostMapping(value="/financial/liability/update/workId={workId}")
    public String updateLiability(@PathVariable("workId") Long workId, PropertyDto propertyDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);

        property.setName(propertyDto.getName());
        property.setType(propertyDto.getType());
        property.setDetailedType(propertyDto.getDetailedType());
        property.setAmount(propertyDto.getAmount());
        property.setStatement(propertyDto.getStatement());
        property.setClosingStart(propertyDto.getClosingStart());
        property.setClosingEnd(propertyDto.getClosingEnd());
        propertyRepository.save(property);

        attributes.addFlashAttribute("alertData", "부채내역을 수정하였습니다.");
        return "redirect:/financial/liability/workId=" + workId;
    }

    @GetMapping(value="/financial/liability/consider/workId={workId}")
    public String considerLiabilityPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/liability/workId=" + workId);
        model.addAttribute("link", "/financial/liability/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/property";
    }
    @PostMapping(value="/financial/liability/consider/workId={workId}")
    public String considerLiability(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/financial/liability/workId=" + workId;
    }
    
    @GetMapping(value="/financial/capital")
    public String allCapital(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("자본");
        Page<Work> property = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-자본내역");
        model.addAttribute("paging", property);
        model.addAttribute("link", "/financial/capital/insert");
        model.addAttribute("linkName", "자본내역");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/capital/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/capital/insert")
    public String createCapitalPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "자본내역 추가");
        model.addAttribute("backLink", "/financial/capital");
        model.addAttribute("link", "/financial/capital/insert");
        model.addAttribute("types", propertyType);
        model.addAttribute("detailedTypes", capitalType);
        model.addAttribute("isNew", true);

        return "/work/account/property";
    }

    @PostMapping(value="/financial/capital/insert")
    public String createCapital(PropertyDto propertyDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("자본");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Property property = new Property();
        property.setWork(work);
        property.setName(propertyDto.getName());
        property.setType(propertyDto.getType());
        property.setDetailedType(propertyDto.getDetailedType());
        property.setAmount(propertyDto.getAmount());
        property.setStatement(propertyDto.getStatement());
        property.setClosingStart(propertyDto.getClosingStart());
        property.setClosingEnd(propertyDto.getClosingEnd());
        propertyRepository.save(property);

        attributes.addFlashAttribute("alertData", "자본내역을 추가하였습니다.");
        return "redirect:/financial/capital";
    }

    @GetMapping(value="/financial/capital/workId={workId}")
    public String capital(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "자본내역");
        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("backLink", "/financial/capital");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/capital/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/capital/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/property";
    }
    @GetMapping(value="/financial/capital/update/workId={workId}")
    public String updateCapitalPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);

        model.addAttribute("headerTitle", "자본내역 수정");
        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("backLink", "/financial/capital/workId=" + workId);
        model.addAttribute("link", "/financial/capital/update/workId=" + workId);
        model.addAttribute("types", propertyType);
        model.addAttribute("detailedTypes", capitalType);
        model.addAttribute("isUpdate", true);

        return "/work/account/property";
    }
    @PostMapping(value="/financial/capital/update/workId={workId}")
    public String updateCapital(@PathVariable("workId") Long workId, PropertyDto propertyDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);

        property.setName(propertyDto.getName());
        property.setType(propertyDto.getType());
        property.setDetailedType(propertyDto.getDetailedType());
        property.setAmount(propertyDto.getAmount());
        property.setStatement(propertyDto.getStatement());
        property.setClosingStart(propertyDto.getClosingStart());
        property.setClosingEnd(propertyDto.getClosingEnd());
        propertyRepository.save(property);

        attributes.addFlashAttribute("alertData", "자본내역을 수정하였습니다.");
        return "redirect:/financial/capital/workId=" + workId;
    }

    @GetMapping(value="/financial/capital/consider/workId={workId}")
    public String considerCapitalPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Property property = propertyRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("property", property);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/capital/workId=" + workId);
        model.addAttribute("link", "/financial/capital/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/property";
    }
    @PostMapping(value="/financial/capital/consider/workId={workId}")
    public String considerCapital(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);
        
        return "redirect:/financial/capital/workId=" + workId;
    }

    @GetMapping(value="/financial/list")
    public String searchPropertyPage(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        model.addAttribute("headerTitle", "재무회계부-재산목록 검색");
        model.addAttribute("searchLink", "/financial/list");
        model.addAttribute("backLink", "/account/work/financial");
        return "/work/work_property_search";
    }
    @PostMapping(value="/financial/list")
    public String searchProperty(@RequestParam("start") LocalDate start, @RequestParam("end") LocalDate end, @RequestParam("type") String type, RedirectAttributes attributes){
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        List<Property> properties = propertyRepository.findAllByClosingStartAndClosingEndAndType(start, end, type);
        if(!properties.isEmpty()){
            long total = 0;
            for(Property property : properties){
                total += property.getAmount();
            }
            attributes.addFlashAttribute("properties", properties);
            attributes.addFlashAttribute("total", total);
        }
        else{
            attributes.addFlashAttribute("alertData", "해당되는 재산이 없습니다.");
            return "redirect:/financial/list";
        }
        String t;
        switch(type){
            case "자산" -> t = "asset";
            case "부채" -> t = "liability";
            case "자본" -> t = "capital";
            default -> t = null;
        }
        return "redirect:/financial/list/start=" + start + "&end=" + end + "&type=" + t;
    }
    @GetMapping(value="/financial/list/start={start}&end={end}&type={type}")
    public String propertiesList(@PathVariable("start") LocalDate start, @PathVariable("end") LocalDate end, Model model){
        model.addAttribute("headerTitle", "재산 목록");
        model.addAttribute("closingStart", start);
        model.addAttribute("closingEnd", end);
        return "/work/account/property_list";
    }

    @GetMapping(value="/financial/balance_sheet")
    public String allBalanceSheet(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-거래내역");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/financial/balance_sheet/insert");
        model.addAttribute("linkName", "거래내역");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/balance_sheet/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/balance_sheet/insert")
    public String createBalanceSheetPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "재무제표 추가");
        model.addAttribute("backLink", "/financial/balance_sheet");
        model.addAttribute("link", "/financial/balance_sheet/insert");
        model.addAttribute("isNew", true);

        return "/work/account/balance_sheet";
    }

    @PostMapping(value="/financial/balance_sheet/insert")
    public String createBalanceSheet(BalanceSheetDto balanceSheetDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("재무제표");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setWork(work);
        balanceSheet.setName(balanceSheetDto.getName());
        LocalDate bsDate = balanceSheetDto.getDate();
        bsDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        balanceSheet.setDate(bsDate);

        balanceSheet.setCash(balanceSheetDto.getCash());
        balanceSheet.setSecurities(balanceSheetDto.getSecurities());
        balanceSheet.setReceivable(balanceSheetDto.getReceivable());
        balanceSheet.setReceivableAccount(balanceSheetDto.getReceivableAccount());
        balanceSheet.setOtherLiquidAsset(balanceSheetDto.getOtherLiquidAsset());

        balanceSheet.setLongTermDeposit(balanceSheetDto.getLongTermDeposit());
        balanceSheet.setStock(balanceSheetDto.getStock());
        balanceSheet.setProperty(balanceSheetDto.getProperty());

        balanceSheet.setGround(balanceSheetDto.getGround());
        balanceSheet.setBuilding(balanceSheetDto.getBuilding());
        balanceSheet.setMachine(balanceSheetDto.getMachine());
        balanceSheet.setEquipment(balanceSheetDto.getEquipment());

        balanceSheet.setProduct(balanceSheetDto.getProduct());
        balanceSheet.setOtherFixedAsset(balanceSheetDto.getOtherFixedAsset());

        balanceSheet.setShortTermBorrowing(balanceSheetDto.getShortTermBorrowing());
        balanceSheet.setPayable(balanceSheetDto.getPayable());
        balanceSheet.setPayableAccount(balanceSheetDto.getPayableAccount());
        balanceSheet.setAdvanceReceived(balanceSheetDto.getAdvanceReceived());
        balanceSheet.setOtherLiquidLiability(balanceSheetDto.getOtherLiquidLiability());

        balanceSheet.setPrivateLoan(balanceSheetDto.getPrivateLoan());
        balanceSheet.setLongTermBorrowing(balanceSheetDto.getLongTermBorrowing());
        balanceSheet.setSeverancePay(balanceSheetDto.getSeverancePay());
        balanceSheet.setOtherFixedLiability(balanceSheetDto.getOtherFixedLiability());

        balanceSheet.setCapital(balanceSheetDto.getCapital());
        balanceSheet.setSurplus(balanceSheetDto.getSurplus());
        balanceSheet.setAdjustment(balanceSheetDto.getAdjustment());
        balanceSheet.setUnappropriated(balanceSheetDto.getUnappropriated());
        balanceSheet.setComprehensive(balanceSheetDto.getComprehensive());

        balanceSheetRepository.save(balanceSheet);

        attributes.addFlashAttribute("alertData", "재무제표를 추가하였습니다.");
        return "redirect:/financial/balance_sheet";
    }

    @GetMapping(value="/financial/balance_sheet/workId={workId}")
    public String balanceSheet(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        BalanceSheet balanceSheet = balanceSheetRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "재무제표");
        model.addAttribute("work", work);
        model.addAttribute("balanceSheet", balanceSheet);
        model.addAttribute("backLink", "/financial/balance_sheet");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/balance_sheet/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/balance_sheet/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/balance_sheet";
    }
    @GetMapping(value="/financial/balance_sheet/update/workId={workId}")
    public String updateBalanceSheetPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        BalanceSheet balanceSheet = balanceSheetRepository.findByWork(work);

        model.addAttribute("headerTitle", "재무제표 수정");
        model.addAttribute("work", work);
        model.addAttribute("balanceSheet", balanceSheet);
        model.addAttribute("backLink", "/financial/balance_sheet/workId=" + workId);
        model.addAttribute("link", "/financial/balance_sheet/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/balance_sheet";
    }
    @PostMapping(value="/financial/balance_sheet/update/workId={workId}")
    public String updateBalanceSheet(@PathVariable("workId") Long workId, BalanceSheetDto balanceSheetDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        BalanceSheet balanceSheet = balanceSheetRepository.findByWork(work);

        balanceSheet.setName(balanceSheetDto.getName());
        LocalDate bsDate = balanceSheetDto.getDate();
        bsDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        balanceSheet.setDate(bsDate);

        balanceSheet.setCash(balanceSheetDto.getCash());
        balanceSheet.setSecurities(balanceSheetDto.getSecurities());
        balanceSheet.setReceivable(balanceSheetDto.getReceivable());
        balanceSheet.setReceivableAccount(balanceSheetDto.getReceivableAccount());
        balanceSheet.setOtherLiquidAsset(balanceSheetDto.getOtherLiquidAsset());

        balanceSheet.setLongTermDeposit(balanceSheetDto.getLongTermDeposit());
        balanceSheet.setStock(balanceSheetDto.getStock());
        balanceSheet.setProperty(balanceSheetDto.getProperty());

        balanceSheet.setGround(balanceSheetDto.getGround());
        balanceSheet.setBuilding(balanceSheetDto.getBuilding());
        balanceSheet.setMachine(balanceSheetDto.getMachine());
        balanceSheet.setEquipment(balanceSheetDto.getEquipment());

        balanceSheet.setProduct(balanceSheetDto.getProduct());
        balanceSheet.setOtherFixedAsset(balanceSheetDto.getOtherFixedAsset());

        balanceSheet.setShortTermBorrowing(balanceSheetDto.getShortTermBorrowing());
        balanceSheet.setPayable(balanceSheetDto.getPayable());
        balanceSheet.setPayableAccount(balanceSheetDto.getPayableAccount());
        balanceSheet.setAdvanceReceived(balanceSheetDto.getAdvanceReceived());
        balanceSheet.setOtherLiquidLiability(balanceSheetDto.getOtherLiquidLiability());

        balanceSheet.setPrivateLoan(balanceSheetDto.getPrivateLoan());
        balanceSheet.setLongTermBorrowing(balanceSheetDto.getLongTermBorrowing());
        balanceSheet.setSeverancePay(balanceSheetDto.getSeverancePay());
        balanceSheet.setOtherFixedLiability(balanceSheetDto.getOtherFixedLiability());

        balanceSheet.setCapital(balanceSheetDto.getCapital());
        balanceSheet.setSurplus(balanceSheetDto.getSurplus());
        balanceSheet.setAdjustment(balanceSheetDto.getAdjustment());
        balanceSheet.setUnappropriated(balanceSheetDto.getUnappropriated());
        balanceSheet.setComprehensive(balanceSheetDto.getComprehensive());

        balanceSheetRepository.save(balanceSheet);
        attributes.addFlashAttribute("alertData", "재무제표를 수정하였습니다.");
        return "redirect:/financial/balance_sheet/workId=" + workId;
    }

    @GetMapping(value="/financial/balance_sheet/consider/workId={workId}")
    public String considerBalanceSheetPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        BalanceSheet balanceSheet = balanceSheetRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("balanceSheet", balanceSheet);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/balance_sheet/workId=" + workId);
        model.addAttribute("link", "/financial/balance_sheet/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/balance_sheet";
    }
    @PostMapping(value="/financial/balance_sheet/consider/workId={workId}")
    public String considerBalanceSheet(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/financial/balance_sheet/workId=" + workId;
    }

    @GetMapping(value="/financial/income")
    public String allIncome(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        Page<Work> income = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-손익계사서");
        model.addAttribute("paging", income);
        model.addAttribute("link", "/financial/income/insert");
        model.addAttribute("linkName", "손익계산서");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/income/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/income/insert")
    public String createIncomePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "손익계산서 추가");
        model.addAttribute("backLink", "/financial/income");
        model.addAttribute("link", "/financial/income/insert");
        model.addAttribute("isNew", true);

        return "/work/account/income_statement";
    }

    @PostMapping(value="/financial/income/insert")
    public String createIncome(IncomeStatementDto incomeStatementDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("손익계산서");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = incomeStatementDto.getStartDate();
        LocalDate end = incomeStatementDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        IncomeStatement incomeStatement = new IncomeStatement();
        incomeStatement.setWork(work);
        incomeStatement.setStartDate(start);
        incomeStatement.setEndDate(end);
        incomeStatement.setSales(incomeStatementDto.getSales());
        incomeStatement.setSalesCost(incomeStatementDto.getSalesCost());
        incomeStatement.setOperatingExpense(incomeStatementDto.getOperatingExpense());
        incomeStatement.setFinancialEarning(incomeStatementDto.getFinancialEarning());
        incomeStatement.setFinancialExpense(incomeStatementDto.getFinancialExpense());
        incomeStatement.setCorporateTax(incomeStatementDto.getCorporateTax());
        incomeStatementRepository.save(incomeStatement);

        attributes.addFlashAttribute("alertData", "손익계산서를 추가하였습니다.");
        return "redirect:/financial/income";
    }

    @GetMapping(value="/financial/income/workId={workId}")
    public String income(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        IncomeStatement incomeStatement = incomeStatementRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "손익계산서");
        model.addAttribute("work", work);
        model.addAttribute("incomeStatement", incomeStatement);
        model.addAttribute("backLink", "/financial/income");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/income/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/income/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/income_statement";
    }
    @GetMapping(value="/financial/income/update/workId={workId}")
    public String updateIncomePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        IncomeStatement incomeStatement = incomeStatementRepository.findByWork(work);

        model.addAttribute("headerTitle", "손익계산서 수정");
        model.addAttribute("work", work);
        model.addAttribute("incomeStatement", incomeStatement);
        model.addAttribute("backLink", "/financial/income/workId=" + workId);
        model.addAttribute("link", "/financial/income/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/income_statement";
    }
    @PostMapping(value="/financial/income/update/workId={workId}")
    public String updateIncome(@PathVariable("workId") Long workId, IncomeStatementDto incomeStatementDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        IncomeStatement incomeStatement = incomeStatementRepository.findByWork(work);

        LocalDate start = incomeStatementDto.getStartDate();
        LocalDate end = incomeStatementDto.getEndDate();
        incomeStatement.setStartDate(start);
        incomeStatement.setEndDate(end);
        incomeStatement.setSales(incomeStatementDto.getSales());
        incomeStatement.setSalesCost(incomeStatementDto.getSalesCost());
        incomeStatement.setOperatingExpense(incomeStatementDto.getOperatingExpense());
        incomeStatement.setFinancialEarning(incomeStatementDto.getFinancialEarning());
        incomeStatement.setFinancialExpense(incomeStatementDto.getFinancialExpense());
        incomeStatementRepository.save(incomeStatement);

        attributes.addFlashAttribute("alertData", "손익계산서를 수정하였습니다.");
        return "redirect:/financial/income/workId=" + workId;
    }

    @GetMapping(value="/financial/income/consider/workId={workId}")
    public String considerIncomePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        IncomeStatement incomeStatement = incomeStatementRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("incomeStatement", incomeStatement);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/income/workId=" + workId);
        model.addAttribute("link", "/financial/income/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/income_statement";
    }
    @PostMapping(value="/financial/income/consider/workId={workId}")
    public String considerIncome(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/financial/income/workId=" + workId;
    }

    @GetMapping(value="/financial/comprehensive_income")
    public String allComprehensiveIncome(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        Page<Work> comprehensiveIncome = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-포괄손익게산서");
        model.addAttribute("paging", comprehensiveIncome);
        model.addAttribute("link", "/financial/comprehensive_income/insert");
        model.addAttribute("linkName", "포괄손익계산서");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/comprehensive_income/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/comprehensive_income/insert")
    public String createComprehensiveIncomePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "포괄손익계산서 추가");
        model.addAttribute("backLink", "/financial/capital");
        model.addAttribute("link", "/financial/capital/insert");
        model.addAttribute("isNew", true);

        return "/work/account/comprehensive_income_statement";
    }

    @PostMapping(value="/financial/comprehensive_income/insert")
    public String createComprehensiveIncome(ComprehensiveIncomeStatementDto comprehensiveIncomeStatementDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("포괄손익계산서");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = comprehensiveIncomeStatementDto.getStartDate();
        LocalDate end = comprehensiveIncomeStatementDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        IncomeStatement incomeStatement = incomeStatementRepository.findByStartDateAndEndDate(start, end);

        ComprehensiveIncomeStatement comprehensiveIncomeStatement = new ComprehensiveIncomeStatement();
        comprehensiveIncomeStatement.setIncomeStatement(incomeStatement);
        comprehensiveIncomeStatement.setStartDate(start);
        comprehensiveIncomeStatement.setEndDate(end);
        comprehensiveIncomeStatement.setNetIncome(incomeStatement.getNetIncome());
        comprehensiveIncomeStatement.setOtherIncome(comprehensiveIncomeStatementDto.getOtherIncome());
        comprehensiveIncomeStatement.setTotalIncome(comprehensiveIncomeStatement.getNetIncome() + comprehensiveIncomeStatement.getOtherIncome());
        comprehensiveIncomeStatementRepository.save(comprehensiveIncomeStatement);

        attributes.addFlashAttribute("alertData", "포괄손익계산서를 추가하였습니다.");
        return "redirect:/financial/comprehensive_income";
    }

    @GetMapping(value="/financial/comprehensive_income/workId={workId}")
    public String comprehensiveIncome(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        ComprehensiveIncomeStatement comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "포괄손익계산서");
        model.addAttribute("work", work);
        model.addAttribute("comprehensiveIncomeStatement", comprehensiveIncomeStatement);
        model.addAttribute("backLink", "/financial/comprehensive_income");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/comprehensive_income/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/comprehensive_income/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/comprehensive_income_statement";
    }
    @GetMapping(value="/financial/comprehensive_income/update/workId={workId}")
    public String updateComprehensiveIncomePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        ComprehensiveIncomeStatement comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByWork(work);

        model.addAttribute("headerTitle", "포괄손익계산서 수정");
        model.addAttribute("work", work);
        model.addAttribute("comprehensiveIncomeStatement", comprehensiveIncomeStatement);
        model.addAttribute("backLink", "/financial/capital/workId=" + workId);
        model.addAttribute("link", "/financial/capital/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/comprehensive_income_statement";
    }
    @PostMapping(value="/financial/comprehensive_income/update/workId={workId}")
    public String updateComprehensiveIncome(@PathVariable("workId") Long workId, ComprehensiveIncomeStatementDto comprehensiveIncomeStatementDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        ComprehensiveIncomeStatement comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByWork(work);

        LocalDate start = comprehensiveIncomeStatementDto.getStartDate();
        LocalDate end = comprehensiveIncomeStatementDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        IncomeStatement incomeStatement = incomeStatementRepository.findByStartDateAndEndDate(start, end);
        if(incomeStatement != null){
            comprehensiveIncomeStatement.setIncomeStatement(incomeStatement);
            comprehensiveIncomeStatement.setNetIncome(incomeStatement.getNetIncome());
            comprehensiveIncomeStatement.setTotalIncome(incomeStatement.getNetIncome() + comprehensiveIncomeStatementDto.getOtherIncome());
        }
        else{
            attributes.addFlashAttribute("alertData", "손익계산서를 불러올 수 없습니다.");
            attributes.addFlashAttribute("comprehensiveIncomeStatement", comprehensiveIncomeStatementDto);
        }
        comprehensiveIncomeStatement.setStartDate(start);
        comprehensiveIncomeStatement.setEndDate(end);
        comprehensiveIncomeStatement.setOtherIncome(comprehensiveIncomeStatementDto.getOtherIncome());
        comprehensiveIncomeStatementRepository.save(comprehensiveIncomeStatement);

        attributes.addFlashAttribute("alertData", "포괄손익계산서를 수정하였습니다.");
        return "redirect:/financial/comprehensive_income/workId=" + workId;
    }

    @GetMapping(value="/financial/comprehensive_income/consider/workId={workId}")
    public String considerComprehensiveIncomePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        ComprehensiveIncomeStatement comprehensiveIncomeStatement = comprehensiveIncomeStatementRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("comprehensiveIncomeStatement", comprehensiveIncomeStatement);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/capital/workId=" + workId);
        model.addAttribute("link", "/financial/capital/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/comprehensive_income_statement";
    }
    @PostMapping(value="/financial/comprehensive_income/consider/workId={workId}")
    public String considerComprehensiveIncome(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/financial/comprehensive_income/workId=" + workId;
    }

    @GetMapping(value="/financial/equity_change")
    public String allEquityChange(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        Page<Work> equityChangeStatement = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-자본변동표");
        model.addAttribute("paging", equityChangeStatement);
        model.addAttribute("link", "/financial/equity_change/insert");
        model.addAttribute("linkName", "자본변동표");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/equity_change/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/equity_change/insert")
    public String createEquityChangePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "자본변동표 추가");
        model.addAttribute("backLink", "/financial/equity_change");
        model.addAttribute("link", "/financial/equity_change/insert");
        model.addAttribute("isNew", true);

        String [] tags = new String [] {
                "<td class='width-12'><input type='text' class='inner-width form-control' name='type' th:value='${equityChange?.equityChange?.type}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='capital' th:value='${equityChange?.equityChange?.capital}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='surplus' th:value='${equityChange?.equityChange?.surplus}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='adjustment' th:value='${equityChange?.equityChange?.adjustment}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='unappropriated' th:value='${equityChange?.equityChange?.unappropriated}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='comprehensive' th:value='${equityChange?.equityChange?.comprehensive}'></td>",
                "<td class='w-25'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 8);
        return "/work/account/equity_change_statement";
    }

    @PostMapping(value="/financial/equity_change/insert")
    public String createEquityChange(EquityChangeStatementDto equityChangeStatementDto, EquityChangeDto equityChangeDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("자본변동표");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = equityChangeStatementDto.getStartDate();
        LocalDate end = equityChangeStatementDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        EquityChangeStatement equityChangeStatement = new EquityChangeStatement();
        equityChangeStatement.setWork(work);
        equityChangeStatement.setStartDate(start);
        equityChangeStatement.setEndDate(end);
        equityChangeStatementRepository.save(equityChangeStatement);

        for(int i = 0; i < equityChangeDto.getType().length; i++){
            EquityChange equityChange = new EquityChange();
            equityChange.setType(equityChangeDto.getType()[i]);

            equityChange.setCapital(equityChangeDto.getCapital()[i]);
            equityChange.setSurplus(equityChangeDto.getSurplus()[i]);
            equityChange.setAdjustment(equityChangeDto.getAdjustment()[i]);
            equityChange.setUnappropriated(equityChangeDto.getUnappropriated()[i]);
            equityChange.setComprehensive(equityChangeDto.getComprehensive()[i]);

            long total = equityChangeDto.getCapital()[i] + equityChangeDto.getSurplus()[i] + equityChangeDto.getAdjustment()[i] +
                    equityChangeDto.getUnappropriated()[i] + equityChangeDto.getComprehensive()[i];
            equityChange.setTotal(total);
            equityChange.setEquityChangeStatement(equityChangeStatement);
            equityChangeRepository.save(equityChange);
        }

        attributes.addFlashAttribute("alertData", "자본변동표를 추가하였습니다.");
        return "redirect:/financial/equity_change";
    }

    @GetMapping(value="/financial/equity_change/workId={workId}")
    public String equityChange(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        EquityChangeStatement equityChangeStatement = equityChangeStatementRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "자본변동표");
        model.addAttribute("work", work);
        model.addAttribute("equityChangeStatement", equityChangeStatement);
        model.addAttribute("backLink", "/financial/equity_change");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/equity_change/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/equity_change/consider/workId=" + workId);
        }
        model.addAttribute("dataDeleteLink", ("/financial/equity_change/workId=" + workId + "/delete/"));
        model.addAttribute("isUpdatable", true);

        return "/work/account/equity_change_statement";
    }
    @GetMapping(value="/financial/equity_change/workId={workId}/delete/{equityChangeId}")
    public String deleteEquityChangeData(@PathVariable("workId") Long workId, @PathVariable("equityChangeId") Long equityChangeId,
                                         RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        EquityChangeStatement equityChangeStatement = equityChangeStatementRepository.findByWork(work);
        EquityChange equityChange = equityChangeRepository.findByEquityChangeId(equityChangeId);
        equityChangeStatement.getEquityChange().remove(equityChange);
        equityChangeRepository.delete(equityChange);
        equityChangeStatementRepository.save(equityChangeStatement);

        attributes.addFlashAttribute("alertData", "자본변동표의 요소를 삭제했습니다.");
        return "redirect:/financial/equity_change/workId=" + workId;
    }
    @GetMapping(value="/financial/equity_change/update/workId={workId}")
    public String updateEquityChangePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        EquityChangeStatement equityChangeStatement = equityChangeStatementRepository.findByWork(work);

        model.addAttribute("headerTitle", "자본변동표 수정");
        model.addAttribute("work", work);
        model.addAttribute("equityChangeStatement", equityChangeStatement);
        model.addAttribute("backLink", "/financial/equity_change/workId=" + workId);
        model.addAttribute("link", "/financial/equity_change/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        String [] tags = new String [] {
                "<td class='width-12'><input type='text' class='inner-width form-control' name='type' th:value='${equityChange?.equityChange?.type}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='capital' th:value='${equityChange?.equityChange?.capital}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='surplus' th:value='${equityChange?.equityChange?.surplus}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='adjustment' th:value='${equityChange?.equityChange?.adjustment}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='unappropriated' th:value='${equityChange?.equityChange?.unappropriated}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='comprehensive' th:value='${equityChange?.equityChange?.comprehensive}'></td>",
                "<td class='width-12'>-</td>",
                "<td class='width-12'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 8);

        return "/work/account/equity_change_statement";
    }
    @PostMapping(value="/financial/equity_change/update/workId={workId}")
    public String updateEquityChange(@PathVariable("workId") Long workId, EquityChangeStatementDto equityChangeStatementDto,
                                     EquityChangeDto equityChangeDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        EquityChangeStatement equityChangeStatement = equityChangeStatementRepository.findByWork(work);

        LocalDate start = equityChangeStatementDto.getStartDate();
        LocalDate end = equityChangeStatementDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        equityChangeStatement.setStartDate(start);
        equityChangeStatement.setEndDate(end);

        List<EquityChange> equityChanges = equityChangeStatement.getEquityChange();
        for(int i = 0; i < equityChanges.size(); i++){
            EquityChange equityChange = equityChanges.get(i);
            equityChange.setType(equityChangeDto.getType()[i]);

            equityChange.setCapital(equityChangeDto.getCapital()[i]);
            equityChange.setSurplus(equityChangeDto.getSurplus()[i]);
            equityChange.setAdjustment(equityChangeDto.getAdjustment()[i]);
            equityChange.setUnappropriated(equityChangeDto.getUnappropriated()[i]);
            equityChange.setComprehensive(equityChangeDto.getComprehensive()[i]);

            long total = equityChangeDto.getCapital()[i] + equityChangeDto.getSurplus()[i] + equityChangeDto.getAdjustment()[i] +
                    equityChangeDto.getUnappropriated()[i] + equityChangeDto.getComprehensive()[i];
            equityChange.setTotal(total);
            equityChange.setEquityChangeStatement(equityChangeStatement);
            equityChangeRepository.save(equityChange);
        }
        if(equityChanges.size() < equityChangeDto.getType().length){
            for(int i = equityChanges.size(); i < equityChangeDto.getType().length; i++){
                EquityChange equityChange = new EquityChange();

                equityChange.setType(equityChangeDto.getType()[i]);
                equityChange.setCapital(equityChangeDto.getCapital()[i]);
                equityChange.setSurplus(equityChangeDto.getSurplus()[i]);
                equityChange.setAdjustment(equityChangeDto.getAdjustment()[i]);
                equityChange.setUnappropriated(equityChangeDto.getUnappropriated()[i]);
                equityChange.setComprehensive(equityChangeDto.getComprehensive()[i]);

                long total = equityChangeDto.getCapital()[i] + equityChangeDto.getSurplus()[i] + equityChangeDto.getAdjustment()[i] +
                        equityChangeDto.getUnappropriated()[i] + equityChangeDto.getComprehensive()[i];
                equityChange.setTotal(total);
                equityChange.setEquityChangeStatement(equityChangeStatement);
                equityChangeRepository.save(equityChange);
                equityChanges.add(equityChange);
            }
        }
        equityChangeStatementRepository.save(equityChangeStatement);

        attributes.addFlashAttribute("alertData", "자본변동표를 수정하였습니다.");
        return "redirect:/financial/equity_change/workId=" + workId;
    }

    @GetMapping(value="/financial/equity_change/consider/workId={workId}")
    public String considerEquityChangePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        EquityChangeStatement equityChangeStatement = equityChangeStatementRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("equityChangeStatement", equityChangeStatement);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/equity_change/workId=" + workId);
        model.addAttribute("link", "/financial/equity_change/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/equity_change_statement";
    }
    @PostMapping(value="/financial/equity_change/consider/workId={workId}")
    public String considerEquityChange(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/financial/equity_change/workId=" + workId;
    }

    @GetMapping(value="/financial/cash_flow")
    public String allCashFlow(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        Page<Work> property = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-재무기록-현금흐름표");
        model.addAttribute("paging", property);
        model.addAttribute("link", "/financial/cash_flow/insert");
        model.addAttribute("linkName", "현금흐름표");
        model.addAttribute("backLink", "/account/work/financial");
        model.addAttribute("detailedWorkLink", "/financial/cash_flow/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/financial/cash_flow/insert")
    public String createCashFlowPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "현금흐름표 추가");
        model.addAttribute("backLink", "/financial/cash_flow");
        model.addAttribute("link", "/financial/cash_flow/insert");
        model.addAttribute("isNew", true);
        String [] tags = new String [] {
                """
                <td class='w-25'><select class='inner-width form-control' name='type'>
                    <option th:value='${영업활동}'>영업활동</option>
                    <option th:value='${투자활동}'>투자활동</option>
                    <option th:value='${재무활동}'>재무활동</option>
                </select></td>
                """,
                "<td class='w-25'><input type='text' class='inner-width form-control' name='detailedType' th:value='${cashFlow?.detailedType}'></td>",
                "<td class='w-25'><input type='text' class='inner-width form-control' name='amount' th:value='${cashFlow?.amount}'></td>",
                "<td class='w-25'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 4);

        return "/work/account/cash_flow";
    }

    @PostMapping(value="/financial/cash_flow/insert")
    public String createCashFlow(CashFlowDto cashFlowDto, CashFlowDataDto cashFlowDataDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("현금흐름표");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        CashFlowStatement cashFlowStatement = new CashFlowStatement();
        cashFlowStatement.setWork(work);
        cashFlowStatement.setStage(cashFlowDto.getStage());
        cashFlowStatement.setStatement(cashFlowDto.getStatement());
        LocalDate start = cashFlowDto.getStartDate();
        LocalDate end = cashFlowDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        cashFlowStatement.setStartDate(start);
        cashFlowStatement.setEndDate(end);
        cashFlowStatement.setCashFlow(new ArrayList<>());
        cashFlowStatementRepository.save(cashFlowStatement);

        for(int i = 0; i < cashFlowDataDto.getType().length; i++){
            CashFlow cashFlow = new CashFlow();
            cashFlow.setType(cashFlowDataDto.getType()[i]);
            cashFlow.setDetailedType(cashFlowDataDto.getDetailedType()[i]);
            cashFlow.setAmount(cashFlowDataDto.getAmount()[i]);
            cashFlow.setCashFlowStatement(cashFlowStatement);
            cashFlowRepository.save(cashFlow);
        }

        attributes.addFlashAttribute("alertData", "현금흐름표를 추가하였습니다.");
        return "redirect:/financial/cash_flow";
    }

    @GetMapping(value="/financial/cash_flow/workId={workId}")
    public String cashFlow(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        CashFlowStatement cashFlowStatement = cashFlowStatementRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "현금흐름표");
        model.addAttribute("work", work);
        model.addAttribute("cashFlow", cashFlowStatement);
        model.addAttribute("backLink", "/financial/cash_flow");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/financial/cash_flow/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/financial/cash_flow/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/cash_flow";
    }
    @GetMapping(value="/financial/cash_flow/update/workId={workId}")
    public String updateCashFlowPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        CashFlowStatement cashFlowStatement = cashFlowStatementRepository.findByWork(work);

        model.addAttribute("headerTitle", "현금흐름표 수정");
        model.addAttribute("work", work);
        model.addAttribute("cashFlow", cashFlowStatement);
        model.addAttribute("backLink", "/financial/cash_flow/workId=" + workId);
        model.addAttribute("link", "/financial/cash_flow/update/workId=" + workId);
        model.addAttribute("isUpdate", true);
        String [] tags = new String [] {
                """
                <td class='w-25'><select class='inner-width form-control' name='type' th:value='${cashFlow?.type}'>
                    <option th:value='${영업활동}' th:selected='${cashFlow.type}==${'영업활동'}'>영업활동</option>
                    <option th:value='${투자활동}' th:selected='${cashFlow.type}==${'투자활동'}'>투자활동</option>
                    <option th:value='${재무활동}' th:selected='${cashFlow.type}==${'재무활동'}'>재무활동</option>
                </select></td>
                """,
                "<td class='w-25'><input type='text' class='inner-width form-control' name='amount' th:value='${cashFlow?.detailedType}'></td>",
                "<td class='w-25'><input type='text' class='inner-width form-control' name='amount' th:value='${cashFlow?.amount}'></td>",
                "<td class='w-25'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 4);

        return "/work/account/cash_flow";
    }
    @PostMapping(value="/financial/cash_flow/update/workId={workId}")
    public String updateCashFlow(@PathVariable("workId") Long workId, CashFlowDto cashFlowDto, CashFlowDataDto cashFlowDataDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        CashFlowStatement cashFlowStatement = cashFlowStatementRepository.findByWork(work);

        cashFlowStatement.setStage(cashFlowDto.getStage());
        cashFlowStatement.setStatement(cashFlowDto.getStatement());
        LocalDate start = cashFlowDto.getStartDate();
        LocalDate end = cashFlowDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        cashFlowStatement.setStartDate(start);
        cashFlowStatement.setEndDate(end);
        cashFlowStatementRepository.save(cashFlowStatement);

        List<CashFlow> cashFlows = cashFlowStatement.getCashFlow();
        for(int i = 0; i < cashFlows.size(); i++){
            CashFlow cashFlow = cashFlows.get(i);
            cashFlow.setType(cashFlowDataDto.getType()[i]);
            cashFlow.setDetailedType(cashFlowDataDto.getDetailedType()[i]);
            cashFlow.setAmount(cashFlowDataDto.getAmount()[i]);
            cashFlow.setCashFlowStatement(cashFlowStatement);
            cashFlowRepository.save(cashFlow);
            cashFlows.add(cashFlow);
        }

        if(cashFlows.size() < cashFlowDataDto.getType().length){
            for(int i = cashFlows.size(); i < cashFlowDataDto.getType().length; i++){
                CashFlow cashFlow = new CashFlow();
                cashFlow.setType(cashFlowDataDto.getType()[i]);
                cashFlow.setDetailedType(cashFlowDataDto.getDetailedType()[i]);
                cashFlow.setAmount(cashFlowDataDto.getAmount()[i]);
                cashFlow.setCashFlowStatement(cashFlowStatement);
                cashFlowRepository.save(cashFlow);
                cashFlows.add(cashFlow);
            }
        }
        cashFlowStatementRepository.save(cashFlowStatement);
        attributes.addFlashAttribute("alertData", "현금흐름표를 수정하였습니다.");
        return "redirect:/financial/cash_flow/workId=" + workId;
    }

    @GetMapping(value="/financial/cash_flow/consider/workId={workId}")
    public String considerCashFlowPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        CashFlowStatement cashFlowStatement = cashFlowStatementRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("cashFlow", cashFlowStatement);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/financial/cash_flow/workId=" + workId);
        model.addAttribute("link", "/financial/cash_flow/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/cash_flow";
    }
    @PostMapping(value="/financial/cash_flow/consider/workId={workId}")
    public String considerCashFlow(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/financial/cash_flow/workId=" + workId;
    }

    @GetMapping(value="/account/work/budget")
    public String budgetIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("예산 배정", "/budget/assign");
        works.put("예산 계획", "/budget/plan");
        works.put("추가 예산", "/budget/additional");

        model.addAttribute("headerTitle", "재무회계부 업무-예산");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/budget/assign")
    public String allBudgetAssign(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("예산");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-예산-예산배정");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/budget/assign/insert");
        model.addAttribute("linkName", "예산배정");
        model.addAttribute("backLink", "/account/work/budget");
        model.addAttribute("detailedWorkLink", "/budget/assign/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/budget/assign/insert")
    public String createBudgetAssignPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "예산 배정");
        model.addAttribute("backLink", "/budget/assign");
        model.addAttribute("link", "/budget/assign/insert");
        model.addAttribute("isNew", true);

        return "/work/account/budget";
    }

    @PostMapping(value="/budget/assign/insert")
    public String createBudgetAssign(BudgetDto budgetDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("예산");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Department department = departmentRepository.findByDepartmentName(budgetDto.getDepartment());
        LocalDate start = budgetDto.getStartDate();
        LocalDate end = budgetDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Budget budget = new Budget();
        budget.setWork(work);
        budget.setDepartment(department);
        if(budgetDto.getSection() == null){
            budget.setSection(null);
        }
        else{
            Section section = sectionRepository.findBySectionName(budgetDto.getSection());
            budget.setSection(section);
        }
        budget.setInitial(budgetDto.getInitial());
        budget.setAdditional(0);
        budget.setBudgetPlan(new ArrayList<>());
        budget.setAdditionalBudget(new ArrayList<>());
        budget.setStartDate(start);
        budget.setEndDate(end);
        budgetRepository.save(budget);

        attributes.addFlashAttribute("alertData", "예산배정이 완료되였습니다.");
        return "redirect:/budget/assign";
    }

    @GetMapping(value="/budget/assign/workId={workId}")
    public String budgetAssign(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Budget budget = budgetRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "예산배정");
        model.addAttribute("work", work);
        model.addAttribute("budget", budget);
        model.addAttribute("backLink", "/budget/assign");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/budget/assign/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/budget/assign/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/budget";
    }
    @GetMapping(value="/budget/assign/update/workId={workId}")
    public String updateBudgetAssignPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Budget budget = budgetRepository.findByWork(work);

        model.addAttribute("headerTitle", "예산배정 수정");
        model.addAttribute("work", work);
        model.addAttribute("budget", budget);
        model.addAttribute("backLink", "/budget/assign/workId=" + workId);
        model.addAttribute("link", "/budget/assign/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/budget";
    }
    @PostMapping(value="/budget/assign/update/workId={workId}")
    public String updateBudgetAssign(@PathVariable("workId") Long workId, BudgetDto budgetDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Budget budget = budgetRepository.findByWork(work);

        LocalDate start = budgetDto.getStartDate();
        LocalDate end = budgetDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        budget.setInitial(budgetDto.getInitial());
        budget.setStartDate(start);
        budget.setEndDate(end);
        budgetRepository.save(budget);

        attributes.addFlashAttribute("alertData", "예산배정 내용을 수정하였습니다.");
        return "redirect:/budget/assign/workId=" + workId;
    }

    @GetMapping(value="/budget/assign/consider/workId={workId}")
    public String considerBudgetAssignPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Budget budget = budgetRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("budget", budget);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/budget/assign/workId=" + workId);
        model.addAttribute("link", "/budget/assign/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/transaction";
    }
    @PostMapping(value="/budget/assign/consider/workId={workId}")
    public String considerBudgetAssign(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/budget/assign/workId=" + workId;
    }

    @GetMapping(value="/budget/plan")
    public String allBudgetPlan(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("예산 계획");
        Page<Work> equityChangeStatement = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-예산-예산사용계획");
        model.addAttribute("paging", equityChangeStatement);
        model.addAttribute("link", "/budget/plan/insert");
        model.addAttribute("linkName", "예산사용계획");
        model.addAttribute("backLink", "/account/work/budget");
        model.addAttribute("detailedWorkLink", "/budget/plan/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/budget/plan/insert")
    public String createBudgetPlanPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "예산사용계획 추가");
        model.addAttribute("backLink", "/budget/plan");
        model.addAttribute("link", "/budget/plan/insert");
        model.addAttribute("isNew", true);

        String [] tags = new String [] {
                "<td class='width-20'><input type='text' class='inner-width form-control' name='type'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='item'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='required'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='statement'></td>",
                "<td class='width-20'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 5);
        return "/work/account/budget_plan";
    }

    @PostMapping(value="/budget/plan/insert")
    public String createBudgetPlan(BudgetPlanDto budgetPlanDto, BudgetItemDto budgetItemDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("예산 계획");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Staff budgetStaff = staffRepository.findByUserId(budgetPlanDto.getUserId());
        // 예산 초과 검사

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = budgetPlanDto.getStartDate();
        LocalDate end = budgetPlanDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        BudgetPlan budgetPlan = new BudgetPlan();
        budgetPlan.setWork(work);
        budgetPlan.setStaff(budgetStaff);
        budgetPlan.setDepartment(budgetStaff.getDepartment());
        if(budgetStaff.getSection() != null){
            budgetPlan.setSection(budgetStaff.getSection());
        }
        else{
            budgetPlan.setSection(null);
        }
        budgetPlan.setBudgetItem(new ArrayList<>());
        budgetPlan.setStartDate(start);
        budgetPlan.setEndDate(end);
        budgetPlanRepository.save(budgetPlan);

        for(int i = 0; i < budgetItemDto.getType().length; i++){
            BudgetItem budgetItem = new BudgetItem();
            budgetItem.setBudgetPlan(budgetPlan);
            budgetItem.setType(budgetItemDto.getType()[i]);
            budgetItem.setItem(budgetItemDto.getItem()[i]);
            budgetItem.setRequired(budgetItemDto.getRequired()[i]);
            budgetItem.setStatement(budgetItemDto.getStatement()[i]);
            budgetItemRepository.save(budgetItem);
        }

        attributes.addFlashAttribute("alertData", "예산사용계획을 추가하였습니다.");
        return "redirect:/budget/plan";
    }

    @GetMapping(value="/budget/plan/workId={workId}")
    public String budgetPlan(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        BudgetPlan budgetPlan = budgetPlanRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "자본변동표");
        model.addAttribute("work", work);
        model.addAttribute("budgetPlan", budgetPlan);
        model.addAttribute("backLink", "/budget/plan");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/budget/plane/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/budget/plan/consider/workId=" + workId);
        }
        model.addAttribute("dataDeleteLink", ("/budget/plan/workId=" + workId + "/delete/"));
        model.addAttribute("isUpdatable", true);

        return "/work/account/budget_plan";
    }
    @GetMapping(value="/budget/plan/workId={workId}/delete/{budgetItemId}")
    public String deleteBudgetPlanData(@PathVariable("workId") Long workId, @PathVariable("budgetItemId") Long budgetItemId,
                                         RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        BudgetPlan budgetPlan = budgetPlanRepository.findByWork(work);
        BudgetItem budgetItem = budgetItemRepository.findByBudgetItemId(budgetItemId);
        budgetPlan.getBudgetItem().remove(budgetItem);
        budgetItemRepository.delete(budgetItem);
        budgetPlanRepository.save(budgetPlan);

        attributes.addFlashAttribute("alertData", "예산사용계획의 요소를 삭제했습니다.");
        return "redirect:/budget/plan/workId=" + workId;
    }
    @GetMapping(value="/budget/plan/update/workId={workId}")
    public String updateBudgetPlanPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        BudgetPlan budgetPlan = budgetPlanRepository.findByWork(work);

        model.addAttribute("headerTitle", "예산사용계획 수정");
        model.addAttribute("work", work);
        model.addAttribute("budgetPlan", budgetPlan);
        model.addAttribute("backLink", "/budget/plan/workId=" + workId);
        model.addAttribute("link", "/budget/plan/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        String [] tags = new String [] {
                "<td class='width-20'><input type='text' class='inner-width form-control' name='type'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='item'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='required'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='statement'></td>",
                "<td class='width-20'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 5);

        return "/work/account/budget_plan";
    }
    @PostMapping(value="/budget/plan/update/workId={workId}")
    public String updateBudgetPlan(@PathVariable("workId") Long workId, BudgetPlanDto budgetPlanDto, BudgetItemDto budgetItemDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        BudgetPlan budgetPlan = budgetPlanRepository.findByWork(work);

        LocalDate start = budgetPlanDto.getStartDate();
        LocalDate end = budgetPlanDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        budgetPlan.setStartDate(start);
        budgetPlan.setEndDate(end);

        List<BudgetItem> budgetItems = budgetPlan.getBudgetItem();
        for(int i = 0; i < budgetItems.size(); i++){
            BudgetItem budgetItem = budgetItems.get(i);
            budgetItem.setBudgetPlan(budgetPlan);
            budgetItem.setType(budgetItemDto.getType()[i]);
            budgetItem.setItem(budgetItemDto.getItem()[i]);
            budgetItem.setRequired(budgetItemDto.getRequired()[i]);
            budgetItem.setStatement(budgetItemDto.getStatement()[i]);
            budgetItemRepository.save(budgetItem);
        }
        if(budgetItems.size() < budgetItemDto.getType().length){
            for(int i = budgetItems.size(); i < budgetItemDto.getType().length; i++){
                BudgetItem budgetItem = new BudgetItem();
                budgetItem.setBudgetPlan(budgetPlan);
                budgetItem.setType(budgetItemDto.getType()[i]);
                budgetItem.setItem(budgetItemDto.getItem()[i]);
                budgetItem.setRequired(budgetItemDto.getRequired()[i]);
                budgetItem.setStatement(budgetItemDto.getStatement()[i]);
                budgetItemRepository.save(budgetItem);
                budgetItems.add(budgetItem);
            }
        }
        budgetPlanRepository.save(budgetPlan);

        attributes.addFlashAttribute("alertData", "에산사용계획을 수정하였습니다.");
        return "redirect:/budget/plan/workId=" + workId;
    }

    @GetMapping(value="/budget/plan/consider/workId={workId}")
    public String considerBudgetPlanPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        BudgetPlan budgetPlan = budgetPlanRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("budgetPlan", budgetPlan);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/budget/plan/workId=" + workId);
        model.addAttribute("link", "/budget/plan/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/budget_plan";
    }
    @PostMapping(value="/budget/plan/consider/workId={workId}")
    public String considerBudgetPlan(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/budget/plan/workId=" + workId;
    }

    @GetMapping(value="/budget/additional")
    public String allBudgetAdditional(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("추가 예산 계획");
        Page<Work> equityChangeStatement = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-예산-추경예산");
        model.addAttribute("paging", equityChangeStatement);
        model.addAttribute("link", "/budget/additional/insert");
        model.addAttribute("linkName", "추경예산");
        model.addAttribute("backLink", "/account/work/budget");
        model.addAttribute("detailedWorkLink", "/budget/additional/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/budget/additional/insert")
    public String createBudgetAdditionalPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "추경예산 추가");
        model.addAttribute("backLink", "/budget/additional");
        model.addAttribute("link", "/budget/additional/insert");
        model.addAttribute("isNew", true);

        String [] tags = new String [] {
                "<td class='width-20'><input type='text' class='inner-width form-control' name='type'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='item'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='addition'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='reason'></td>",
                "<td class='width-20'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 5);
        return "/work/account/additional_budget";
    }

    @PostMapping(value="/budget/additional/insert")
    public String createBudgetAdditional(AdditionalBudgetDto additionalBudgetDto, AdditionalBudgetItemDto additionalBudgetItemDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("추가 예산 계획");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Staff budgetStaff = staffRepository.findByUserId(additionalBudgetDto.getUserId());

        AdditionalBudget additionalBudget = new AdditionalBudget();
        additionalBudget.setWork(work);
        additionalBudget.setDepartment(budgetStaff.getDepartment());
        if(budgetStaff.getSection() != null){
            additionalBudget.setSection(budgetStaff.getSection());
        }
        else{
            additionalBudget.setSection(null);
        }
        changeAllBudget(additionalBudget, additionalBudgetItemDto.getAddition());
        additionalBudgetRepository.save(additionalBudget);


        for(int i = 0; i < additionalBudgetItemDto.getType().length; i++){
            AdditionalBudgetItem additionalBudgetItem = new AdditionalBudgetItem();
            additionalBudgetItem.setItem(additionalBudgetItemDto.getItem()[i]);
            additionalBudgetItem.setType(additionalBudgetItemDto.getType()[i]);
            additionalBudgetItem.setAddition(additionalBudgetItemDto.getAddition()[i]);
            additionalBudgetItem.setReason(additionalBudgetItemDto.getReason()[i]);
            additionalBudgetItem.setAdditionalBudget(additionalBudget);
            additionalBudgetItemRepository.save(additionalBudgetItem);
        }

        attributes.addFlashAttribute("alertData", "추경예산을 추가하였습니다.");
        return "redirect:/budget/additional";
    }

    private void changeAllBudget(AdditionalBudget additionalBudget, long [] additional){
        changeDepartmentBudget(additionalBudget, additional);
        if(additionalBudget.getSection() != null){
            changeSectionBudget(additionalBudget, additional);
        }
    }

    private void changeDepartmentBudget(AdditionalBudget additionalBudget, long [] additional){
        Department department = additionalBudget.getDepartment();
        Budget budget = budgetRepository.findByDepartmentAndSection(department, null);
        List<AdditionalBudget> additionalBudgets = budget.getAdditionalBudget();

        changeBudget(budget, additionalBudget, additional);
    }
    private void changeSectionBudget(AdditionalBudget additionalBudget, long [] additional){
        Department department = additionalBudget.getDepartment();
        Section section = additionalBudget.getSection();
        Budget budget = budgetRepository.findByDepartmentAndSection(department, section);
        List<AdditionalBudget> additionalBudgets = budget.getAdditionalBudget();

        changeBudget(budget, additionalBudget, additional);
    }

    private void changeBudget(Budget budget, AdditionalBudget additionalBudget, long [] additional){
        List<AdditionalBudget> additionalBudgets = budget.getAdditionalBudget();

        additionalBudgets.add(additionalBudget);
        long sum = budget.getAdditional();
        for(long add : additional){
            sum += add;
        }
        budget.setAdditional(sum);
        budget.setAdditionalBudget(additionalBudgets);
        budgetRepository.save(budget);
    }

    @GetMapping(value="/budget/additional/workId={workId}")
    public String budgetAdditional(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        AdditionalBudget additionalBudget = additionalBudgetRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "추경예산");
        model.addAttribute("work", work);
        model.addAttribute("additionalBudget", additionalBudget);
        model.addAttribute("backLink", "/budget/additional");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/budget/additional/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/budget/additional/consider/workId=" + workId);
        }
        model.addAttribute("dataDeleteLink", ("/budget/additional/workId=" + workId + "/delete/"));
        model.addAttribute("isUpdatable", true);

        return "/work/account/additional_budget";
    }
    @GetMapping(value="/budget/additional/workId={workId}/delete/{additionalBudgetItemId}")
    public String deleteAdditionalBudgetData(@PathVariable("workId") Long workId, @PathVariable("additionalBudgetItemId") Long additionalBudgetItemId,
                                         RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        AdditionalBudget additionalBudget = additionalBudgetRepository.findByWork(work);
        AdditionalBudgetItem additionalBudgetItem = additionalBudgetItemRepository.findByAdditionalBudgetItemId(additionalBudgetItemId);
        additionalBudget.getAdditionalBudgetItem().remove(additionalBudgetItem);
        additionalBudgetItemRepository.delete(additionalBudgetItem);
        additionalBudgetRepository.save(additionalBudget);

        attributes.addFlashAttribute("alertData", "추경예산의 요소를 삭제했습니다.");
        return "redirect:/budget/additional/workId=" + workId;
    }
    @GetMapping(value="/budget/additional/update/workId={workId}")
    public String updateBudgetAdditionalPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        AdditionalBudget additionalBudget = additionalBudgetRepository.findByWork(work);

        model.addAttribute("headerTitle", "자본변동표 수정");
        model.addAttribute("work", work);
        model.addAttribute("additionalBudget", additionalBudget);
        model.addAttribute("backLink", "/financial/equity_change/workId=" + workId);
        model.addAttribute("link", "/financial/equity_change/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        String [] tags = new String [] {
                "<td class='width-20'><input type='text' class='inner-width form-control' name='type'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='item></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='addition'></td>",
                "<td class='width-20'><input type='text' class='inner-width form-control' name='reason'></td>",
                "<td class='width-20'><button type='button' class='btn btn-secondary inner-width' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        model.addAttribute("cellLength", 5);

        return "/work/account/additional_budget";
    }
    @PostMapping(value="/budget/additional/update/workId={workId}")
    public String updateBudgetAdditional(@PathVariable("workId") Long workId, AdditionalBudgetDto additionalBudgetDto,
                                         AdditionalBudgetItemDto additionalBudgetItemDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        AdditionalBudget additionalBudget = additionalBudgetRepository.findByWork(work);
        List<AdditionalBudgetItem> additionalBudgetItems = additionalBudget.getAdditionalBudgetItem();
        for(int i = 0; i < additionalBudgetItems.size(); i++){
            AdditionalBudgetItem additionalBudgetItem = additionalBudgetItems.get(i);
            additionalBudgetItem.setItem(additionalBudgetItemDto.getItem()[i]);
            additionalBudgetItem.setType(additionalBudgetItemDto.getType()[i]);
            additionalBudgetItem.setAddition(additionalBudgetItemDto.getAddition()[i]);
            additionalBudgetItem.setReason(additionalBudgetItemDto.getReason()[i]);
            additionalBudgetItem.setAdditionalBudget(additionalBudget);
            additionalBudgetItemRepository.save(additionalBudgetItem);
        }
        if(additionalBudgetItems.size() < additionalBudgetItemDto.getType().length){
            int correction = additionalBudgetItemDto.getType().length - additionalBudgetItems.size() + 1;
            long [] additional = new long[correction];
            for(int i = additionalBudgetItems.size(); i < additionalBudgetItemDto.getType().length; i++){
                AdditionalBudgetItem additionalBudgetItem = new AdditionalBudgetItem();
                additionalBudgetItem.setItem(additionalBudgetItemDto.getItem()[i]);
                additionalBudgetItem.setType(additionalBudgetItemDto.getType()[i]);
                additionalBudgetItem.setAddition(additionalBudgetItemDto.getAddition()[i]);
                additionalBudgetItem.setReason(additionalBudgetItemDto.getReason()[i]);
                additionalBudgetItem.setAdditionalBudget(additionalBudget);
                additionalBudgetItems.add(additionalBudgetItem);
                additionalBudgetItemRepository.save(additionalBudgetItem);
                additional[i - additionalBudgetItems.size()] = additionalBudgetItemDto.getAddition()[i];
            }
            changeAllBudget(additionalBudget, additional);
        }
        additionalBudgetRepository.save(additionalBudget);

        attributes.addFlashAttribute("alertData", "자본변동표를 수정하였습니다.");
        return "redirect:/financial/equity_change/workId=" + workId;
    }

    @GetMapping(value="/budget/additional/consider/workId={workId}")
    public String considerBudgetAdditionalPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        AdditionalBudget additionalBudget = additionalBudgetRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("additionalBudget", additionalBudget);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/budget/additional/workId=" + workId);
        model.addAttribute("link", "/budget/additional/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/additional_budget";
    }
    @PostMapping(value="/budget/additional/consider/workId={workId}")
    public String considerBudgetAdditional(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/budget/additional/workId=" + workId;
    }

    // -- 차후 구현 예정
    @GetMapping(value="/account/work/tax")
    public String taxIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("법인세", "/tax/corporate");
        works.put("가산세", "/tax/additional");
        works.put("근로소득세", "/tax/payroll");
        works.put("부가가치세", "/tax/value_added");

        model.addAttribute("headerTitle", "재무회계부 업무-세무");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/account/work/receivable")
    public String receivableIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("채권", "/receivable/all");
        works.put("채권 개별 상환 이력", "/receivable/history");
        works.put("채권 상환 이력", "/receivable/history_list");

        model.addAttribute("headerTitle", "재무회계부 업무-채권");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/receivable/all")
    public String allReceivable(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("채권");
        Page<Work> receivable = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-채권-채권");
        model.addAttribute("paging", receivable);
        model.addAttribute("link", "/receivable/all/insert");
        model.addAttribute("linkName", "채권 추가");
        model.addAttribute("backLink", "/account/work/receivable");
        model.addAttribute("detailedWorkLink", "/receivable/all/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/receivable/all/insert")
    public String createReceivablePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "채권 추가");
        model.addAttribute("backLink", "/receivable/all");
        model.addAttribute("link", "/receivable/all/insert");
        model.addAttribute("isNew", true);

        return "/work/account/debt";
    }

    @PostMapping(value="/receivable/all/insert")
    public String createReceivable(@RequestParam("prior") long prior, DebtDto debtDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("채권");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = debtDto.getStart();
        LocalDate expire = debtDto.getExpire();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        expire.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                debtDto.getName(), debtDto.getType(), debtDto.getLocation(), debtDto.getContact()
        );

        Debt debt = new Debt();
        debt.setWork(work);
        debt.setDebtType("채권");
        debt.setDebtHistory(new ArrayList<>());
        debt.setPatron(patron);
        debt.setStart(start);
        debt.setExpire(expire);
        debt.setItem(debtDto.getItem());
        debt.setType(debtDto.getType());
        debt.setTotal(debtDto.getTotal());
        debt.setStatement(debtDto.getStatement());
        debtRepository.save(debt);

        if(prior != 0){
            List<DebtHistory> history = debt.getDebtHistory();
            WorkType priorWorkType = workTypeRepository.findByWorkName("채권 상환");
            Work priorWork = new Work();
            priorWork.setWorkType(priorWorkType);
            priorWork.setDraftStaff(staff);
            priorWork.setDraftState(WorkState.PENDING);
            workRepository.save(priorWork);

            DebtHistory debtHistory = new DebtHistory();
            debtHistory.setWork(priorWork);
            debtHistory.setDebt(debt);
            debtHistory.setDate(start);
            debtHistory.setAmount(prior);
            debtHistory.setStatus("선수금");
            debtHistoryRepository.save(debtHistory);

            history.add(debtHistory);
            debt.setDebtHistory(history);
            debtRepository.save(debt);
        }
        Credit credit = patron.getCredit();
        List<Debt> debts = credit.getDebt();
        debts.add(debt);
        credit.setDebt(debts);
        creditRepository.save(credit);

        attributes.addFlashAttribute("alertData", "채권을 추가하였습니다.");
        return "redirect:/receivable/all";
    }

    @GetMapping(value="/receivable/all/workId={workId}")
    public String receivable(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        List<DebtHistory> history = debt.getDebtHistory();
        long rest = debt.getTotal();
        for(DebtHistory debtHistory : history){
            rest -= debtHistory.getAmount();
        }

        model.addAttribute("headerTitle", "채권");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("rest", rest);
        model.addAttribute("backLink", "/receivable/all");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/receivable/all/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/receivable/all/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/debt";
    }
    @GetMapping(value="/receivable/all/update/workId={workId}")
    public String updateReceivablePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);

        List<DebtHistory> history = debt.getDebtHistory();
        long rest = debt.getTotal();
        for(DebtHistory debtHistory : history){
            rest -= debtHistory.getAmount();
        }

        model.addAttribute("headerTitle", "채권 수정");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("rest", rest);
        model.addAttribute("backLink", "/receivable/all/workId=" + workId);
        model.addAttribute("link", "/receivable/all/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/debt";
    }
    @PostMapping(value="/receivable/all/update/workId={workId}")
    public String updateReceivable(@PathVariable("workId") Long workId, DebtDto debtDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);

        LocalDate start = debtDto.getStart();
        LocalDate expire = debtDto.getExpire();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        expire.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        debt.setStart(start);
        debt.setExpire(expire);
        debt.setItem(debtDto.getItem());
        debt.setType(debtDto.getType());
        debt.setTotal(debtDto.getTotal());
        debt.setStatement(debtDto.getStatement());
        debtRepository.save(debt);

        attributes.addFlashAttribute("alertData", "채권을 수정하였습니다.");
        return "redirect:/receivable/all/workId=" + workId;
    }

    @GetMapping(value="/receivable/all/consider/workId={workId}")
    public String considerReceivablePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        List<DebtHistory> history = debt.getDebtHistory();
        long rest = debt.getTotal();
        for(DebtHistory debtHistory : history){
            rest -= debtHistory.getAmount();
        }

        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("rest", rest);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/receivable/all/workId=" + workId);
        model.addAttribute("link", "/receivable/all/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/debt";
    }
    @PostMapping(value="/receivable/all/consider/workId={workId}")
    public String considerReceivable(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/receivable/all/workId=" + workId;
    }

    @GetMapping(value="/receivable/history")
    public String allReceivableHistory(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("채권 상환");
        Page<Work> receivable = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-채권-채권 상환");
        model.addAttribute("paging", receivable);
        model.addAttribute("link", "/receivable/history/insert");
        model.addAttribute("linkName", "채권 상환");
        model.addAttribute("backLink", "/account/work/receivable");
        model.addAttribute("detailedWorkLink", "/receivable/history/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/receivable/history/insert")
    public String createReceivableHistoryPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "채권 상환");
        model.addAttribute("backLink", "/receivable/history");
        model.addAttribute("link", "/receivable/history/insert");
        model.addAttribute("isNew", true);

        return "/work/account/debt_history";
    }

    @PostMapping(value="/receivable/history/insert")
    public String createReceivable(DebtHistoryDto debtHistoryDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("채권 상환");
        LocalDate draftDate = LocalDate.now();
        draftDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = debtHistoryDto.getStart();
        LocalDate date = debtHistoryDto.getDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                debtHistoryDto.getName(), debtHistoryDto.getPatronType(), debtHistoryDto.getLocation(), debtHistoryDto.getContact()
        );
        Debt debt = debtRepository.findByPatronAndStartAndItemAndTotal(patron, start, debtHistoryDto.getItem(), debtHistoryDto.getTotal());

        DebtHistory debtHistory = new DebtHistory();
        debtHistory.setWork(work);
        debtHistory.setDebt(debt);
        debtHistory.setDate(date);
        debtHistory.setAmount(debtHistoryDto.getAmount());
        debtHistory.setStatus(debtHistoryDto.getStatus());
        debtHistoryRepository.save(debtHistory);

        List<DebtHistory> history = debt.getDebtHistory();
        history.add(debtHistory);
        debt.setDebtHistory(history);
        debtRepository.save(debt);

        attributes.addFlashAttribute("alertData", "채권 상환내역을 추가하였습니다.");
        return "redirect:/receivable/history";
    }

    @GetMapping(value="/receivable/history/workId={workId}")
    public String receivableHistory(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);
        Debt debt = debtHistory.getDebt();
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "채권 상환내역");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("debtHistory", debtHistory);
        model.addAttribute("backLink", "/receivable/history");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/receivable/history/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/receivable/history/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/debt_history";
    }

    @GetMapping(value="/receivable/history/update/workId={workId}")
    public String updateReceivableHistoryPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);
        Debt debt = debtHistory.getDebt();

        model.addAttribute("headerTitle", "채권 상환내역 수정");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("debtHistory", debtHistory);
        model.addAttribute("backLink", "/receivable/history/workId=" + workId);
        model.addAttribute("link", "/receivable/history/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/debt_history";
    }
    @PostMapping(value="/receivable/history/update/workId={workId}")
    public String updateReceivableHistory(@PathVariable("workId") Long workId, DebtHistoryDto debtHistoryDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);

        LocalDate start = debtHistoryDto.getStart();
        LocalDate date = debtHistoryDto.getDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        debtHistory.setDate(date);
        debtHistory.setAmount(debtHistoryDto.getAmount());
        debtHistory.setStatus(debtHistoryDto.getStatus());
        debtHistoryRepository.save(debtHistory);

        attributes.addFlashAttribute("alertData", "채권 상환내역을 수정하였습니다.");
        return "redirect:/receivable/history/workId=" + workId;
    }

    @GetMapping(value="/receivable/history/consider/workId={workId}")
    public String considerReceivableHistoryPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);
        Debt debt = debtHistory.getDebt();
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("debtHistory", debtHistory);
        model.addAttribute("debt", debt);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/receivable/history/workId=" + workId);
        model.addAttribute("link", "/receivable/history/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/debt_history";
    }
    @PostMapping(value="/receivable/history/consider/workId={workId}")
    public String considerReceivableHistory(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/payable/history/workId=" + workId;
    }

    @GetMapping(value="/receivable/history_list")
    public String allReceivableHistoryList(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        WorkType workType = workTypeRepository.findByWorkName("채권");
        Page<Work> history = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-채권-채권 상환내역");
        model.addAttribute("paging", history);
        model.addAttribute("backLink", "/account/work/receivable");
        model.addAttribute("detailedWorkLink", "/receivable/history_list/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }
    @GetMapping(value="/receivable/history_list/workId={workId}")
    public String receivableHistoryList(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);

        model.addAttribute("headerTitle", "채권 상환내역");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("backLink", "/receivable/history");

        return "/work/account/debt_history_list";
    }

    @GetMapping(value="/account/work/payable")
    public String payableIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("채무", "/payable/all");
        works.put("채무 개별 변제 이력", "/payable/history");
        works.put("채무 변제 이력", "/payable/history_list");

        model.addAttribute("headerTitle", "재무회계부 업무-채무");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/payable/all")
    public String allPayable(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("채무");
        Page<Work> payable = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-채무-채무");
        model.addAttribute("paging", payable);
        model.addAttribute("link", "/payable/all/insert");
        model.addAttribute("linkName", "채무 추가");
        model.addAttribute("backLink", "/account/work/payable");
        model.addAttribute("detailedWorkLink", "/payable/all/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/payable/all/insert")
    public String createPayablePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "채무 추가");
        model.addAttribute("backLink", "/payable/all");
        model.addAttribute("link", "/payable/all/insert");
        model.addAttribute("isNew", true);

        return "/work/account/debt";
    }

    @PostMapping(value="/payable/all/insert")
    public String createPayable(@RequestParam("prior") long prior, DebtDto debtDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("채무");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = debtDto.getStart();
        LocalDate expire = debtDto.getExpire();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        expire.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                debtDto.getName(), debtDto.getType(), debtDto.getLocation(), debtDto.getContact()
        );

        Debt debt = new Debt();
        debt.setWork(work);
        debt.setDebtType("채무");
        debt.setDebtHistory(new ArrayList<>());
        debt.setPatron(patron);
        debt.setStart(start);
        debt.setExpire(expire);
        debt.setItem(debtDto.getItem());
        debt.setType(debtDto.getType());
        debt.setTotal(debtDto.getTotal());
        debt.setStatement(debtDto.getStatement());
        debtRepository.save(debt);

        if(prior != 0){
            List<DebtHistory> history = debt.getDebtHistory();
            WorkType priorWorkType = workTypeRepository.findByWorkName("채무 변제");
            Work priorWork = new Work();
            priorWork.setWorkType(priorWorkType);
            priorWork.setDraftStaff(staff);
            priorWork.setDraftState(WorkState.PENDING);
            workRepository.save(priorWork);

            DebtHistory debtHistory = new DebtHistory();
            debtHistory.setWork(priorWork);
            debtHistory.setDebt(debt);
            debtHistory.setDate(start);
            debtHistory.setAmount(prior);
            debtHistory.setStatus("선급금");
            debtHistoryRepository.save(debtHistory);

            history.add(debtHistory);
            debt.setDebtHistory(history);
            debtRepository.save(debt);
        }

        Credit credit = patron.getCredit();
        List<Debt> debts = credit.getDebt();
        debts.add(debt);
        credit.setDebt(debts);
        creditRepository.save(credit);

        attributes.addFlashAttribute("alertData", "채무를 추가하였습니다.");
        return "redirect:/payable/all";
    }

    @GetMapping(value="/payable/all/workId={workId}")
    public String payable(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        List<DebtHistory> history = debt.getDebtHistory();
        long rest = debt.getTotal();
        for(DebtHistory debtHistory : history){
            rest -= debtHistory.getAmount();
        }

        model.addAttribute("headerTitle", "채무");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("rest", rest);
        model.addAttribute("backLink", "/payable/all");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/payable/all/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/payable/all/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/debt";
    }
    @GetMapping(value="/payable/all/update/workId={workId}")
    public String updatePayablePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);

        List<DebtHistory> history = debt.getDebtHistory();
        long rest = debt.getTotal();
        for(DebtHistory debtHistory : history){
            rest -= debtHistory.getAmount();
        }

        model.addAttribute("headerTitle", "채무 수정");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("rest", rest);
        model.addAttribute("backLink", "/payable/all/workId=" + workId);
        model.addAttribute("link", "/payable/all/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/debt";
    }
    @PostMapping(value="/payable/all/update/workId={workId}")
    public String updatePayable(@PathVariable("workId") Long workId, DebtDto debtDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);

        LocalDate start = debtDto.getStart();
        LocalDate expire = debtDto.getExpire();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        expire.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        debt.setStart(start);
        debt.setExpire(expire);
        debt.setItem(debtDto.getItem());
        debt.setType(debtDto.getType());
        debt.setTotal(debtDto.getTotal());
        debt.setStatement(debtDto.getStatement());
        debtRepository.save(debt);

        attributes.addFlashAttribute("alertData", "채무를 수정하였습니다.");
        return "redirect:/payable/all/workId=" + workId;
    }

    @GetMapping(value="/payable/all/consider/workId={workId}")
    public String considerPayablePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        List<DebtHistory> history = debt.getDebtHistory();
        long rest = debt.getTotal();
        for(DebtHistory debtHistory : history){
            rest -= debtHistory.getAmount();
        }

        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("rest", rest);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/payable/all/workId=" + workId);
        model.addAttribute("link", "/payable/all/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/debt";
    }
    @PostMapping(value="/payable/all/consider/workId={workId}")
    public String considerPayable(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/payable/all/workId=" + workId;
    }

    @GetMapping(value="/payable/history")
    public String allPayableHistory(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("채무 변제");
        Page<Work> receivable = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-채무-채무 변제 내역");
        model.addAttribute("paging", receivable);
        model.addAttribute("link", "/payable/history/insert");
        model.addAttribute("linkName", "채무 변제");
        model.addAttribute("backLink", "/account/work/payable");
        model.addAttribute("detailedWorkLink", "/payable/history/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/payable/history/insert")
    public String createPayableHistoryPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "채무 변제 내역");
        model.addAttribute("backLink", "/payable/history");
        model.addAttribute("link", "/payable/history/insert");
        model.addAttribute("isNew", true);

        return "/work/account/debt_history";
    }

    @PostMapping(value="/payable/history/insert")
    public String createPayable(DebtHistoryDto debtHistoryDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("채무 변제");
        LocalDate requestDate = LocalDate.now();
        requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = debtHistoryDto.getStart();
        LocalDate date = debtHistoryDto.getDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                debtHistoryDto.getName(), debtHistoryDto.getPatronType(), debtHistoryDto.getLocation(), debtHistoryDto.getContact()
        );
        Debt debt = debtRepository.findByPatronAndStartAndItemAndTotal(patron, start, debtHistoryDto.getItem(), debtHistoryDto.getTotal());

        DebtHistory debtHistory = new DebtHistory();
        debtHistory.setWork(work);
        debtHistory.setDebt(debt);
        debtHistory.setDate(date);
        debtHistory.setAmount(debtHistoryDto.getAmount());
        debtHistory.setStatus(debtHistoryDto.getStatus());
        debtHistoryRepository.save(debtHistory);

        List<DebtHistory> history = debt.getDebtHistory();
        history.add(debtHistory);
        debt.setDebtHistory(history);
        debtRepository.save(debt);

        attributes.addFlashAttribute("alertData", "채무 변제내역을 추가하였습니다.");
        return "redirect:/payable/history";
    }

    @GetMapping(value="/payable/history/workId={workId}")
    public String payableHistory(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);
        Debt debt = debtHistory.getDebt();
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "채무 변제내역");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("debtHistory", debtHistory);
        model.addAttribute("backLink", "/payable/history");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/payable/history/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/payable/history/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/debt_history";
    }

    @GetMapping(value="/payable/history/update/workId={workId}")
    public String updatePayableHistoryPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);
        Debt debt = debtHistory.getDebt();

        model.addAttribute("headerTitle", "채권 변제내역 수정");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("debtHistory", debtHistory);
        model.addAttribute("backLink", "/payable/history/workId=" + workId);
        model.addAttribute("link", "/payable/history/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/debt_history";
    }
    @PostMapping(value="/payable/history/update/workId={workId}")
    public String updatePayableHistory(@PathVariable("workId") Long workId, DebtHistoryDto debtHistoryDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);

        LocalDate start = debtHistoryDto.getStart();
        LocalDate date = debtHistoryDto.getDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        debtHistory.setDate(date);
        debtHistory.setAmount(debtHistoryDto.getAmount());
        debtHistory.setStatus(debtHistoryDto.getStatus());
        debtHistoryRepository.save(debtHistory);

        attributes.addFlashAttribute("alertData", "채권 변제내역을 수정하였습니다.");
        return "redirect:/payable/history/workId=" + workId;
    }

    @GetMapping(value="/payable/history/consider/workId={workId}")
    public String considerPayableHistoryPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DebtHistory debtHistory = debtHistoryRepository.findByWork(work);
        Debt debt = debtHistory.getDebt();
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("debtHistory", debtHistory);
        model.addAttribute("debt", debt);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/payable/history/workId=" + workId);
        model.addAttribute("link", "/payable/history/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/debt_history";
    }
    @PostMapping(value="/payable/history/consider/workId={workId}")
    public String considerPayableHistory(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/payable/history/workId=" + workId;
    }

    @GetMapping(value="/payable/history_list")
    public String allPayableHistoryList(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        WorkType workType = workTypeRepository.findByWorkName("채무");
        Page<Work> history = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-채무-채무 변제내역");
        model.addAttribute("paging", history);
        model.addAttribute("backLink", "/account/work/payable");
        model.addAttribute("detailedWorkLink", "/payable/history_list/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }
    @GetMapping(value="/payable/history_list/workId={workId}")
    public String payableHistoryList(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Debt debt = debtRepository.findByWork(work);

        model.addAttribute("headerTitle", "채무 변제내역");
        model.addAttribute("work", work);
        model.addAttribute("debt", debt);
        model.addAttribute("backLink", "/payable/history");

        return "/work/account/debt_history_list";
    }

    /* 수정예정
    @GetMapping(value="/account/work/asset_management")
    public String assetManagementIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("자산대장", "/asset/all");
        works.put("개별 자산", "/asset");
        works.put("자산이력(개별)", "/asset/history");
        works.put("자산이력(종합)", "/asset/history_list");

        model.addAttribute("headerTitle", "재무회계부 업무-자산관리");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/asset/all")
    public String assetList(Model model){
        List<AssetType> assetTypes = assetRepository.findAll();
        model.addAttribute("assets", assetTypes);
        return "/work/account/asset_list";
    }
    @GetMapping(value="/asset")
    public String allAssetManagement(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("자산관리");
        Page<Work> payable = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-자산관리-자산관리");
        model.addAttribute("paging", payable);
        model.addAttribute("link", "/asset/insert");
        model.addAttribute("linkName", "자산관리 추가");
        model.addAttribute("backLink", "/account/work/asset");
        model.addAttribute("detailedWorkLink", "/fixed_asset/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/asset/insert")
    public String createAssetManagementPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("requestStaff", staff);
        model.addAttribute("headerTitle", "자산관리 추가");
        model.addAttribute("backLink", "/asset");
        model.addAttribute("link", "/asset/insert");
        model.addAttribute("isNew", true);

        return "/work/account/asset";
    }

    @PostMapping(value="/asset/insert")
    public String createAssetManagement(AssetTypeDto assetTypeDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("자산관리");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setRequestStaff(staff);
        work.setRequestDate(date);
        work.setState(State.REQUEST);
        workRepository.save(work);

        Department department = departmentRepository.findByDepartmentName(assetTypeDto.getDepartment());

        AssetType assetType = new AssetType();
        assetType.setWork(work);

        assetRepository.save(assetType);


        attributes.addFlashAttribute("alertData", "고정자산을 추가하였습니다.");
        return "redirect:/asset";
    }

    @GetMapping(value="/asset/workId={workId}")
    public String assetManagement(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        AssetType assetType = assetRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getRequestStaff();

        model.addAttribute("headerTitle", "자산관리");
        model.addAttribute("work", work);
        model.addAttribute("workState", Converter.convertToWorkApprovalStateString(work.getState()));
        model.addAttribute("asset", assetType);
        model.addAttribute("backLink", "/asset");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/asset/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/asset/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/asset";
    }
    @GetMapping(value="/asset/update/workId={workId}")
    public String updateAssetManagementPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        AssetType assetType = assetRepository.findByWork(work);

        model.addAttribute("headerTitle", "자산관리 수정");
        model.addAttribute("work", work);
        model.addAttribute("workState", Converter.convertToWorkApprovalStateString(work.getState()));
        model.addAttribute("asset", assetType);
        model.addAttribute("backLink", "/asset/workId=" + workId);
        model.addAttribute("link", "/asset/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/asset";
    }
    @PostMapping(value="/asset/update/workId={workId}")
    public String updateAsset(@PathVariable("workId") Long workId, AssetTypeDto assetTypeDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);

        AssetType assetType = assetRepository.findByWork(work);

        Department department = departmentRepository.findByDepartmentName(assetTypeDto.getDepartment());
        //수정
        assetRepository.save(assetType);

        attributes.addFlashAttribute("alertData",  "자산관리를 수정하였습니다.");
        return "redirect:/fixed_asset/workId=" + workId;
    }

    @GetMapping(value="/asset/consider/workId={workId}")
    public String considerFixedAssetPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        AssetType assetType = assetRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("asset", assetType);
        model.addAttribute("responseStaff", staff);
        model.addAttribute("backLink", "/asset/workId=" + workId);
        model.addAttribute("link", "/asset/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/asset";
    }
    @PostMapping(value="/asset/consider/workId={workId}")
    public String considerAssetManagement(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        work.setResponseStaff(staff);
        work.setResponseDate(date);
        work.setState(Converter.convertToWorkApprovalState(workDto.getState()));
        workRepository.save(work);

        return "redirect:/asset/workId=" + workId;
    }


    @GetMapping(value="/asset/history")
    public String allAssetHistory(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("고정자산이력");
        Page<Work> receivable = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-자산관리-개별 자산이력");
        model.addAttribute("paging", receivable);
        model.addAttribute("link", "/asset/history/insert");
        model.addAttribute("linkName", "고정자산이력 추가");
        model.addAttribute("backLink", "/account/work/asset");
        model.addAttribute("detailedWorkLink", "/asset/history/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/asset/history/insert")
    public String createAssetHistoryPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("requestStaff", staff);
        model.addAttribute("headerTitle", "자산이력 추가");
        model.addAttribute("backLink", "/asset/history");
        model.addAttribute("link", "/asset/history/insert");
        model.addAttribute("isNew", true);

        return "/work/account/asset_history";
    }

    @PostMapping(value="/asset/history/insert")
    public String createFixedAsset( Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("자산이력관리");
        LocalDate requestDate = LocalDate.now();
        requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setRequestStaff(staff);
        work.setRequestDate(requestDate);
        work.setState(State.REQUEST);
        workRepository.save(work);

        Department department = departmentRepository.findByDepartmentName(assetHistoryDto.getDepartment());
        Asset fixedAsset = AssetRepository.findByNameAndTypeAndCodeAndDepartment(
                assetHistoryDto.getName(),
                assetHistoryDto.getType(),
                assetHistoryDto.getCode(),
                department
        );

        AssetHistory assetHistory = new AssetHistory();

        assetHistoryRepository.save(assetHistory);


        attributes.addFlashAttribute("alertData", "자산이력을 추가하였습니다.");
        return "redirect:/asset/history";
    }

    @GetMapping(value="/asset/history/workId={workId}")
    public String assetHistory(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        AssetHistory assetHistory = assetHistoryRepository.findByWork(work);

        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getRequestStaff();

        model.addAttribute("headerTitle", "개별 자산이력");
        model.addAttribute("work", work);
        model.addAttribute("workState", Converter.convertToWorkApprovalStateString(work.getState()));
        model.addAttribute("assetHistory", assetHistory);
        model.addAttribute("backLink", "/asset/history");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/asset/history/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/asset/history/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/asset_history";
    }

    @GetMapping(value="/fixed_asset/history/update/workId={workId}")
    public String updateFixedAssetHistoryPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        AssetHistory assetHistory = assetHistoryRepository.findByWork(work);

        model.addAttribute("headerTitle", "자산이력 수정");
        model.addAttribute("work", work);
        model.addAttribute("workState", Converter.convertToWorkApprovalStateString(work.getState()));
        model.addAttribute("assetHistory", assetHistory);
        model.addAttribute("backLink", "/fixed_asset/history/workId=" + workId);
        model.addAttribute("link", "/fixed_asset/history/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/asset_history";
    }
    @PostMapping(value="/asset/history/update/workId={workId}")
    public String updateAssetHistory(@PathVariable("workId") Long workId, AssetHistoryDto assetHistoryDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        AssetHistory assetHistory = assetHistoryRepository.findByWork(work);

        // assetHistory 수정

        assetHistoryRepository.save(assetHistory);
        attributes.addFlashAttribute("alertData", "자산이력을 수정하였습니다.");
        return "redirect:/asset/history/workId=" + workId;
    }

    @GetMapping(value="/asset/history/consider/workId={workId}")
    public String considerAssetHistoryPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        AssetHistory assetHistory = assetHistoryRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("assetHistory", assetHistory);
        model.addAttribute("responseStaff", staff);
        model.addAttribute("backLink", "/asset/history/workId=" + workId);
        model.addAttribute("link", "/asset/history/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/asset_history";
    }
    @PostMapping(value="/asset/history/consider/workId={workId}")
    public String considerAssetHistory(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        work.setResponseStaff(staff);
        work.setResponseDate(date);
        work.setState(Converter.convertToWorkApprovalState(workDto.getState()));
        workRepository.save(work);

        return "redirect:/asset/history/workId=" + workId;
    }

    @GetMapping(value="/asset/history_list")
    public String allAssetHistoryList(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        WorkType workType = workTypeRepository.findByWorkName("자산관리");
        Page<Work> history = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-자산관리-자산관리이력");
        model.addAttribute("paging", history);
        model.addAttribute("backLink", "/account/work/asset");
        model.addAttribute("detailedWorkLink", "/asset/history_list/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }
    @GetMapping(value="/asset/history_list/workId={workId}")
    public String assetHistoryList(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        AssetType fixedAssetType = assetRepository.findByWork(work);

        model.addAttribute("headerTitle", "채무 변제내역");
        model.addAttribute("work", work);
        model.addAttribute("workState", Converter.convertToWorkApprovalStateString(work.getState()));
        model.addAttribute("fixedAsset", fixedAssetType);
        model.addAttribute("backLink", "/asset/history");

        return "/work/account/asset_history_list";
    }
    */

    @GetMapping(value="/account/work/cost")
    public String costIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("표준원가", "/cost/standard");
        works.put("실제원가", "/cost/actual");

        model.addAttribute("headerTitle", "재무회계부 업무-원가관리");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/cost/standard")
    public String allStandard(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        Page<Work> standardCost = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-원가관리-표준원가");
        model.addAttribute("paging", standardCost);
        model.addAttribute("link", "/cost/standard/insert");
        model.addAttribute("linkName", "표준원가");
        model.addAttribute("backLink", "/account/work/cost");
        model.addAttribute("detailedWorkLink", "/cost/standard/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/cost/standard/insert")
    public String createStandardPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "표준원가 추가");
        model.addAttribute("backLink", "/cost/standard");
        model.addAttribute("link", "/cost/standard/insert");
        model.addAttribute("isNew", true);

        return "/work/account/standard_cost";
    }

    @PostMapping(value="/cost/standard/insert")
    public String createStandard(StandardCostDto standardCostDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("표준원가");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate costDate = standardCostDto.getDate();
        costDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        StandardCost standardCost = new StandardCost();
        standardCost.setWork(work);
        standardCost.setItem(standardCostDto.getItem());
        standardCost.setType(standardCostDto.getType());
        standardCost.setGrade(standardCostDto.getGrade());
        standardCost.setBaseCost(standardCostDto.getBaseCost());
        standardCost.setStatement(standardCostDto.getStatement());
        standardCost.setDate(costDate);
        standardCostRepository.save(standardCost);

        attributes.addFlashAttribute("alertData", "표준원가를 추가하였습니다.");
        return "redirect:/cost/standard";
    }

    @GetMapping(value="/cost/standard/workId={workId}")
    public String standard(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        StandardCost standardCost = standardCostRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "표준원가");
        model.addAttribute("work", work);
        model.addAttribute("standardCost", standardCost);
        model.addAttribute("backLink", "/cost/standard");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/cost/standard/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/cost/standard/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/standard_cost";
    }
    @GetMapping(value="/cost/standard/update/workId={workId}")
    public String updateStandardPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        StandardCost standardCost = standardCostRepository.findByWork(work);

        model.addAttribute("headerTitle", "표준원가 수정");
        model.addAttribute("work", work);
        model.addAttribute("standardCost", standardCost);
        model.addAttribute("backLink", "/cost/standard/workId=" + workId);
        model.addAttribute("link", "/cost/standard/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/standard_cost";
    }
    @PostMapping(value="/cost/standard/update/workId={workId}")
    public String updateTransaction(@PathVariable("workId") Long workId, StandardCostDto standardCostDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        StandardCost standardCost = standardCostRepository.findByWork(work);

        LocalDate costDate = standardCostDto.getDate();
        costDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        standardCost.setItem(standardCostDto.getItem());
        standardCost.setType(standardCostDto.getType());
        standardCost.setGrade(standardCostDto.getGrade());
        standardCost.setBaseCost(standardCostDto.getBaseCost());
        standardCost.setStatement(standardCostDto.getStatement());
        standardCost.setDate(costDate);
        standardCostRepository.save(standardCost);

        attributes.addFlashAttribute("alertData", "표준원가를 수정하였습니다.");
        return "redirect:/cost/standard/workId=" + workId;
    }

    @GetMapping(value="/cost/standard/consider/workId={workId}")
    public String considerStandardPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        StandardCost standardCost = standardCostRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("standardCost", standardCost);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/cost/standard/workId=" + workId);
        model.addAttribute("link", "/cost/standard/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/transaction";
    }
    @PostMapping(value="/cost/standard/consider/workId={workId}")
    public String considerStandard(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/cost/standard/workId=" + workId;
    }

    @GetMapping(value="/cost/actual")
    public String allActual(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("실제원가");
        Page<Work> actualCost = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-원가관리-실제원가");
        model.addAttribute("paging", actualCost);
        model.addAttribute("link", "/cost/actual/insert");
        model.addAttribute("linkName", "실제원가");
        model.addAttribute("backLink", "/account/work/cost");
        model.addAttribute("detailedWorkLink", "/cost/actual/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/cost/actual/insert")
    public String createActualPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "실제원가 추가");
        model.addAttribute("backLink", "/cost/actual");
        model.addAttribute("link", "/cost/actual/insert");
        model.addAttribute("isNew", true);

        return "/work/account/actual_cost";
    }

    @PostMapping(value="/cost/actual/insert")
    public String createActual(ActualCostDto actualCostDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("실제원가");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate costDate = actualCostDto.getDate();
        LocalDate apply = actualCostDto.getApply();
        costDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        apply.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        StandardCost standardCost = standardCostRepository.findByTypeAndItemAndGradeAndDate(
                actualCostDto.getType(),
                actualCostDto.getItem(),
                actualCostDto.getGrade(),
                apply
        );


        ActualCost actualCost = new ActualCost();
        actualCost.setWork(work);

        actualCost.setActualCost(actualCostDto.getActualCost());
        actualCost.setDate(costDate);
        actualCost.setStatement(actualCostDto.getStatement());
        actualCostRepository.save(actualCost);

        attributes.addFlashAttribute("alertData", "실제원가를 추가하였습니다.");
        return "redirect:/cost/actual";
    }

    @GetMapping(value="/cost/actual/workId={workId}")
    public String actual(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        ActualCost actualCost = actualCostRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "실제원가");
        model.addAttribute("work", work);
        model.addAttribute("actualCost", actualCost);
        model.addAttribute("backLink", "/cost/actual");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/cost/actual/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/cost/actual/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/actual_cost";
    }
    @GetMapping(value="/cost/actual/update/workId={workId}")
    public String updateActualPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        ActualCost actualCost = actualCostRepository.findByWork(work);

        model.addAttribute("headerTitle", "실제원가 수정");
        model.addAttribute("work", work);
        model.addAttribute("actualCost", actualCost);
        model.addAttribute("backLink", "/cost/actual/workId=" + workId);
        model.addAttribute("link", "/cost/actual/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/actual_cost";
    }
    @PostMapping(value="/cost/actual/update/workId={workId}")
    public String updateActual(@PathVariable("workId") Long workId, ActualCostDto actualCostDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        ActualCost actualCost= actualCostRepository.findByWork(work);

        LocalDate costDate = actualCostDto.getDate();
        costDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        actualCost.setActualCost(actualCostDto.getActualCost());
        actualCost.setDate(costDate);
        actualCost.setStatement(actualCostDto.getStatement());
        actualCostRepository.save(actualCost);

        attributes.addFlashAttribute("alertData", "실제원가를 수정하였습니다.");
        return "redirect:/cost/actual/workId=" + workId;
    }

    @GetMapping(value="/cost/actual/consider/workId={workId}")
    public String considerActualPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        ActualCost actualCost = actualCostRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("actualCost", actualCost);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/cost/actual/workId=" + workId);
        model.addAttribute("link", "/cost/actual/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/actual_cost";
    }
    @PostMapping(value="/cost/actual/consider/workId={workId}")
    public String considerActual(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/cost/actual/workId=" + workId;
    }

    @GetMapping(value="/account/work/decision")
    public String decisionIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("원가중심점", "/decision/cost_center");
        works.put("내부오더", "/decision/inner_order");

        model.addAttribute("headerTitle", "재무회계부 업무-의사결정지원");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/decision/cost_center")
    public String allCostCenter(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("원가중심점");
        Page<Work> costCenter = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-의사결정지원-원가중심점");
        model.addAttribute("paging", costCenter);
        model.addAttribute("link", "/decision/cost_center/insert");
        model.addAttribute("linkName", "원가중심점");
        model.addAttribute("backLink", "/account/work/decision");
        model.addAttribute("detailedWorkLink", "/decision/cost_center/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/decision/cost_center/insert")
    public String createCostCenterPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "원가중심점 추가");
        model.addAttribute("backLink", "/decision/cost_center");
        model.addAttribute("link", "/decision/cost_center/insert");
        model.addAttribute("isNew", true);

        return "/work/account/cost_center";
    }

    @PostMapping(value="/decision/cost_center/insert")
    public String createCostCenter(CostCenterDto costCenterDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("원가중심점");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Department department = departmentRepository.findByDepartmentName(costCenterDto.getDepartment());
        Budget budget = budgetRepository.findByDepartmentAndSection(department, null);

        CostCenter costCenter = new CostCenter();
        costCenter.setWork(work);
        costCenter.setDepartment(department);
        costCenter.setBudget(budget);
        costCenter.setInitial(budget.getInitial());
        costCenter.setAdditional(budget.getAdditional());
        costCenter.setUsed(0); // 수정 예정
        costCenter.setOtherCost(0);
        costCenter.setInnerOrder(new ArrayList<>());
        costCenter.setPerformance(new ArrayList<>());
        costCenterRepository.save(costCenter);

        attributes.addFlashAttribute("alertData", "원가중심점을 추가하였습니다.");
        return "redirect:/decision/cost_center";
    }

    @GetMapping(value="/decision/cost_center/workId={workId}")
    public String costCenter(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        CostCenter costCenter = costCenterRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "원가중심점");
        model.addAttribute("work", work);
        model.addAttribute("costCenter", costCenter);
        model.addAttribute("backLink", "/decision/cost_center");

        return "/work/account/cost_center";
    }

    @GetMapping(value="/decision/inner_order")
    public String allInnerOrder(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("내부오더");
        Page<Work> innerOrder = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-의사결정지원-내부오더");
        model.addAttribute("paging", innerOrder);
        model.addAttribute("link", "/decision/inner_order/insert");
        model.addAttribute("linkName", "내부오더");
        model.addAttribute("backLink", "/account/work/decision");
        model.addAttribute("detailedWorkLink", "/decision/inner_order/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/decision/inner_order/insert")
    public String createInnerOrderPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "내부오더 추가");
        model.addAttribute("backLink", "/decision/inner_order");
        model.addAttribute("link", "/decision/inner_order/insert");
        model.addAttribute("isNew", true);

        return "/work/account/inner_order";
    }

    @PostMapping(value="/decision/inner_order/insert")
    public String createInnderOrder(InnerOrderDto innerOrderDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("내부오더");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Department department = departmentRepository.findByDepartmentName(innerOrderDto.getDepartment());
        LocalDate innerOrderDate = innerOrderDto.getDate();
        innerOrderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        CostCenter costCenter = costCenterRepository.findByDepartment(department);

        InnerOrder innerOrder = new InnerOrder();
        innerOrder.setWork(work);
        innerOrder.setDepartment(department);
        innerOrder.setCostCenter(costCenter);
        if(innerOrderDto.getSection() != null){
            Section section = sectionRepository.findBySectionName(innerOrderDto.getSection());
            innerOrder.setSection(section);
        }
        else{
            innerOrder.setSection(null);
        }
        innerOrder.setItem(innerOrderDto.getItem());
        innerOrder.setType(innerOrderDto.getType());
        innerOrder.setCost(innerOrderDto.getCost());
        innerOrder.setDate(innerOrderDate);
        innerOrderRepository.save(innerOrder);

        attributes.addFlashAttribute("alertData", "내부오더를 추가하였습니다.");
        return "redirect:/decision/inner_order";
    }

    @GetMapping(value="/decision/inner_order/workId={workId}")
    public String InnerOrder(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        InnerOrder innerOrder = innerOrderRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "내부오더");
        model.addAttribute("work", work);
        model.addAttribute("innerOrder", innerOrder);
        model.addAttribute("backLink", "/decision/inner_order");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/decision/inner_order/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/decision/inner_order/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/inner_order";
    }
    @GetMapping(value="/decision/inner_order/update/workId={workId}")
    public String updateInnerOrderPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        InnerOrder innerOrder = innerOrderRepository.findByWork(work);

        model.addAttribute("headerTitle", "내부오더 수정");
        model.addAttribute("work", work);
        model.addAttribute("innerOrder", innerOrder);
        model.addAttribute("backLink", "/decision/inner_order/workId=" + workId);
        model.addAttribute("link", "/decision/inner_order/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/inner_order";
    }
    @PostMapping(value="/decision/inner_order/update/workId={workId}")
    public String updateInnerOrder(@PathVariable("workId") Long workId, InnerOrderDto innerOrderDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        InnerOrder innerOrder = innerOrderRepository.findByWork(work);

        Department department = departmentRepository.findByDepartmentName(innerOrderDto.getDepartment());
        LocalDate innerOrderDate = innerOrderDto.getDate();
        innerOrderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        CostCenter costCenter = costCenterRepository.findByDepartment(department);

        innerOrder.setDepartment(department);
        innerOrder.setCostCenter(costCenter);
        if(innerOrderDto.getSection() != null){
            Section section = sectionRepository.findBySectionName(innerOrderDto.getSection());
            innerOrder.setSection(section);
        }
        else{
            innerOrder.setSection(null);
        }
        innerOrder.setItem(innerOrderDto.getItem());
        innerOrder.setType(innerOrderDto.getType());
        innerOrder.setCost(innerOrderDto.getCost());
        innerOrder.setDate(innerOrderDate);
        innerOrderRepository.save(innerOrder);

        attributes.addFlashAttribute("alertData", "내부오더를 수정하였습니다.");
        return "redirect:/decision/inner_order/workId=" + workId;
    }

    @GetMapping(value="/decision/inner_order/consider/workId={workId}")
    public String considerInnerOrderPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        InnerOrder innerOrder = innerOrderRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("innerOrder", innerOrder);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/decision/inner_order/workId=" + workId);
        model.addAttribute("link", "/decision/inner_order/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/inner_order";
    }
    @PostMapping(value="/decision/inner_order/consider/workId={workId}")
    public String considerInnerOrder(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/decision/inner_order/workId=" + workId;
    }

    @GetMapping(value="/account/work/profit")
    public String profitIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("수익성 분석", "/profit/analysis");

        model.addAttribute("headerTitle", "재무회계부 업무-수익성 분석");
        model.addAttribute("backLink", "/account/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/profit/analysis")
    public String allProfitAnalysis(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("수익성 분석");
        Page<Work> profitAnalysis = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "재무회계부 업무-의사결정지원-내부오더");
        model.addAttribute("paging", profitAnalysis);
        model.addAttribute("link", "/profit/analysis/insert");
        model.addAttribute("linkName", "내부오더");
        model.addAttribute("backLink", "/account/work/profit");
        model.addAttribute("detailedWorkLink", "/profit/analysis/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/profit/analysis/insert")
    public String createProfitAnalysisPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "수익성 분석 추가");
        model.addAttribute("backLink", "/profit/analysis");
        model.addAttribute("link", "/profit/analysis/insert");
        model.addAttribute("isNew", true);

        return "/work/account/profit_analysis";
    }

    @PostMapping(value="/profit/analysis/insert")
    public String createProfitAnalysis(ProfitabilityAnalysisDto profitabilityAnalysisDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("수익성 분석");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate start = profitabilityAnalysisDto.getStartDate();
        LocalDate end = profitabilityAnalysisDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        IncomeStatement incomeStatement = incomeStatementRepository.findByStartDateAndEndDate(start, end);

        ProfitabilityAnalysis profitabilityAnalysis = new ProfitabilityAnalysis();
        profitabilityAnalysis.setWork(work);
        profitabilityAnalysis.setGrossAsset(profitabilityAnalysisDto.getGrossAsset());
        profitabilityAnalysis.setOwnerCapital(profitabilityAnalysisDto.getOwnerCapital());
        profitabilityAnalysis.setNetIncomeRate((double) incomeStatement.getNetIncome()/incomeStatement.getSales());
        profitabilityAnalysis.setGrossMarginRate((double) incomeStatement.getGrossSales()/incomeStatement.getSales());
        profitabilityAnalysis.setOperatingProfitRate((double) incomeStatement.getOperatingProfit()/incomeStatement.getSales());
        profitabilityAnalysis.setGrossCapitalRate((double) incomeStatement.getNetIncome()/profitabilityAnalysisDto.getGrossAsset());
        profitabilityAnalysis.setReturnOnEquity((double) incomeStatement.getNetIncome()/profitabilityAnalysisDto.getOwnerCapital());
        profitabilityAnalysis.setStartDate(start);
        profitabilityAnalysis.setEndDate(end);
        profitabilityAnalysisRepository.save(profitabilityAnalysis);

        attributes.addFlashAttribute("alertData", "수익성 분석이 완료되었습니다.");
        return "redirect:/decision/profit_analysis";
    }

    @GetMapping(value="/profit/analysis/workId={workId}")
    public String profitAnalysis(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "내부오더");
        model.addAttribute("work", work);
        model.addAttribute("profitabilityAnalysis", profitabilityAnalysis);
        model.addAttribute("backLink", "/profit/analysis");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/profit/analysis/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/profit/analysis/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/account/profit_analysis";
    }
    @GetMapping(value="/profit/analysis/update/workId={workId}")
    public String updateProfitAnalysisPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        InnerOrder innerOrder = innerOrderRepository.findByWork(work);

        model.addAttribute("headerTitle", "수익성 분석 수정");
        model.addAttribute("work", work);
        model.addAttribute("innerOrder", innerOrder);
        model.addAttribute("backLink", "/profit/analysis/workId=" + workId);
        model.addAttribute("link", "/profit/analysis/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/account/profit_analysis";
    }
    @PostMapping(value="/profit/analysis/update/workId={workId}")
    public String updateProfitAnalysis(@PathVariable("workId") Long workId, ProfitabilityAnalysisDto profitabilityAnalysisDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(work);

        LocalDate start = profitabilityAnalysisDto.getStartDate();
        LocalDate end = profitabilityAnalysisDto.getEndDate();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        IncomeStatement incomeStatement = incomeStatementRepository.findByStartDateAndEndDate(start, end);

        profitabilityAnalysis.setGrossAsset(profitabilityAnalysisDto.getGrossAsset());
        profitabilityAnalysis.setNetIncomeRate((double) incomeStatement.getNetIncome()/incomeStatement.getSales());
        profitabilityAnalysis.setGrossMarginRate((double) incomeStatement.getGrossSales()/incomeStatement.getSales());
        profitabilityAnalysis.setOperatingProfitRate((double) incomeStatement.getOperatingProfit()/incomeStatement.getSales());
        profitabilityAnalysis.setGrossCapitalRate((double) incomeStatement.getNetIncome()/profitabilityAnalysisDto.getGrossAsset());
        profitabilityAnalysis.setReturnOnEquity((double) incomeStatement.getNetIncome()/profitabilityAnalysisDto.getOwnerCapital());
        profitabilityAnalysis.setStartDate(start);
        profitabilityAnalysis.setEndDate(end);
        profitabilityAnalysisRepository.save(profitabilityAnalysis);

        attributes.addFlashAttribute("alertData", "수익성 분석을 수정하였습니다.");
        return "redirect:/profit/analysis/workId=" + workId;
    }

    @GetMapping(value="/profit/analysis/consider/workId={workId}")
    public String considerProfitAnalysisPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        ProfitabilityAnalysis profitabilityAnalysis = profitabilityAnalysisRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("profitabilityAnalysis", profitabilityAnalysis);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/profit/analysis/workId=" + workId);
        model.addAttribute("link", "/profit/analysis/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/account/profit_analysis";
    }
    @PostMapping(value="/profit/analysis/consider/workId={workId}")
    public String considerProfitAnalysis(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/profit/analysis/workId=" + workId;
    }
}