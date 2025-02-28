package com.company.www.controller.workController;


import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.dto.work.WorkDto;
import com.company.www.dto.work.sales.*;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.account.StandardCost;
import com.company.www.entity.work.information_technology.Software;
import com.company.www.entity.work.sales.*;
import com.company.www.repository.belong.RoleRepository;
import com.company.www.repository.work.account.StandardCostRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.information_technology.SoftwareRepository;
import com.company.www.repository.work.sales.*;
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
public class SalesWorkController {

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
    StandardCostRepository standardCostRepository;

    @Autowired
    PatronRepository patronRepository;
    @Autowired
    CreditRepository creditRepository;

    @Autowired
    SoftwareQuotationRepository softwareQuotationRepository;
    @Autowired
    SoftwareOrderRepository softwareOrderRepository;
    @Autowired
    SoftwareItemRepository softwareItemRepository;
    @Autowired
    SoftwareBillRepository softwareBillRepository;

    @Autowired
    DevelopQuotationRepository developQuotationRepository;
    @Autowired
    DevelopOrderRepository developOrderRepository;
    @Autowired
    UnitRepository unitRepository;
    @Autowired
    DevelopBillRepository developBillRepository;

    @Autowired
    SoftwareRepository softwareRepository;
    @Autowired
    SoftwareSalesRepository softwareSalesRepository;

    @GetMapping(value="/sales/work")
    public String salesWork(Model model, Principal principal){
        model.addAttribute("headerTitle", "영업부 업무");
        model.addAttribute("backLink", "/");
        model.addAttribute("isWork", true);

        staffInfoService.getInfo(model, principal);

        List<WorkType> workTypes = workTypeRepository.findAllByRole(roleRepository.findByRoleName("영업"));
        workTypes.removeAll(Converter.filter(workTypeRepository, 
                "고객 정보", "고객 신용 정보", "소프트웨어 견적서", "개발 견적서", "소프트웨어 주문", "개발 주문", "소프트웨어 대금", "개발 대금", "출하 관리"
                )
        );
        model.addAttribute("workType", workTypes);
        model.addAttribute("isWork", true);

        return "/work/work";
    }

    @GetMapping(value="/sales/work/patron")
    public String patronIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("고객 정보", "/patron/information");
        works.put("고객 신용 정보", "/patron/credit");

        model.addAttribute("headerTitle", "영업부 업무-고객");
        model.addAttribute("backLink", "/sales/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    @GetMapping(value="/patron/information")
    public String allPatron(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("고객 정보");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-고객-고객 정보");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/patron/information/insert");
        model.addAttribute("linkName", "고객 정보");
        model.addAttribute("backLink", "/sales/work/patron");
        model.addAttribute("detailedWorkLink", "/patron/information/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/patron/information/insert")
    public String createPatronPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("requestStaff", staff);
        model.addAttribute("headerTitle", "고객 정보 추가");
        model.addAttribute("backLink", "/patron/information");
        model.addAttribute("link", "/patron/information/insert");
        model.addAttribute("isNew", true);

        return "/work/sales/patron";
    }

    @PostMapping(value="/patron/information/insert")
    public String createPatron(PatronDto patronDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("고객 정보");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Patron patron = new Patron();
        patron.setWork(work);
        patron.setName(patronDto.getName());
        patron.setType(patronDto.getType());
        patron.setContact(patronDto.getContact());
        patron.setLocation(patronDto.getLocation());
        patronRepository.save(patron);

        attributes.addFlashAttribute("alertData", "고객정보를 추가하였습니다.");
        return "redirect:/patron/information";
    }

    @GetMapping(value="/patron/information/workId={workId}")
    public String patron(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Patron patron = patronRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "고객정보");
        model.addAttribute("work", work);
        model.addAttribute("patron", patron);
        model.addAttribute("backLink", "/patron/information");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/patron/information/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/patron/information/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/sales/patron";
    }
    @GetMapping(value="/patron/information/update/workId={workId}")
    public String updatePatronPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Patron patron = patronRepository.findByWork(work);

        model.addAttribute("headerTitle", "고객정보 수정");
        model.addAttribute("work", work);
        model.addAttribute("patron", patron);
        model.addAttribute("backLink", "/patron/information/workId=" + workId);
        model.addAttribute("link", "/patron/information/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/sales/patron";
    }
    @PostMapping(value="/patron/information/update/workId={workId}")
    public String updatePatron(@PathVariable("workId") Long workId, PatronDto patronDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Patron patron = patronRepository.findByWork(work);

        patron.setName(patronDto.getName());
        patron.setType(patronDto.getType());
        patron.setContact(patronDto.getContact());
        patron.setLocation(patronDto.getLocation());
        patronRepository.save(patron);

        attributes.addFlashAttribute("alertData", "고객정보를 수정하였습니다.");
        return "redirect:/patron/information/workId=" + workId;
    }

    @GetMapping(value="/patron/information/consider/workId={workId}")
    public String considerPatronPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Patron patron = patronRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("patron", patron);
        model.addAttribute("draftStaff", staff);
        model.addAttribute("backLink", "/patron/information/workId=" + workId);
        model.addAttribute("link", "/patron/information/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/patron";
    }
    @PostMapping(value="/patron/information/consider/workId={workId}")
    public String considerPatron(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/patron/information/workId=" + workId;
    }

    // 고객 신용정보
    @GetMapping(value="/patron/credit")
    public String allCredit(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("고객 신용 정보");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-고객-고객 신용 정보");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/patron/credit/insert");
        model.addAttribute("linkName", "고객 정보");
        model.addAttribute("backLink", "/sales/work/patron");
        model.addAttribute("detailedWorkLink", "/patron/credit/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/patron/credit/insert")
    public String createCreditPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "고객 신용 정보 추가");
        model.addAttribute("backLink", "/patron/credit");
        model.addAttribute("link", "/patron/credit/insert");
        model.addAttribute("isNew", true);

        return "/work/sales/credit";
    }

    @PostMapping(value="/patron/credit/insert")
    public String createCredit(CreditDto creditDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("고객 신용 정보");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                creditDto.getName(), creditDto.getType(), creditDto.getLocation(), creditDto.getContact()
        );
        Credit credit = new Credit();
        credit.setWork(work);
        credit.setPatron(patron);
        credit.setGrade(creditDto.getGrade());
        credit.setScore(creditDto.getScore());
        credit.setDebt(new ArrayList<>());
        credit.setTotalPayable(0);
        credit.setTotalReceivable(0);
        credit.setPaid(0);
        credit.setReceived(0);
        creditRepository.save(credit);

        attributes.addFlashAttribute("alertData", "고객 신용 정보를 추가하였습니다.");
        return "redirect:/patron/credit";
    }

    @GetMapping(value="/patron/credit/workId={workId}")
    public String credit(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Credit credit = creditRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "고객 신용 정보");
        model.addAttribute("work", work);
        model.addAttribute("credit", credit);
        model.addAttribute("backLink", "/patron/credit");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/patron/credit/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/patron/credit/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/sales/credit";
    }
    @GetMapping(value="/patron/credit/update/workId={workId}")
    public String updateCreditPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Credit credit = creditRepository.findByWork(work);

        model.addAttribute("headerTitle", "고객 신용 정보 수정");
        model.addAttribute("work", work);
        model.addAttribute("credit", credit);
        model.addAttribute("backLink", "/patron/credit/workId=" + workId);
        model.addAttribute("link", "/patron/credit/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/sales/credit";
    }
    @PostMapping(value="/patron/credit/update/workId={workId}")
    public String updateCredit(@PathVariable("workId") Long workId, CreditDto creditDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Credit credit = creditRepository.findByWork(work);

        credit.setGrade(creditDto.getGrade());
        credit.setScore(creditDto.getScore());
        creditRepository.save(credit);

        attributes.addFlashAttribute("alertData", "고객정보를 수정하였습니다.");
        return "redirect:/patron/credit/workId=" + workId;
    }

    @GetMapping(value="/patron/credit/consider/workId={workId}")
    public String considerCreditPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Credit credit = creditRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("credit", credit);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/patron/credit/workId=" + workId);
        model.addAttribute("link", "/patron/credit/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/credit";
    }
    @PostMapping(value="/patron/credit/consider/workId={workId}")
    public String considerCredit(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/patron/credit/workId=" + workId;
    }

    @GetMapping(value="/sales/work/quotation")
    public String quotationIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("소프트웨어 견적서", "/quotation/software");
        works.put("개발 견적서", "/quotation/develop");

        model.addAttribute("headerTitle", "영업부 업무-견적서");
        model.addAttribute("backLink", "/sales/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }
    
    // 소프트웨어 견적서
    @GetMapping(value="/quotation/software")
    public String allSoftwareQuotation(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 견적서");
        Page<Work> equityChangeStatement = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부 업무-견적서-소프트웨어 견적서");
        model.addAttribute("paging", equityChangeStatement);
        model.addAttribute("link", "/quotation/software/insert");
        model.addAttribute("linkName", "소프트웨어 견적서");
        model.addAttribute("backLink", "/sales/work/quotation");
        model.addAttribute("detailedWorkLink", "/quotation/software/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/quotation/software/insert")
    public String createSoftwareQuotationPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "소프트웨어 견적서 추가");
        model.addAttribute("backLink", "/quotation/software");
        model.addAttribute("link", "/quotation/software/insert");
        model.addAttribute("isNew", true);

        String [] tags = new String [] {
                "<td class='w-25'><input type='text' class='inner-width form-control' name='softwareName'></td>",
                "<td class='w-25'><input type='text' class='inner-width form-control' name='softwareType'></td>",
                "<td class='w-25'><input type='text' class='inner-width form-control' name='quantity'> </td>",
                "<td class='w-25'><button type='button' class='inner-width form-control' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        return "/work/sales/software_quotation";
    }

    @PostMapping(value="/quotation/software/insert")
    public String createSoftwareQuotation(SoftwareQuotationDto softwareQuotationDto, SoftwareItemDto softwareItemDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 견적서");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate quotationDate = softwareQuotationDto.getDate();
        quotationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                softwareQuotationDto.getName(),
                softwareQuotationDto.getType(),
                softwareQuotationDto.getLocation(),
                softwareQuotationDto.getContact()
        );

        SoftwareQuotation softwareQuotation = new SoftwareQuotation();
        softwareQuotation.setWork(work);
        softwareQuotation.setDate(quotationDate);
        softwareQuotation.setPatron(patron);
        softwareQuotation.setSoftwareOrder(null);
        softwareQuotation.setQuotation(softwareQuotationDto.getQuotation());
        softwareQuotationRepository.save(softwareQuotation);

        long total = 0, price;
        Software software; List<SoftwareItem> allSoftware = new ArrayList<>();
        for(int i = 0; i < softwareItemDto.getSoftwareName().length; i++){
            software = softwareRepository.findBySoftwareNameAndSoftwareType(softwareItemDto.getSoftwareName()[i], softwareItemDto.getSoftwareType()[i]);
            price = software.getPrice() * softwareItemDto.getQuantity()[i];
            total += price;

            SoftwareItem softwareItem = new SoftwareItem();
            softwareItem.setSoftwareQuotation(softwareQuotation);
            softwareItem.setSoftware(software);
            softwareItem.setQuantity(softwareItemDto.getQuantity()[i]);
            softwareItem.setSoftwareTotalPrice(price);
            softwareItemRepository.save(softwareItem);

            allSoftware.add(softwareItem);
        }
        softwareQuotation.setSoftwareItems(allSoftware);
        softwareQuotation.setTotalPrice(total);
        softwareQuotationRepository.save(softwareQuotation);

        attributes.addFlashAttribute("alertData", "소프트웨어 견적서를 추가하였습니다.");
        return "redirect:/quotation/software";
    }

    @GetMapping(value="/quotation/software/workId={workId}")
    public String softwareQuotation(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareQuotation softwareQuotation = softwareQuotationRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "소프트웨어 견적서");
        model.addAttribute("work", work);
        model.addAttribute("softwareQuotation", softwareQuotation);
        model.addAttribute("backLink", "/quotation/software");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/quotation/software/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/quotation/software/consider/workId=" + workId);
        }
        model.addAttribute("dataDeleteLink", ("/quotation/software/workId=" + workId + "/delete/"));
        model.addAttribute("isUpdatable", true);

        return "/work/sales/software_quotation";
    }
    @GetMapping(value="/quotation/software/workId={workId}/delete/{softwareItemId}")
    public String deleteSoftwareQuotationData(@PathVariable("workId") Long workId, @PathVariable("softwareItemId") Long softwareItemId,
                                         RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        SoftwareQuotation softwareQuotation = softwareQuotationRepository.findByWork(work);
        SoftwareItem softwareItem = softwareItemRepository.findBySoftwareItemId(softwareItemId);
        softwareQuotation.getSoftwareItems().remove(softwareItem);
        softwareItemRepository.delete(softwareItem);
        softwareQuotationRepository.save(softwareQuotation);

        attributes.addFlashAttribute("alertData", "소프트웨어 견적서의 요소를 삭제했습니다.");
        return "redirect:/quotation/software/workId=" + workId;
    }
    @GetMapping(value="/quotation/software/update/workId={workId}")
    public String updateSoftwareQuotationPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        SoftwareQuotation softwareQuotation = softwareQuotationRepository.findByWork(work);

        model.addAttribute("headerTitle", "자본변동표 수정");
        model.addAttribute("work", work);
        model.addAttribute("softwareQuotation", softwareQuotation);
        model.addAttribute("backLink", "/quotation/software/workId=" + workId);
        model.addAttribute("link", "/quotation/software/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        String [] tags = new String [] {
                "<td class='w-25'><input type='text' class='inner-width form-control' name='softwareName'></td>",
                "<td class='w-25'><input type='text' class='inner-width form-control' name='softwareType'></td>",
                "<td class='w-25'><input type='text' class='inner-width form-control' name='quantity'> </td>",
                "<td class='w-25'><button type='button' class='inner-width form-control' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);

        return "/work/sales/software_quotation";
    }
    @PostMapping(value="/quotation/software/update/workId={workId}")
    public String updateSoftwareQuotation(@PathVariable("workId") Long workId, SoftwareQuotationDto softwareQuotationDto,
                                          SoftwareItemDto softwareItemDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        SoftwareQuotation softwareQuotation = softwareQuotationRepository.findByWork(work);

        LocalDate date = softwareQuotationDto.getDate();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        softwareQuotation.setQuotation(softwareQuotationDto.getQuotation());
        softwareQuotation.setDate(date);

        List<SoftwareItem> allSoftware = softwareQuotation.getSoftwareItems();
        Software software;
        long total = 0, price;
        for(int i = 0; i < allSoftware.size(); i++){
            SoftwareItem softwareItem = allSoftware.get(i);
            software = softwareRepository.findBySoftwareNameAndSoftwareType(
                    softwareItemDto.getSoftwareName()[i], softwareItemDto.getSoftwareType()[i]
            );
            price = softwareItemDto.getQuantity()[i] * software.getPrice();
            total += price;

            softwareItem.setSoftware(software);
            softwareItem.setQuantity(softwareItemDto.getQuantity()[i]);
            softwareItem.setSoftwareTotalPrice(price);
            softwareItemRepository.save(softwareItem);
        }
        if(allSoftware.size() < softwareItemDto.getSoftwareName().length){
            for(int i = allSoftware.size(); i < softwareItemDto.getSoftwareName().length; i++){
                software = softwareRepository.findBySoftwareNameAndSoftwareType(softwareItemDto.getSoftwareName()[i], softwareItemDto.getSoftwareType()[i]);
                price = software.getPrice() * softwareItemDto.getQuantity()[i];
                total += price;

                SoftwareItem additional = new SoftwareItem();
                additional.setSoftwareQuotation(softwareQuotation);
                additional.setSoftware(software);
                additional.setQuantity(softwareItemDto.getQuantity()[i]);
                additional.setSoftwareTotalPrice(price);
                softwareItemRepository.save(additional);

                allSoftware.add(additional);
            }
        }
        softwareQuotation.setSoftwareItems(allSoftware);
        softwareQuotation.setTotalPrice(total);
        softwareQuotationRepository.save(softwareQuotation);

        attributes.addFlashAttribute("alertData", "소프트웨어 견적서를 수정하였습니다.");
        return "redirect:/quotation/software/workId=" + workId;
    }

    @GetMapping(value="/quotation/software/consider/workId={workId}")
    public String considerSoftwareQuotationPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareQuotation softwareQuotation = softwareQuotationRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("softwareQuotation", softwareQuotation);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/quotation/software/workId=" + workId);
        model.addAttribute("link", "/quotation/software/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/software_quotation";
    }
    @PostMapping(value="/quotation/software/consider/workId={workId}")
    public String considerSoftwareQuotation(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/quotation/software/workId=" + workId;
    }

    // 개발 견적서
    @GetMapping(value="/quotation/develop")
    public String allDevelopQuotation(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("개발 견적서");
        Page<Work> equityChangeStatement = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부 업무-견적서-개발 견적서");
        model.addAttribute("paging", equityChangeStatement);
        model.addAttribute("link", "/quotation/develop/insert");
        model.addAttribute("linkName", "개발 견적서");
        model.addAttribute("backLink", "/sales/work/quotation");
        model.addAttribute("detailedWorkLink", "/quotation/develop/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/quotation/develop/insert")
    public String createDevelopQuotationPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "개발 견적서 추가");
        model.addAttribute("backLink", "/quotation/software");
        model.addAttribute("link", "/quotation/software/insert");
        model.addAttribute("isNew", true);

        String [] tags = new String [] {
                "<td class='width-16'><input type='text' class='inner-width form-control' name='item'></td>",
                "<td class='width-16'><input type='text' class='inner-width form-control' name='type'></td>",
                "<td class='width-16'><input type='text' class='inner-width form-control' name='grade'> </td>",
                "<td class='width-16'><input type='date' class='inner-width form-control' name='date'></td>",
                "<td class='width-16'><input type='text' class='inner-width form-control' name='quantity'> </td>",
                "<td class='width-16'><button type='button' class='inner-width form-control' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);
        return "/work/sales/develop_quotation";
    }

    @PostMapping(value="/quotation/develop/insert")
    public String createDevelopQuotation(DevelopQuotationDto developQuotationDto, UnitDto unitDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 견적서");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate quotationDate = developQuotationDto.getDate();
        quotationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                developQuotationDto.getName(),
                developQuotationDto.getType(),
                developQuotationDto.getLocation(),
                developQuotationDto.getContact()
        );

        DevelopQuotation developQuotation = new DevelopQuotation();
        developQuotation.setWork(work);
        developQuotation.setDate(quotationDate);
        developQuotation.setPatron(patron);
        developQuotation.setQuotation(developQuotationDto.getQuotation());
        developQuotationRepository.save(developQuotation);

        long total = 0;
        StandardCost standardCost; List<Unit> units = new ArrayList<>();
        for(int i = 0; i < unitDto.getItem().length; i++){
            standardCost = standardCostRepository.findByTypeAndItemAndGradeAndDate(
                    unitDto.getType()[i], unitDto.getItem()[i], unitDto.getGrade()[i], unitDto.getDate()[i]
            );
            // total += (long) Math.ceil(standardCost.getTotal() * unitDto.getManMonth()[i] * unitDto.getQuantity()[i]);

            Unit unit = new Unit();
            // unit.setDevelopQuotation(developQuotation);
            unit.setStandardCost(standardCost);
            unit.setQuantity(unitDto.getQuantity()[i]);
            unit.setTask(unitDto.getTask()[i]);
            unitRepository.save(unit);
            units.add(unit);
        }
        developQuotation.setUnits(units);
        developQuotation.setDevelopCost(total);
        developQuotationRepository.save(developQuotation);

        attributes.addFlashAttribute("alertData", "개발 견적서를 추가하였습니다.");
        return "redirect:/quotation/develop";
    }

    @GetMapping(value="/quotation/develop/workId={workId}")
    public String developQuotation(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DevelopQuotation developQuotation = developQuotationRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "소프트웨어 견적서");
        model.addAttribute("work", work);
        model.addAttribute("developQuotation", developQuotation);
        model.addAttribute("backLink", "/quotation/software");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/quotation/develop/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/quotation/develop/consider/workId=" + workId);
        }
        model.addAttribute("dataDeleteLink", ("/quotation/develop/workId=" + workId + "/delete/"));
        model.addAttribute("isUpdatable", true);

        return "/work/sales/develop_quotation";
    }
    @GetMapping(value="/quotation/develop/workId={workId}/delete/{unitId}")
    public String deleteDevelopQuotationData(@PathVariable("workId") Long workId, @PathVariable("unitId") Long unitId,
                                              RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        DevelopQuotation developQuotation = developQuotationRepository.findByWork(work);
        Unit unit = unitRepository.findByUnitId(unitId);
        developQuotation.getUnits().remove(unit);
        unitRepository.delete(unit);
        developQuotationRepository.save(developQuotation);

        attributes.addFlashAttribute("alertData", "개발 견적서의 요소를 삭제했습니다.");
        return "redirect:/quotation/develop/workId=" + workId;
    }
    @GetMapping(value="/quotation/develop/update/workId={workId}")
    public String updateDevelopQuotationPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        DevelopQuotation developQuotation = developQuotationRepository.findByWork(work);

        model.addAttribute("headerTitle", "자본변동표 수정");
        model.addAttribute("work", work);
        model.addAttribute("developQuotation", developQuotation);
        model.addAttribute("backLink", "/quotation/develop/workId=" + workId);
        model.addAttribute("link", "/quotation/develop/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        String [] tags = new String [] {
                "<td class='width-16'><input type='text' class='inner-width form-control' name='item'></td>",
                "<td class='width-16'><input type='text' class='inner-width form-control' name='type'></td>",
                "<td class='width-16'><input type='text' class='inner-width form-control' name='grade'> </td>",
                "<td class='width-16'><input type='date' class='inner-width form-control' name='date'></td>",
                "<td class='width-16'><input type='text' class='inner-width form-control' name='quantity'> </td>",
                "<td class='width-16'><button type='button' class='inner-width form-control' onclick='deleteMenuEvent(this)'>행 삭제</button></td>"
        };
        model.addAttribute("tags", tags);

        return "/work/sales/develop_quotation";
    }
    @PostMapping(value="/quotation/develop/update/workId={workId}")
    public String updateDevelopQuotation(@PathVariable("workId") Long workId, DevelopQuotationDto developQuotationDto,
                                          UnitDto unitDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        DevelopQuotation developQuotation = developQuotationRepository.findByWork(work);

        LocalDate date = developQuotationDto.getDate();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        developQuotation.setQuotation(developQuotationDto.getQuotation());
        developQuotation.setDate(date);

        List<Unit> units = developQuotation.getUnits();
        StandardCost standardCost;
        long total = 0;
        for(int i = 0; i < units.size(); i++){
            Unit unit = units.get(i);
            standardCost = standardCostRepository.findByTypeAndItemAndGradeAndDate(
                    unitDto.getType()[i], unitDto.getItem()[i], unitDto.getGrade()[i], unitDto.getDate()[i]
            );
            // total += (long) Math.ceil(standardCost.getCost() * unitDto.getQuantity()[i] * unitDto.getManMonth()[i]);

            unit.setStandardCost(standardCost);
            unit.setQuantity(unitDto.getQuantity()[i]);
            unit.setTask(unitDto.getTask()[i]);
            unitRepository.save(unit);
        }
        if(units.size() < unitDto.getItem().length){
            for(int i = units.size(); i < unitDto.getItem().length; i++){
                standardCost = standardCostRepository.findByTypeAndItemAndGradeAndDate(
                        unitDto.getType()[i], unitDto.getItem()[i], unitDto.getGrade()[i], unitDto.getDate()[i]
                );
                // total += (long) Math.ceil(standardCost.getTotal() * unitDto.getManMonth()[i] * unitDto.getQuantity()[i]);

                Unit unit = new Unit();
                // unit.setDevelopQuotation(developQuotation);
                unit.setStandardCost(standardCost);
                unit.setQuantity(unitDto.getQuantity()[i]);
                unit.setTask(unitDto.getTask()[i]);
                unitRepository.save(unit);
                units.add(unit);
            }
        }
        developQuotation.setDevelopCost(total);
        developQuotation.setUnits(units);
        developQuotationRepository.save(developQuotation);

        attributes.addFlashAttribute("alertData", "개발 견적서를 수정하였습니다.");
        return "redirect:/quotation/develop/workId=" + workId;
    }

    @GetMapping(value="/quotation/develop/consider/workId={workId}")
    public String considerDevelopQuotationPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DevelopQuotation developQuotation = developQuotationRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("developQuotation", developQuotation);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/quotation/develop/workId=" + workId);
        model.addAttribute("link", "/quotation/develop/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/develop_quotation";
    }
    @PostMapping(value="/quotation/develop/consider/workId={workId}")
    public String considerDevelopQuotation(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/quotation/develop/workId=" + workId;
    }

    //
    @GetMapping(value="/sales/work/order")
    public String orderIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("소프트웨어 주문", "/order/software");
        works.put("소프트웨어 개발 주문", "/order/develop");

        model.addAttribute("headerTitle", "영업부 업무-주문");
        model.addAttribute("backLink", "/sales/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    // 소프트웨어 주문
    @GetMapping(value="/order/software")
    public String allSoftwareOrder(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 주문");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-주문-소프트웨어 주문");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/order/software/insert");
        model.addAttribute("linkName", "주문");
        model.addAttribute("backLink", "/sales/work/order");
        model.addAttribute("detailedWorkLink", "/order/software/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/order/software/insert")
    public String createSoftwareOrderPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "소프트웨어 주문 추가");
        model.addAttribute("backLink", "/order/software");
        model.addAttribute("link", "/order/software/insert");
        model.addAttribute("isNew", true);

        return "/work/sales/software_order";
    }

    @PostMapping(value="/order/software/insert")
    public String createSoftwareOrder(SoftwareOrderDto softwareOrderDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 주문");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                softwareOrderDto.getName(), softwareOrderDto.getType(), softwareOrderDto.getLocation(), softwareOrderDto.getContact()
        );
        SoftwareQuotation softwareQuotation = softwareQuotationRepository.findByQuotationAndPatron(softwareOrderDto.getQuotation(), patron);

        String [] dateCode = softwareOrderDto.getOrderDate().toString().split("-");
        String orderCode = "SWO-" + dateCode[0] + dateCode[1] + dateCode[2] + "-" + softwareQuotation.getQuotation();

        SoftwareOrder softwareOrder = new SoftwareOrder();
        softwareOrder.setWork(work);
        softwareOrder.setPatron(patron);
        softwareOrder.setSoftwareQuotation(softwareQuotation);
        softwareOrder.setOrderCode(orderCode);
        softwareOrder.setOrderDate(softwareOrderDto.getOrderDate());
        softwareOrder.setProvideDate(softwareOrderDto.getProvideDate());
        softwareOrder.setPayment(softwareOrderDto.getPayment());
        softwareOrder.setStatus(softwareOrderDto.getStatus());
        softwareOrder.setStatement(softwareOrderDto.getStatement());

        softwareOrderRepository.save(softwareOrder);

        softwareQuotation.setSoftwareOrder(softwareOrder);

        softwareQuotationRepository.save(softwareQuotation);

        attributes.addFlashAttribute("alertData", "소프트웨어 주문을 추가하였습니다.");
        return "redirect:/order/software";
    }

    @GetMapping(value="/order/software/workId={workId}")
    public String softwareOrder(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareOrder softwareOrder = softwareOrderRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "소프트웨어 주문");
        model.addAttribute("work", work);
        model.addAttribute("softwareOrder", softwareOrder);
        model.addAttribute("backLink", "/order/software");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/order/software/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/order/software/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/sales/software_order";
    }
    @GetMapping(value="/order/software/update/workId={workId}")
    public String updateSoftwareOrderPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        SoftwareOrder softwareOrder = softwareOrderRepository.findByWork(work);

        model.addAttribute("headerTitle", "소프트웨어 주문 수정");
        model.addAttribute("work", work);
        model.addAttribute("softwareOrder", softwareOrder);
        model.addAttribute("backLink", "/order/software/workId=" + workId);
        model.addAttribute("link", "/order/software/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/sales/software_order";
    }
    @PostMapping(value="/order/software/update/workId={workId}")
    public String updateSoftwareOrder(@PathVariable("workId") Long workId, SoftwareOrderDto softwareOrderDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        SoftwareOrder softwareOrder = softwareOrderRepository.findByWork(work);

        softwareOrder.setOrderDate(softwareOrderDto.getOrderDate());
        softwareOrder.setProvideDate(softwareOrderDto.getProvideDate());
        softwareOrder.setPayment(softwareOrderDto.getPayment());
        softwareOrder.setStatus(softwareOrderDto.getStatus());
        softwareOrder.setStatement(softwareOrderDto.getStatement());

        softwareOrderRepository.save(softwareOrder);

        attributes.addFlashAttribute("alertData", "소프트웨어 주문을 수정하였습니다.");
        return "redirect:/order/software/workId=" + workId;
    }

    @GetMapping(value="/order/software/consider/workId={workId}")
    public String considerSoftwareOrderPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareOrder softwareOrder = softwareOrderRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("softwareOrder", softwareOrder);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/order/software/workId=" + workId);
        model.addAttribute("link", "/order/software/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/software_order";
    }
    @PostMapping(value="/order/software/consider/workId={workId}")
    public String considerSoftwareOrder(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/order/software/workId=" + workId;
    }

    // 소프트웨어 개발 주문
    @GetMapping(value="/order/develop")
    public String allDevelopOrder(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("개발 주문");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-주문-개발 주문");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/order/develop/insert");
        model.addAttribute("linkName", "주문");
        model.addAttribute("backLink", "/sales/work/order");
        model.addAttribute("detailedWorkLink", "/order/develop/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/order/develop/insert")
    public String createDevelopOrderPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "소프트웨어 개발 주문 추가");
        model.addAttribute("backLink", "/order/develop");
        model.addAttribute("link", "/order/develop/insert");
        model.addAttribute("isNew", true);

        return "/work/sales/develop_order";
    }

    @PostMapping(value="/order/develop/insert")
    public String createDevelopOrder(DevelopOrderDto developOrderDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("개발 주문");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                developOrderDto.getName(), developOrderDto.getType(), developOrderDto.getLocation(), developOrderDto.getContact()
        );

        DevelopQuotation developQuotation = developQuotationRepository.findByQuotationAndPatron(developOrderDto.getQuotation(), patron);

        String [] dateCode = developOrderDto.getOrderDate().toString().split("-");
        String orderCode = "DVO-" + dateCode[0] + dateCode[1] + dateCode[2] + "-" + developQuotation.getQuotation();

        DevelopOrder developOrder = new DevelopOrder();
        developOrder.setWork(work);
        developOrder.setDevelopQuotation(developQuotation);
        developOrder.setOrderCode(orderCode);
        developOrder.setOrderDate(developOrderDto.getOrderDate());
        developOrder.setProvideDate(developOrderDto.getProvideDate());
        developOrder.setPayment(developOrderDto.getPayment());
        developOrder.setStatus(developOrderDto.getStatus());
        developOrder.setStatement(developOrderDto.getStatement());

        developOrderRepository.save(developOrder);

        attributes.addFlashAttribute("alertData", "소프트웨어 개발 주문을 추가하였습니다.");
        return "redirect:/order/develop";
    }

    @GetMapping(value="/order/develop/workId={workId}")
    public String developOrder(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DevelopOrder developOrder = developOrderRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "개발 주문");
        model.addAttribute("work", work);
        model.addAttribute("developOrder", developOrder);
        model.addAttribute("backLink", "/order/develop");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/order/develop/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/order/develop/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/sales/develop_order";
    }
    @GetMapping(value="/order/develop/update/workId={workId}")
    public String updateDevelopOrderPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        SoftwareOrder softwareOrder = softwareOrderRepository.findByWork(work);

        model.addAttribute("headerTitle", "소프트웨어 개발 주문 수정");
        model.addAttribute("work", work);
        model.addAttribute("softwareOrder", softwareOrder);
        model.addAttribute("backLink", "/order/develop/workId=" + workId);
        model.addAttribute("link", "/order/develop/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/sales/develop_order";
    }
    @PostMapping(value="/order/develop/update/workId={workId}")
    public String updateDevelopOrder(@PathVariable("workId") Long workId, DevelopOrderDto developOrderDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        DevelopOrder developOrder = developOrderRepository.findByWork(work);

        developOrder.setOrderDate(developOrderDto.getOrderDate());
        developOrder.setProvideDate(developOrderDto.getProvideDate());
        developOrder.setPayment(developOrderDto.getPayment());
        developOrder.setStatus(developOrderDto.getStatus());
        developOrder.setStatement(developOrderDto.getStatement());

        developOrderRepository.save(developOrder);

        attributes.addFlashAttribute("alertData", "소프트웨어 개발 주문을 수정하였습니다.");
        return "redirect:/order/develop/workId=" + workId;
    }

    @GetMapping(value="/order/develop/consider/workId={workId}")
    public String considerDevelopOrderPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DevelopOrder developOrder = developOrderRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("developOrder", developOrder);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/order/develop/workId=" + workId);
        model.addAttribute("link", "/order/develop/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/develop_order";
    }
    @PostMapping(value="/order/develop/consider/workId={workId}")
    public String considerDevelopOrder(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/order/develop/workId=" + workId;
    }

    @GetMapping(value="/sales/work/software")
    public String provisionIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("소프트웨어 목록", "/software/list");
        works.put("소프트웨어 판매실적(일일)", "/software/daily");
        works.put("소프트웨어 판매실적(월간), 구현 예정", "/software/monthly");

        model.addAttribute("headerTitle", "영업부 업무-소프트웨어 관리");
        model.addAttribute("backLink", "/sales/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    // 소프트웨어 목록 : 추후 수정 예정, 조회만 가능
    @GetMapping(value="/software/list")
    public String allSoftware(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("제품 결과물");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-소프트웨어-소프트웨어 목록");
        model.addAttribute("paging", patron);
        model.addAttribute("backLink", "/sales/work/software");
        model.addAttribute("detailedWorkLink", "/software/list/workId=");
        model.addAttribute("isWork", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/software/list/workId={workId}")
    public String software(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        Software software= softwareRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "소프트웨어");
        model.addAttribute("work", work);
        model.addAttribute("software", software);
        model.addAttribute("backLink", "/software/list");

        return "/work/information_technology/software";
    }

    // 소프트웨어 판매실적(일일)
    @GetMapping(value="/software/daily")
    public String allSoftwareDailySales(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 판매실적");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-소프트웨어-소프트웨어 판매실적");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/software/daily/insert");
        model.addAttribute("linkName", "판매실적");
        model.addAttribute("backLink", "/sales/work/software");
        model.addAttribute("detailedWorkLink", "/software/daily/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/software/daily/insert")
    public String createSoftwareDailySalesPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "판매실적 추가");
        model.addAttribute("backLink", "/software/daily");
        model.addAttribute("link", "/software/daily/insert");
        model.addAttribute("isNew", true);

        return "/work/sales/software_sales";
    }

    @PostMapping(value="/software/daily/insert")
    public String createSoftwareDailySales(SoftwareSalesDto softwareSalesDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 판매실적");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        LocalDate salesDate = softwareSalesDto.getDevelopDate();
        salesDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Software software = softwareRepository.findBySoftwareNameAndSoftwareTypeAndDevelopDateAndPrice(
                softwareSalesDto.getSoftwareName(), softwareSalesDto.getSoftwareType(), softwareSalesDto.getDevelopDate(), softwareSalesDto.getPrice()
        );

        long total = softwareSalesDto.getQuantity() * software.getPrice();

        SoftwareSales softwareSales= new SoftwareSales();
        softwareSales.setWork(work);
        softwareSales.setSoftware(software);
        softwareSales.setSalesDate(salesDate);
        softwareSales.setQuantity(softwareSalesDto.getQuantity());
        softwareSales.setTotal(total);

        softwareSalesRepository.save(softwareSales);

        attributes.addFlashAttribute("alertData", "소프트웨어 판매실적을 추가하였습니다.");
        return "redirect:/software/daily";
    }

    @GetMapping(value="/software/daily/workId={workId}")
    public String softwareDailySales(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareSales softwareSales = softwareSalesRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "소프트웨어");
        model.addAttribute("work", work);
        model.addAttribute("softwareSales", softwareSales);
        model.addAttribute("backLink", "/software/daily");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/software/daily/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/software/daily/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/sales/software_sales";
    }
    @GetMapping(value="/software/daily/update/workId={workId}")
    public String updateSoftwareDailySalesPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        SoftwareSales softwareSales = softwareSalesRepository.findByWork(work);

        model.addAttribute("headerTitle", "소프트웨어 판매 실적 수정");
        model.addAttribute("work", work);
        model.addAttribute("softwareSales", softwareSales);
        model.addAttribute("backLink", "/software/daily/workId=" + workId);
        model.addAttribute("link", "/software/daily/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/sales/software_sales";
    }
    @PostMapping(value="/software/daily/update/workId={workId}")
    public String updateSoftwareDailySales(@PathVariable("workId") Long workId, SoftwareSalesDto softwareSalesDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        SoftwareSales softwareSales = softwareSalesRepository.findByWork(work);

        LocalDate salesDate = softwareSalesDto.getDevelopDate();
        salesDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Software software = softwareSales.getSoftware();

        long total = softwareSalesDto.getQuantity() * software.getPrice();

        softwareSales.setSalesDate(salesDate);
        softwareSales.setQuantity(softwareSalesDto.getQuantity());
        softwareSales.setTotal(total);

        attributes.addFlashAttribute("alertData", "소프트웨어 판매실적을 수정하였습니다.");
        return "redirect:/software/daily/workId=" + workId;
    }

    @GetMapping(value="/software/daily/consider/workId={workId}")
    public String considerSoftwareDailySalesPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareSales softwareSales = softwareSalesRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("softwareSales", softwareSales);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/software/daily/workId=" + workId);
        model.addAttribute("link", "/software/daily/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/software_sales";
    }
    @PostMapping(value="/software/daily/consider/workId={workId}")
    public String considerSoftwareDailySales(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/software/daily/workId=" + workId;
    }

    @GetMapping(value="/sales/work/bill")
    public String billIndex(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Map<String, String> works = new LinkedHashMap<>();
        works.put("소프트웨어 대금", "/bill/software");
        works.put("개발 대금", "/bill/develop");

        model.addAttribute("headerTitle", "영업부 업무-대금 청구");
        model.addAttribute("backLink", "/sales/work");
        model.addAttribute("works", works);

        return "/work/work_detail";
    }

    // 소프트웨어 대금
    @GetMapping(value="/bill/software")
    public String allSoftwareBill(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 대금");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-대금 청구-소프트웨어 대금");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/bill/software/insert");
        model.addAttribute("linkName", "대금 청구");
        model.addAttribute("backLink", "/bill/software/software");
        model.addAttribute("detailedWorkLink", "/bill/software/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/bill/software/insert")
    public String createSoftwareBillPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "대금 청구 추가");
        model.addAttribute("backLink", "/bill/software");
        model.addAttribute("link", "/bill/software/insert");
        model.addAttribute("isNew", true);

        return "/work/sales/software_bill";
    }

    @PostMapping(value="/bill/software/insert")
    public String createSoftwareBill(SoftwareBillDto softwareBillDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("소프트웨어 대금");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                softwareBillDto.getName(), softwareBillDto.getType(), softwareBillDto.getLocation(), softwareBillDto.getContact()
        );

        SoftwareOrder softwareOrder = softwareOrderRepository.findByOrderCodeAndOrderDate(softwareBillDto.getOrderCode(), softwareBillDto.getOrderDate());

        long rest = softwareOrder.getSoftwareQuotation().getTotalPrice() - softwareBillDto.getReceived();

        SoftwareBill softwareBill = new SoftwareBill();
        softwareBill.setWork(work);
        softwareBill.setPatron(patron);
        softwareBill.setSoftwareOrder(softwareOrder);
        softwareBill.setReceived(softwareBillDto.getReceived());
        softwareBill.setRest(rest);
        softwareBill.setReceiveStatement(softwareBillDto.getReceiveStatement());

        softwareBillRepository.save(softwareBill);

        attributes.addFlashAttribute("alertData", "소프트웨어 대금 청구가 완료되었습니다.");
        return "redirect:/bill/software";
    }

    @GetMapping(value="/bill/software/workId={workId}")
    public String softwareBill(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareBill softwareBill = softwareBillRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "소프트웨어 대금");
        model.addAttribute("work", work);
        model.addAttribute("softwareBill", softwareBill);
        model.addAttribute("backLink", "/bill/software");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/bill/software/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/bill/software/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/sales/software_bill";
    }
    @GetMapping(value="/bill/software/update/workId={workId}")
    public String updateSoftwareBillsPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        SoftwareBill softwareBill = softwareBillRepository.findByWork(work);

        model.addAttribute("headerTitle", "소프트웨어 대금 청구 수정");
        model.addAttribute("work", work);
        model.addAttribute("softwareBill", softwareBill);
        model.addAttribute("backLink", "/bill/software/workId=" + workId);
        model.addAttribute("link", "/bill/software/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/sales/software_bill";
    }
    @PostMapping(value="/bill/software/update/workId={workId}")
    public String updateSoftwareBill(@PathVariable("workId") Long workId, SoftwareBillDto softwareBillDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        SoftwareBill softwareBill = softwareBillRepository.findByWork(work);

        long rest = softwareBill.getSoftwareOrder().getSoftwareQuotation().getTotalPrice() - softwareBillDto.getReceived();

        softwareBill.setReceived(softwareBillDto.getReceived());
        softwareBill.setRest(rest);
        softwareBill.setReceiveStatement(softwareBillDto.getReceiveStatement());

        softwareBillRepository.save(softwareBill);

        attributes.addFlashAttribute("alertData", "소프트웨어 대금 청구 내역을 수정하였습니다.");
        return "redirect:/bill/software/workId=" + workId;
    }

    @GetMapping(value="/bill/software/consider/workId={workId}")
    public String considerSoftwareBillPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        SoftwareBill softwareBill = softwareBillRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("softwareBill", softwareBill);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/bill/software/workId=" + workId);
        model.addAttribute("link", "/bill/software/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/software_bill";
    }
    @PostMapping(value="/bill/software/consider/workId={workId}")
    public String considerSoftwareBill(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/bill/software/workId=" + workId;
    }

    // 개발 대금
    @GetMapping(value="/bill/develop")
    public String allDevelopBill(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        WorkType workType = workTypeRepository.findByWorkName("개발 대금");
        Page<Work> patron = workRepository.findAllByWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), workType);

        model.addAttribute("headerTitle", "영업부부 업무-대금 청구-개발 대금");
        model.addAttribute("paging", patron);
        model.addAttribute("link", "/bill/develop/insert");
        model.addAttribute("linkName", "대금 청구");
        model.addAttribute("backLink", "/bill/develop/software");
        model.addAttribute("detailedWorkLink", "/bill/develop/workId=");
        model.addAttribute("isWork", true);
        model.addAttribute("isRequest", true);

        return "/work/work_detail_list";
    }

    @GetMapping(value="/bill/develop/insert")
    public String createDevelopBillPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("draftStaff", staff);
        model.addAttribute("headerTitle", "대금 청구 추가");
        model.addAttribute("backLink", "/bill/develop");
        model.addAttribute("link", "/bill/develop/insert");
        model.addAttribute("isNew", true);

        return "/work/sales/develop_bill";
    }

    @PostMapping(value="/bill/develop/insert")
    public String createDevelopBill(DevelopBillDto developBillDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("개발 대금");
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Work work = new Work();
        work.setWorkType(workType);
        work.setDraftStaff(staff);
        work.setDraftState(WorkState.PENDING);
        workRepository.save(work);

        Patron patron = patronRepository.findByNameAndTypeAndLocationAndContact(
                developBillDto.getName(), developBillDto.getType(), developBillDto.getLocation(), developBillDto.getContact()
        );

        DevelopOrder developOrder = developOrderRepository.findByOrderCodeAndOrderDate(developBillDto.getOrderCode(), developBillDto.getOrderDate());

        long rest = developOrder.getDevelopQuotation().getDevelopCost() - developBillDto.getReceived();

        DevelopBill developBill = new DevelopBill();
        developBill.setWork(work);
        developBill.setDevelopOrder(developOrder);
        developBill.setReceived(developBillDto.getReceived());
        developBill.setRest(rest);
        developBill.setReceiveStatement(developBillDto.getReceiveStatement());

        developBillRepository.save(developBill);

        attributes.addFlashAttribute("alertData", "개발 대금 청구가 완료되었습니다.");
        return "redirect:/bill/develop";
    }

    @GetMapping(value="/bill/develop/workId={workId}")
    public String developBill(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DevelopBill developBill = developBillRepository.findByWork(work);
        Staff curStaff = staffRepository.findByUserId(principal.getName());
        Staff reqStaff = work.getDraftStaff();

        model.addAttribute("headerTitle", "개발 대금");
        model.addAttribute("work", work);
        model.addAttribute("developBill", developBill);
        model.addAttribute("backLink", "/bill/develop");
        if(curStaff.getUserId().equals(reqStaff.getUserId())){
            model.addAttribute("link", "/bill/develop/update/workId=" + workId);
        }
        else{
            model.addAttribute("link", "/bill/develop/consider/workId=" + workId);
        }
        model.addAttribute("isUpdatable", true);

        return "/work/sales/develop_bill";
    }
    @GetMapping(value="/bill/develop/update/workId={workId}")
    public String updateDevelopBillsPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        DevelopBill developBill = developBillRepository.findByWork(work);

        model.addAttribute("headerTitle", "개발 대금 청구 수정");
        model.addAttribute("work", work);
        model.addAttribute("developBill", developBill);
        model.addAttribute("backLink", "/bill/develop/workId=" + workId);
        model.addAttribute("link", "/bill/develop/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work/sales/develop_bill";
    }
    @PostMapping(value="/bill/develop/update/workId={workId}")
    public String updateDevelopBill(@PathVariable("workId") Long workId, DevelopBillDto developBillDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        DevelopBill developBill = developBillRepository.findByWork(work);

        long rest = developBill.getDevelopOrder().getDevelopQuotation().getDevelopCost() - developBillDto.getReceived();

        developBill.setReceived(developBillDto.getReceived());
        developBill.setRest(rest);
        developBill.setReceiveStatement(developBillDto.getReceiveStatement());

        developBillRepository.save(developBill);

        attributes.addFlashAttribute("alertData", "소프트웨어 대금 청구 내역을 수정하였습니다.");
        return "redirect:/bill/develop/workId=" + workId;
    }

    @GetMapping(value="/bill/develop/consider/workId={workId}")
    public String considerDevelopBillPage(@PathVariable("workId") Long workId, Model model, Principal principal){
        Work work = workRepository.findByWorkId(workId);
        DevelopBill developBill = developBillRepository.findByWork(work);
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("work", work);
        model.addAttribute("developBill", developBill);
        model.addAttribute("considerStaff", staff);
        model.addAttribute("backLink", "/bill/develop/workId=" + workId);
        model.addAttribute("link", "/bill/develop/consider/workId=" + workId);
        model.addAttribute("isSecondLevelApproval", true);

        return "/work/sales/develop_bill";
    }
    @PostMapping(value="/bill/develop/consider/workId={workId}")
    public String considerDevelopBill(@PathVariable("workId") Long workId, WorkDto workDto, Principal principal, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        workRepository.save(work);

        return "redirect:/bill/develop/workId=" + workId;
    }

}