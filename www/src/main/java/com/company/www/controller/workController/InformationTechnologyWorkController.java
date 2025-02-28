package com.company.www.controller.workController;


import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.dto.work.WorkDto;
import com.company.www.dto.work.information_technology.DevelopDto;
import com.company.www.dto.work.sales.SoftwareDto;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.information_technology.Develop;
import com.company.www.entity.work.information_technology.Software;
import com.company.www.repository.belong.RoleRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.information_technology.DevelopRepository;
import com.company.www.repository.work.information_technology.SoftwareRepository;
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
public class InformationTechnologyWorkController {

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
    DevelopRepository developRepository;
    @Autowired
    SoftwareRepository softwareRepository;

    @GetMapping(value="/information/work")
    public String programWork(Model model, Principal principal){
        model.addAttribute("headerTitle", "IT부 업무");
        model.addAttribute("backLink", "/");
        model.addAttribute("isWork", true);

        staffInfoService.getInfo(model, principal);

        List<WorkType> workTypes = workTypeRepository.findAllByRole(roleRepository.findByRoleName("IT"));
        workTypes.removeAll(Converter.filter(workTypeRepository, "IT부 업무"));

        model.addAttribute("workType", workTypes);
        model.addAttribute("isWork", true);
        return "/work/work";
    }

    @GetMapping(value="/information/work/plan")
    public String allPlan(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("기획");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-기획");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/information/work/plan/insert");
        model.addAttribute("linkName", "기획 추가");
        model.addAttribute("backLink", "/information/work");
        model.addAttribute("detailedWorkLink", "/information/work/plan/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/plan/insert")
    public String createPlanPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "기획 추가");
        model.addAttribute("backLink", "/information/work/plan");
        model.addAttribute("link", "/information/work/plan/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/develop";
    }

    @PostMapping(value="/information/work/plan/insert")
    public String createPlan(DevelopDto developDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("기획");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Develop develop = new Develop();
        develop.setWork(work);
        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "기획을 추가하였습니다.");
        return "redirect:/information/work/plan";
    }

    @GetMapping(value="/information/work/plan/workId={workId}")
    public String plan(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "기획");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/plan");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/plan/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/plan/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/develop";
    }
    @GetMapping(value="/information/work/plan/update/workId={workId}")
    public String updatePlanPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        model.addAttribute("headerTitle", "기획 수정");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/plan/workId=" + workId);
        model.addAttribute("link", "/information/work/plan/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/plan/update/workId={workId}")
    public String updatePlan(@PathVariable("workId") Long workId, DevelopDto developDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "기획을 수정하였습니다.");
        return "redirect:/information/work/plan/workId=" + workId;
    }

    @GetMapping(value="/information/work/plan/consider/workId={workId}")
    public String considerPlanPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/plan/workId=" + workId);
        model.addAttribute("link", "/information/work/plan/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/plan/consider/workId={workId}")
    public String considerPlan(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/information/work/plan/workId=" + workId;
    }

    // 요구사항
    @GetMapping(value="/information/work/analysis")
    public String allAnalysis(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("요구사항 분석");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-요구사항 분석");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/information/work/analysis/insert");
        model.addAttribute("linkName", "분석 추가");
        model.addAttribute("backLink", "/information/work");
        model.addAttribute("detailedWorkLink", "/information/work/analysis/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/analysis/insert")
    public String createAnalysisPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "분석 추가");
        model.addAttribute("backLink", "/information/work/analysis");
        model.addAttribute("link", "/information/work/analysis/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/develop";
    }

    @PostMapping(value="/information/work/analysis/insert")
    public String createAnalysis(DevelopDto developDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("요구사항 분석");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Develop develop = new Develop();
        develop.setWork(work);
        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "분석을 추가하였습니다.");
        return "redirect:/information/work/analysis";
    }

    @GetMapping(value="/information/work/analysis/workId={workId}")
    public String analysis(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "분석");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/analysis");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/analysis/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/analysis/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/develop";
    }
    @GetMapping(value="/information/work/analysis/update/workId={workId}")
    public String updateAnalysisPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        model.addAttribute("headerTitle", "분석 수정");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/analysis/workId=" + workId);
        model.addAttribute("link", "/information/work/analysis/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/analysis/update/workId={workId}")
    public String updateAnalysis(@PathVariable("workId") Long workId, DevelopDto developDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "분석을 수정하였습니다.");
        return "redirect:/information/work/analysis/workId=" + workId;
    }

    @GetMapping(value="/information/work/analysis/consider/workId={workId}")
    public String considerAnalysisPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/analysis/workId=" + workId);
        model.addAttribute("link", "/information/work/analysis/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/analysis/consider/workId={workId}")
    public String considerAnalysis(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/information/work/analysis/workId=" + workId;
    }

    // 기능&모듈 설계 : layout
    @GetMapping(value="/information/work/design")
    public String allDesign(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("기능&모듈 설계");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-기능&모듈 설계");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/information/work/design/insert");
        model.addAttribute("linkName", "분석 추가");
        model.addAttribute("backLink", "/information/work");
        model.addAttribute("detailedWorkLink", "/information/work/design/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/design/insert")
    public String createDesignPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "설계 추가");
        model.addAttribute("backLink", "/information/work/design");
        model.addAttribute("link", "/information/work/design/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/develop";
    }

    @PostMapping(value="/information/work/design/insert")
    public String createDesign(DevelopDto developDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("기능&모듈 설계");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Develop develop = new Develop();
        develop.setWork(work);
        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "설계를 추가하였습니다.");
        return "redirect:/information/work/design";
    }

    @GetMapping(value="/information/work/design/workId={workId}")
    public String design(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "기능&모듈 설계");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/design");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/design/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/design/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/develop";
    }
    @GetMapping(value="/information/work/design/update/workId={workId}")
    public String updateDesignPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        model.addAttribute("headerTitle", "설계 수정");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/design/workId=" + workId);
        model.addAttribute("link", "/information/work/design/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/design/update/workId={workId}")
    public String updateDesign(@PathVariable("workId") Long workId, DevelopDto developDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "설계를 수정하였습니다.");
        return "redirect:/information/work/design/workId=" + workId;
    }

    @GetMapping(value="/information/work/design/consider/workId={workId}")
    public String considerDesignPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/design/workId=" + workId);
        model.addAttribute("link", "/information/work/design/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/design/consider/workId={workId}")
    public String considerDesign(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/information/work/design/workId=" + workId;
    }

    // 기능&모듈 작성 : coding
    @GetMapping(value="/information/work/coding")
    public String allCodings(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("기능&모듈 작성");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-기능&모듈 작성");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/information/work/coding/insert");
        model.addAttribute("linkName", "분석 추가");
        model.addAttribute("backLink", "/information/work");
        model.addAttribute("detailedWorkLink", "/information/work/coding/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/coding/insert")
    public String createCodingPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "기능&모듈 작성");
        model.addAttribute("backLink", "/information/work/coding");
        model.addAttribute("link", "/information/work/coding/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/develop";
    }

    @PostMapping(value="/information/work/coding/insert")
    public String createCoding(DevelopDto developDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("기능&모듈 작성");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Develop develop = new Develop();
        develop.setWork(work);
        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "기능&모듈을 추가하였습니다.");
        return "redirect:/information/work/coding";
    }

    @GetMapping(value="/information/work/coding/workId={workId}")
    public String coding(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "기능&모듈 작성");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/coding");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/coding/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/coding/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/develop";
    }
    @GetMapping(value="/information/work/coding/update/workId={workId}")
    public String updateCodingPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        model.addAttribute("headerTitle", "기능&모듈 수정");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/coding/workId=" + workId);
        model.addAttribute("link", "/information/work/coding/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/coding/update/workId={workId}")
    public String updateCoding(@PathVariable("workId") Long workId, DevelopDto developDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "기능&모듈을 수정하였습니다.");
        return "redirect:/information/work/coding/workId=" + workId;
    }

    @GetMapping(value="/information/work/coding/consider/workId={workId}")
    public String considerCodingPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/coding/workId=" + workId);
        model.addAttribute("link", "/information/work/coding/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/coding/consider/workId={workId}")
    public String considerCoding(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);
        return "redirect:/information/work/coding/workId=" + workId;
    }

    // 단위 테스트 : unit_test
    @GetMapping(value="/information/work/unit_test")
    public String allUnitTest(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("단위 테스트");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-단위 테스트");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/information/work/unit_test/insert");
        model.addAttribute("linkName", "단위 테스트 추가");
        model.addAttribute("backLink", "/information/work");
        model.addAttribute("detailedWorkLink", "/information/work/unit_test/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/unit_test/insert")
    public String createUnitTestPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "단위 테스트 추가");
        model.addAttribute("backLink", "/information/work/unit_test");
        model.addAttribute("link", "/information/work/unit_test/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/develop";
    }

    @PostMapping(value="/information/work/unit_test/insert")
    public String createUnitTest(DevelopDto developDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("단위 테스트");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Develop develop = new Develop();
        develop.setWork(work);
        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        develop.setAppraisal(developDto.getAppraisal());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "단위 테스트를 추가하였습니다.");
        return "redirect:/information/work/unit_test";
    }

    @GetMapping(value="/information/work/unit_test/workId={workId}")
    public String unitTest(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "단위 테스트");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/unit_test");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/unit_test/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/unit_test/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/develop";
    }
    @GetMapping(value="/information/work/unit_test/update/workId={workId}")
    public String updateUnitTestPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        model.addAttribute("headerTitle", "단위 테스트 수정");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/unit_test/workId=" + workId);
        model.addAttribute("link", "/information/work/unit_test/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/unit_test/update/workId={workId}")
    public String updateUnitTest(@PathVariable("workId") Long workId, DevelopDto developDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        develop.setAppraisal(developDto.getAppraisal());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "단위 테스트를 수정하였습니다.");
        return "redirect:/information/work/unit_test/workId=" + workId;
    }

    @GetMapping(value="/information/work/unit_test/consider/workId={workId}")
    public String considerUnitTestPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/unit_test/workId=" + workId);
        model.addAttribute("link", "/information/work/unit_test/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/unit_test/consider/workId={workId}")
    public String considerUnitTest(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/information/work/unit_test/workId=" + workId;
    }

    // 통합 테스트 : integration_test
    @GetMapping(value="/information/work/integration_test")
    public String allIntegrationTest(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("통합 테스트");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-통합 테스트");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/information/work/integration_test/insert");
        model.addAttribute("linkName", "통합 테스트 추가");
        model.addAttribute("backLink", "/information/work");
        model.addAttribute("detailedWorkLink", "/information/work/integration_test/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/integration_test/insert")
    public String createIntegrationTestPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "통합 테스트 추가");
        model.addAttribute("backLink", "/information/work/integration_test");
        model.addAttribute("link", "/information/work/integration_test/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/develop";
    }

    @PostMapping(value="/information/work/integration_test/insert")
    public String createIntegrationTest(DevelopDto developDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("통합 테스트");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Develop develop = new Develop();
        develop.setWork(work);
        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        develop.setAppraisal(developDto.getAppraisal());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "통합 테스트를 추가하였습니다.");
        return "redirect:/information/work/integration_test";
    }

    @GetMapping(value="/information/work/integration_test/workId={workId}")
    public String integrationTest(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "통합 테스트");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/integration_test");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/integration_test/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/integration_test/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/develop";
    }
    @GetMapping(value="/information/work/integration_test/update/workId={workId}")
    public String updateIntegrationTestPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        model.addAttribute("headerTitle", "통합 테스트 수정");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/integration_test/workId=" + workId);
        model.addAttribute("link", "/information/work/integration_test/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/integration_test/update/workId={workId}")
    public String updateIntegrationTest(@PathVariable("workId") Long workId, DevelopDto developDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        develop.setAppraisal(developDto.getAppraisal());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "통합 테스트를 수정하였습니다.");
        return "redirect:/information/work/integration_test/workId=" + workId;
    }

    @GetMapping(value="/information/work/integration_test/consider/workId={workId}")
    public String considerIntegrationTestPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/integration_test/workId=" + workId);
        model.addAttribute("link", "/information/work/integration_test/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/integration_test/consider/workId={workId}")
    public String considerIntegrationTest(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/information/work/integration_test/workId=" + workId;
    }

    // 시스템 테스트 : system_test
    @GetMapping(value="/information/work/system_test")
    public String allSystemTest(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("시스템 테스트");
        Page<Work> transaction = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-시스템 테스트");
        model.addAttribute("paging", transaction);
        model.addAttribute("link", "/information/work/system_test/insert");
        model.addAttribute("linkName", "시스템 테스트 추가");
        model.addAttribute("backLink", "/information/work");
        model.addAttribute("detailedWorkLink", "/information/work/system_test/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/system_test/insert")
    public String createSystemTestPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "시스템 테스트 추가");
        model.addAttribute("backLink", "/information/work/system_test");
        model.addAttribute("link", "/information/work/system_test/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/develop";
    }

    @PostMapping(value="/information/work/system_test/insert")
    public String createSystemTest(DevelopDto developDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("시스템 테스트");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Develop develop = new Develop();
        develop.setWork(work);
        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        develop.setAppraisal(developDto.getAppraisal());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "시스템 테스트를 추가하였습니다.");
        return "redirect:/information/work/system_test";
    }

    @GetMapping(value="/information/work/system_test/workId={workId}")
    public String systemTest(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "시스템 테스트");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/system_test");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/system_test/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/system_test/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/develop";
    }
    @GetMapping(value="/information/work/system_test/update/workId={workId}")
    public String updateSystemTestPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        model.addAttribute("headerTitle", "시스템 테스트 수정");
        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("backLink", "/information/work/system_test/workId=" + workId);
        model.addAttribute("link", "/information/work/system_test/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/system_test/update/workId={workId}")
    public String updateSystemTest(@PathVariable("workId") Long workId, DevelopDto developDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);

        develop.setProject(developDto.getProject());
        develop.setSubject(developDto.getSubject());
        develop.setPhase(developDto.getPhase());
        develop.setStatus(developDto.getStatus());
        if(developDto.getComplete() != null){
            develop.setComplete(developDto.getComplete());
        }
        develop.setExpire(developDto.getExpire());
        develop.setStatement(developDto.getStatement());
        develop.setAppraisal(developDto.getAppraisal());
        developRepository.save(develop);

        attributes.addFlashAttribute("alertData", "시스템 테스트를 수정하였습니다.");
        return "redirect:/information/work/system_test/workId=" + workId;
    }

    @GetMapping(value="/information/work/system_test/consider/workId={workId}")
    public String considerSystemTestPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Develop develop = developRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("develop", develop);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/system_test/workId=" + workId);
        model.addAttribute("link", "/information/work/system_test/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/develop";
    }
    @PostMapping(value="/information/work/system_test/consider/workId={workId}")
    public String considerSystemTest(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/information/work/system_test/workId=" + workId;
    }

    // 결과물 : output
    @GetMapping(value="/information/work/output")
    public String allSoftware(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("제품 결과물");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "IT부 업무-개발-제품 결과물");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/information/work/output/insert");
        model.addAttribute("linkName", "주문");
        model.addAttribute("backLink", "/information/work/output");
        model.addAttribute("detailedWorkLink", "/information/work/output/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/information/work/output/insert")
    public String createSoftwarePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "소프트웨어 추가");
        model.addAttribute("backLink", "/information/work/output");
        model.addAttribute("link", "/information/work/output/insert");
        model.addAttribute("isNew", true);

        return "/work/information_technology/software";
    }

    @PostMapping(value="/information/work/output/insert")
    public String createSoftware(SoftwareDto softwareDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("소프트웨어");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate developDate = softwareDto.getDevelopDate();
        developDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Software software= new Software();
        software.setWork(work);
        software.setSoftwareName(softwareDto.getSoftwareName());
        software.setSoftwareType(softwareDto.getSoftwareType());
        software.setDevelopDate(developDate);
        software.setPrice(softwareDto.getPrice());
        software.setStatement(softwareDto.getStatement());
        software.setMainFunction(softwareDto.getMainFunction());

        softwareRepository.save(software);

        attributes.addFlashAttribute("alertData", "소프트웨어를 추가하였습니다.");
        return "redirect:/information/work/output";
    }

    @GetMapping(value="/information/work/output/workId={workId}")
    public String software(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Software software= softwareRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "소프트웨어");
        model.addAttribute("work", work);
        model.addAttribute("software", software);
        model.addAttribute("backLink", "/information/work/output");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/information/work/output/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/information/work/output/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/information_technology/software";
    }
    @GetMapping(value="/information/work/output/update/workId={workId}")
    public String updateSoftwarePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Software software = softwareRepository.findByWork(work);

        model.addAttribute("headerTitle", "소프트웨어 수정");
        model.addAttribute("work", work);
        model.addAttribute("software", software);
        model.addAttribute("backLink", "/information/work/output/workId=" + workId);
        model.addAttribute("link", "/information/work/output/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/information_technology/software";
    }
    @PostMapping(value="/information/work/output/update/workId={workId}")
    public String updateSoftware(@PathVariable("workId") Long workId, SoftwareDto softwareDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Software software = softwareRepository.findByWork(work);

        LocalDate developDate = softwareDto.getDevelopDate();
        developDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        software.setSoftwareName(softwareDto.getSoftwareName());
        software.setSoftwareType(softwareDto.getSoftwareType());
        software.setDevelopDate(developDate);
        software.setPrice(softwareDto.getPrice());
        software.setStatement(softwareDto.getStatement());
        software.setMainFunction(softwareDto.getMainFunction());

        softwareRepository.save(software);

        attributes.addFlashAttribute("alertData", "소프트웨어를 수정하였습니다.");
        return "redirect:/information/work/output/workId=" + workId;
    }

    @GetMapping(value="/information/work/output/consider/workId={workId}")
    public String considerSoftwarePage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Software software = softwareRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("software", software);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/information/work/output/workId=" + workId);
        model.addAttribute("link", "/information/work/output/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/information_technology/software";
    }
    @PostMapping(value="/information/work/output/consider/workId={workId}")
    public String considerSoftware(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/information/work/output/workId=" + workId;
    }

}