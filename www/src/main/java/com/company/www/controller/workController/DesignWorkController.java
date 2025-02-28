package com.company.www.controller.workController;

import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.dto.work.WorkDto;
import com.company.www.dto.work.design.DesignDto;
import com.company.www.dto.work.design.ResourceDto;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.design.Design;
import com.company.www.entity.work.design.Resource;
import com.company.www.repository.belong.RoleRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.design.DesignRepository;
import com.company.www.repository.work.design.ResourceRepository;
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
import java.util.List;

@Controller
public class DesignWorkController {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    StaffInfoService staffInfoService;
    @Autowired
    WorkRepository workRepository;
    @Autowired
    WorkTypeRepository workTypeRepository;

    @Autowired
    DesignRepository designRepository;
    @Autowired
    ResourceRepository resourceRepository;

    @GetMapping(value="/design/work")
    public String designWork(Model model, Principal principal){
        model.addAttribute("headerTitle", "디자인부 업무");
        model.addAttribute("backLink", "/");
        model.addAttribute("isWork", true);

        staffInfoService.getInfo(model, principal);

        List<WorkType> workTypes = workTypeRepository.findAllByRole(roleRepository.findByRoleName("디자인"));
        workTypes.removeAll(Converter.filter(workTypeRepository, "디자인부 업무"));
        
        model.addAttribute("workType", workTypes);
        model.addAttribute("isWork", true);

        return "/work/work";
    }

    @GetMapping(value="/design/work/plan")
    public String allPlan(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("UI/UX 기획");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-UI/UX-기획");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/plan/insert");
        model.addAttribute("linkName", "기획 추가");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/plan/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/plan/insert")
    public String createPlanPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "기획 추가");
        model.addAttribute("backLink", "/design/work/plan");
        model.addAttribute("link", "/design/work/plan/insert");
        model.addAttribute("isNew", true);

        return "/work/design/design";
    }

    @PostMapping(value="/design/work/plan/insert")
    public String createPlan(DesignDto designDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("UI/UX 기획");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Design design = new Design();
        design.setWork(work);
        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "기획을 추가하였습니다.");
        return "redirect:/design/work/plan";
    }

    @GetMapping(value="/design/work/plan/workId={workId}")
    public String plan(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "기획");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/plan");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/plan/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/design/work/plan/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/design/design";
    }
    @GetMapping(value="/design/work/plan/update/workId={workId}")
    public String updatePlanPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        model.addAttribute("headerTitle", "기획 수정");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/plan/workId=" + workId);
        model.addAttribute("link", "/design/work/plan/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/plan/update/workId={workId}")
    public String updatePlan(@PathVariable("workId") Long workId, DesignDto designDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "기획을 수정하였습니다.");
        return "redirect:/design/work/plan/workId=" + workId;
    }

    @GetMapping(value="/design/work/plan/consider/workId={workId}")
    public String considerPlanPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design= designRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/design/work/plan/workId=" + workId);
        model.addAttribute("link", "/design/work/plan/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/plan/consider/workId={workId}")
    public String considerPlan(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        workRepository.save(work);

        return "redirect:/design/work/plan/workId=" + workId;
    }

    // UI/UX 분석 analysis
    @GetMapping(value="/design/work/analysis")
    public String allAnalysis(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("UI/UX 분석");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-UI/UX-분석");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/analysis/insert");
        model.addAttribute("linkName", "분석 추가");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/analysis/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/analysis/insert")
    public String createAnalysisPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "분석 추가");
        model.addAttribute("backLink", "/design/work/analysis");
        model.addAttribute("link", "/design/work/analysis/insert");
        model.addAttribute("isNew", true);

        return "/work/design/design";
    }

    @PostMapping(value="/design/work/analysis/insert")
    public String createAnalysis(DesignDto designDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("UI/UX 분석");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Design design = new Design();
        design.setWork(work);
        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "분석을 추가하였습니다.");
        return "redirect:/design/work/analysis";
    }

    @GetMapping(value="/design/work/analysis/workId={workId}")
    public String analysis(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "분석");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/analysis");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/analysis/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/design/work/analysis/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/design/design";
    }
    @GetMapping(value="/design/work/analysis/update/workId={workId}")
    public String updateAnalysisPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        model.addAttribute("headerTitle", "분석 수정");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/analysis/workId=" + workId);
        model.addAttribute("link", "/design/work/analysis/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/analysis/update/workId={workId}")
    public String updateAnalysis(@PathVariable("workId") Long workId, DesignDto designDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "분석을 수정하였습니다.");
        return "redirect:/design/work/analysis/workId=" + workId;
    }

    @GetMapping(value="/design/work/analysis/consider/workId={workId}")
    public String considerAnalysisPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design= designRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/design/work/analysis/workId=" + workId);
        model.addAttribute("link", "/design/work/analysis/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/analysis/consider/workId={workId}")
    public String considerAnalysis(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/design/work/analysis/workId=" + workId;
    }

    // UI/UX 설계 design
    @GetMapping(value="/design/work/design")
    public String allDesign(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("UI/UX 설계");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-UI/UX-설계");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/design/insert");
        model.addAttribute("linkName", "설계 추가");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/design/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/design/insert")
    public String createDesignPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "설계 추가");
        model.addAttribute("backLink", "/design/work/design");
        model.addAttribute("link", "/design/work/design/insert");
        model.addAttribute("isNew", true);

        return "/work/design/design";
    }

    @PostMapping(value="/design/work/design/insert")
    public String createDesign(DesignDto designDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("UI/UX 설계");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Design design = new Design();
        design.setWork(work);
        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "설계를 추가하였습니다.");
        return "redirect:/design/work/design";
    }

    @GetMapping(value="/design/work/design/workId={workId}")
    public String design(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "설계");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/design");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/design/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/design/work/design/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/design/design";
    }
    @GetMapping(value="/design/work/design/update/workId={workId}")
    public String updateDesignPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        model.addAttribute("headerTitle", "설계 수정");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/design/workId=" + workId);
        model.addAttribute("link", "/design/work/design/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/design/update/workId={workId}")
    public String updateDesign(@PathVariable("workId") Long workId, DesignDto designDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "설계를 수정하였습니다.");
        return "redirect:/design/work/design/workId=" + workId;
    }

    @GetMapping(value="/design/work/design/consider/workId={workId}")
    public String considerDesignPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design= designRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/design/work/design/workId=" + workId);
        model.addAttribute("link", "/design/work/design/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/design/consider/workId={workId}")
    public String considerDesign(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/design/work/design/workId=" + workId;
    }

    // UI/UX 작성 make
    @GetMapping(value="/design/work/make")
    public String allMake(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("UI/UX 작성");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-UI/UX-작성");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/make/insert");
        model.addAttribute("linkName", "UI/UX 추가");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/make/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/make/insert")
    public String createMakePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "UI/UX 추가");
        model.addAttribute("backLink", "/design/work/make");
        model.addAttribute("link", "/design/work/make/insert");
        model.addAttribute("isNew", true);

        return "/work/design/design";
    }

    @PostMapping(value="/design/work/make/insert")
    public String createMake(DesignDto designDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("UI/UX 작성");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Design design = new Design();
        design.setWork(work);
        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "UI/UX를 추가하였습니다.");
        return "redirect:/design/work/make";
    }

    @GetMapping(value="/design/work/make/workId={workId}")
    public String make(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "UI/UX");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/make");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/make/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/design/work/make/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/design/design";
    }
    @GetMapping(value="/design/work/make/update/workId={workId}")
    public String updateMakePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        model.addAttribute("headerTitle", "UI/UX 수정");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/make/workId=" + workId);
        model.addAttribute("link", "/design/work/make/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/make/update/workId={workId}")
    public String updateMake(@PathVariable("workId") Long workId, DesignDto designDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "UI/UX를 수정하였습니다.");
        return "redirect:/design/work/make/workId=" + workId;
    }

    @GetMapping(value="/design/work/make/consider/workId={workId}")
    public String considerMakePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design= designRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/design/work/make/workId=" + workId);
        model.addAttribute("link", "/design/work/make/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/make/consider/workId={workId}")
    public String considerMake(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/design/work/make/workId=" + workId;
    }

    // UI/UX 검증 test
    @GetMapping(value="/design/work/test")
    public String allTest(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("UI/UX 검증");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-UI/UX-검증");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/test/insert");
        model.addAttribute("linkName", "UI/UX 검증");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/test/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/test/insert")
    public String createTestPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "UI/UX 검증");
        model.addAttribute("backLink", "/design/work/test");
        model.addAttribute("link", "/design/work/test/insert");
        model.addAttribute("isNew", true);

        return "/work/design/design";
    }

    @PostMapping(value="/design/work/test/insert")
    public String createTest(DesignDto designDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("UI/UX 검증");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Design design = new Design();
        design.setWork(work);
        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "UI/UX 검증이 완료되었습니다.");
        return "redirect:/design/work/test";
    }

    @GetMapping(value="/design/work/test/workId={workId}")
    public String test(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "UI/UX 검증");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/test");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/test/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/design/work/test/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/design/design";
    }
    @GetMapping(value="/design/work/test/update/workId={workId}")
    public String updateTestPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        model.addAttribute("headerTitle", "UI/UX 검증 수정");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/test/workId=" + workId);
        model.addAttribute("link", "/design/work/test/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/test/update/workId={workId}")
    public String updateTest(@PathVariable("workId") Long workId, DesignDto designDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "UI/UX 검증을 수정하였습니다.");
        return "redirect:/design/work/test/workId=" + workId;
    }

    @GetMapping(value="/design/work/test/consider/workId={workId}")
    public String considerTestPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design= designRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/design/work/test/workId=" + workId);
        model.addAttribute("link", "/design/work/test/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/test/consider/workId={workId}")
    public String considerTest(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/design/work/test/workId=" + workId;
    }

    // UI/UX 결과물 output
    @GetMapping(value="/design/work/output")
    public String allOutput(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("UI/UX 결과물");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-UI/UX-결과물");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/output/insert");
        model.addAttribute("linkName", "결과물 추가");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/output/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/output/insert")
    public String createOutputPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "결과물 추가");
        model.addAttribute("backLink", "/design/work/output");
        model.addAttribute("link", "/design/work/output/insert");
        model.addAttribute("isNew", true);

        return "/work/design/design";
    }

    @PostMapping(value="/design/work/output/insert")
    public String createOutput(DesignDto designDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("UI/UX 결과물");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Design design = new Design();
        design.setWork(work);
        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "결과물을 추가하였습니다.");
        return "redirect:/design/work/output";
    }

    @GetMapping(value="/design/work/output/workId={workId}")
    public String output(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "결과물");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/output");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/output/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/design/work/output/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/design/design";
    }
    @GetMapping(value="/design/work/output/update/workId={workId}")
    public String updateOutputPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        model.addAttribute("headerTitle", "결과물 수정");
        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("backLink", "/design/work/output/workId=" + workId);
        model.addAttribute("link", "/design/work/output/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/output/update/workId={workId}")
    public String updateOutput(@PathVariable("workId") Long workId, DesignDto designDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Design design = designRepository.findByWork(work);

        design.setProject(designDto.getProject());
        design.setSubject(designDto.getSubject());
        design.setPhase(designDto.getPhase());
        design.setStatus(designDto.getStatus());
        design.setComplete(designDto.getComplete());
        design.setExpire(designDto.getExpire());
        design.setStatement(designDto.getStatement());
        designRepository.save(design);

        attributes.addFlashAttribute("alertData", "결과물을 수정하였습니다.");
        return "redirect:/design/work/output/workId=" + workId;
    }

    @GetMapping(value="/design/work/output/consider/workId={workId}")
    public String considerOutputPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Design design= designRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("design", design);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/design/work/output/workId=" + workId);
        model.addAttribute("link", "/design/work/output/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/output/consider/workId={workId}")
    public String considerOutput(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/design/work/output/workId=" + workId;
    }

    // 제품 디자인 product_design
    @GetMapping(value="/design/work/product_design")
    public String allProductDesign(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("제품 디자인");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-제품 디자인");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/product_design/insert");
        model.addAttribute("linkName", "제품 디자인 추가");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/product_design/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/product_design/insert")
    public String createProductDesignPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "제품 디자인 추가");
        model.addAttribute("backLink", "/design/work/product_design");
        model.addAttribute("link", "/design/work/product_design/insert");
        model.addAttribute("isNew", true);

        return "/work/design/resource";
    }

    @PostMapping(value="/design/work/product_design/insert")
    public String createProductDesign(ResourceDto resourceDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("제품 디자인");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Resource resource = new Resource();
        resource.setWork(work);
        resource.setSubject(resourceDto.getSubject());
        resource.setComplete(resource.getComplete());
        resource.setStatement(resourceDto.getStatement());
        resourceRepository.save(resource);

        attributes.addFlashAttribute("alertData", "제품 디자인을 추가하였습니다.");
        return "redirect:/design/work/product_design";
    }

    @GetMapping(value="/design/work/product_design/workId={workId}")
    public String productDesign(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Resource resource = resourceRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "제품 디자인");
        model.addAttribute("work", work);
        model.addAttribute("resource", resource);
        model.addAttribute("backLink", "/design/work/product_design");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/product_design/update/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/design/resource";
    }
    @GetMapping(value="/design/work/product_design/update/workId={workId}")
    public String updateProductDesignPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Resource resource = resourceRepository.findByWork(work);

        model.addAttribute("headerTitle", "제품 디자인 수정");
        model.addAttribute("work", work);
        model.addAttribute("resource", resource);
        model.addAttribute("backLink", "/design/work/product_design/workId=" + workId);
        model.addAttribute("link", "/design/work/product_design/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/design/resource";
    }
    @PostMapping(value="/design/work/product_design/update/workId={workId}")
    public String updateProductDesign(@PathVariable("workId") Long workId, ResourceDto resourceDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Resource resource = resourceRepository.findByWork(work);

        resource.setWork(work);
        resource.setSubject(resourceDto.getSubject());
        resource.setComplete(resource.getComplete());
        resource.setStatement(resourceDto.getStatement());
        resourceRepository.save(resource);

        attributes.addFlashAttribute("alertData", "제품 디자인을 수정하였습니다.");
        return "redirect:/design/work/product_design/workId=" + workId;
    }

    /*
    @GetMapping(value="/design/work/product_design/consider/workId={workId}")
    public String considerProductDesignPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Resource resource = resourceRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("resource", resource);
        model.addAttribute("responseStaff", staff);
        model.addAttribute("backLink", "/design/work/product_design/workId=" + workId);
        model.addAttribute("link", "/design/work/product_design/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/product_design/consider/workId={workId}")
    public String considerProductDesign(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        work.setResponseStaff(staff);
        work.setResponseDate(date);
        work.setState(Converter.convertToWorkState(workDto.getState()));
        workRepository.save(work);

        return "redirect:/design/work/product_design/workId=" + workId;
    }
     */

    // 제품 디자인 평가 product_design_evaluate
    @GetMapping(value="/design/work/product_design_evaluate")
    public String allProductDesignEvaluate(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("제품 디자인");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "디자인부 업무-제품 디자인");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/design/work/plan/insert");
        model.addAttribute("linkName", "제품 디자인 추가");
        model.addAttribute("backLink", "/design/work");
        model.addAttribute("detailedWorkLink", "/design/work/plan/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/design/work/product_design_evaluate/workId={workId}")
    public String productDesignEvaluate(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Resource resource = resourceRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "제품 디자인");
        model.addAttribute("work", work);
        model.addAttribute("resource", resource);
        model.addAttribute("backLink", "/design/work/product_design_evaluate");
        if(!curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/design/work/product_design_evaluate/consider/workId=" + workId);
        }
        model.addAttribute("isAppraisal", true);

        return "/work/design/resource";
    }

    @GetMapping(value="/design/work/product_design_evaluate/consider/workId={workId}")
    public String considerProductDesignEvaluatePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Resource resource = resourceRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("resource", resource);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/design/work/product_design_evaluate/workId=" + workId);
        model.addAttribute("link", "/design/work/product_design_evaluate/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);
        model.addAttribute("isAppraisal", true);

        return "/work/design/design";
    }
    @PostMapping(value="/design/work/product_design_evaluate/consider/workId={workId}")
    public String considerProductDesignEvaluate(@PathVariable("workId") Long workId, WorkDto workDto, ResourceDto resourceDto,
                                        Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if(resourceDto.getAppraisal().isEmpty()){
            attributes.addFlashAttribute("alertData", "평가내용을 입력하세요.");
            return "redirect:/design/work/product_design_evaluate/consider/workId=" + workId;
        }

        Resource resource = resourceRepository.findByWork(work);
        resource.setAppraisal(resourceDto.getAppraisal());
        resourceRepository.save(resource);

        workRepository.save(work);

        attributes.addFlashAttribute("alertData", "디자인 평가가 완료되었습니다.");

        return "redirect:/design/work/product_design_evaluate/workId=" + workId;
    }
}