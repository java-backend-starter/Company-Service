package com.company.www.controller.workController;

import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.dto.work.human_resource.CommuteDto;
import com.company.www.dto.work.human_resource.HolidayDto;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.human_reasource.Commute;
import com.company.www.entity.work.human_reasource.Holiday;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.repository.work.human_resource.CommuteRepository;
import com.company.www.repository.work.human_resource.HolidayRepository;
import com.company.www.service.staff.StaffService;
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

@Controller
public class CommonWorkController {
    @Autowired
    StaffRepository staffRepository;

    @Autowired
    StaffService staffService;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    WorkTypeRepository workTypeRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    CommuteRepository commuteRepository;

    @GetMapping(value="/holiday")
    public String allRequestHoliday(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffService.getInfo(model, principal);

        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("휴가관리");
        Page<Work> holidays = workRepository.findAllByDraftStaffAndWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), staff, workType);

        model.addAttribute("headerTitle", "휴가");
        model.addAttribute("paging", holidays);
        model.addAttribute("name", staff.getStaffName());
        model.addAttribute("requestLink", "/holiday/request/workId=");
        model.addAttribute("backLink", "/");
        model.addAttribute("link", "/holiday/request");
        model.addAttribute("linkName", "휴가 신청서 작성");
        model.addAttribute("isRequest", true);

        return "/work/work";
    }

    @GetMapping(value="/holiday/request")
    public String requestHolidayPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("headerTitle", "휴가 신청");
        model.addAttribute("draftStaff", staff);
        model.addAttribute("backLink", "/holiday");
        model.addAttribute("isNew", true);

        return "/work/human_resource/holiday";
    }
    @PostMapping(value="/holiday/request")
    public String requestHoliday(HolidayDto holidayDto, RedirectAttributes attributes){
        if(holidayDto.getStartDate() != null && holidayDto.getEndDate() != null){
            Holiday holiday = new Holiday();
            Work work = new Work();

            Staff requestStaff = staffRepository.findByStaffName(holidayDto.getStaff());
            WorkType workType = workTypeRepository.findByWorkName("휴가관리");
            LocalDate requestDate = LocalDate.now();
            requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            work.setWorkType(workType);
            work.setDraftStaff(requestStaff);
            work.setDraftState(WorkState.PENDING);
            workRepository.save(work);

            holiday.setHolidayType(Converter.convertToHolidayType(holidayDto.getHolidayType()));
            holiday.setStartDate(holidayDto.getStartDate());
            holiday.setEndDate(holidayDto.getEndDate());
            if(holidayDto.getHalf().equals("Y")){
                holiday.setHalf("Y");
            }
            else if(holidayDto.getHalf().isEmpty() || holidayDto.getHalf().equals("N")){
                holiday.setHalf("N");
            }
            holiday.setReason(holidayDto.getReason());
            holiday.setWork(work);
            holidayRepository.save(holiday);

            attributes.addFlashAttribute("alertData", "휴가 신청이 완료되었습니다.");
            return "redirect:/holiday";
        }
        else{
            attributes.addFlashAttribute("alertDate", "휴가날짜를 다시 입력하세요");
            return "redirect:/holiday/request";
        }
    }

    @GetMapping(value="/holiday/request/workId={workId}")
    public String detailedRequestHoliday(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Holiday holiday = holidayRepository.findByWork(work);

        model.addAttribute("headerTitle", "휴가 신청 내용");
        model.addAttribute("holiday", holiday);
        model.addAttribute("work", work);
        model.addAttribute("link", "/holiday/request/update/workId=" + workId);
        model.addAttribute("backLink", "/holiday");
        if(work.getDraftState().equals(WorkState.PENDING)){
            model.addAttribute("isUpdatable", true);
        }

        return "/work/human_resource/holiday";
    }

    @GetMapping(value="/holiday/request/update/workId={workId}")
    public String updateRequestHolidayPage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);
        Holiday holiday = holidayRepository.findByWork(work);

        model.addAttribute("headerTitle", "휴가 신청 내용");
        model.addAttribute("holiday", holiday);
        model.addAttribute("work", work);
        model.addAttribute("link", "/holiday/request/update/workId=" + workId);
        model.addAttribute("backLink", "/holiday");
        model.addAttribute("isUpdate", true);

        return "/work/human_resource/holiday";
    }

    @PostMapping(value="/holiday/request/update/workId={workId}")
    public String updateRequestHoliday(@PathVariable("workId") Long workId, HolidayDto holidayDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Holiday holiday = holidayRepository.findByWork(work);

        holiday.setHolidayType(Converter.convertToHolidayType(holidayDto.getHolidayType()));
        holiday.setHalf(holidayDto.getHalf());
        holiday.setReason(holidayDto.getReason());
        holiday.setStartDate(holidayDto.getStartDate());
        holiday.setEndDate(holidayDto.getEndDate());
        holidayRepository.save(holiday);

        attributes.addFlashAttribute("alertData","수정이 완료되었습니다.");
        return "redirect:/holiday/request/workId=" + workId;
    }

    /*
    @GetMapping(value="/commute")
    public String allRequestCommute(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffService.getInfo(model, principal);

        Staff staff = staffRepository.findByUserId(principal.getName());
        WorkType workType = workTypeRepository.findByWorkName("근태기록");
        Page<Work> paging = workRepository.findAllByDraftStaffAndWorkTypeOrderByWorkIdDesc(PageRequest.of(page, 10), staff, workType);
        model.addAttribute("headerTitle", "출퇴근 기록");
        model.addAttribute("paging", paging);
        model.addAttribute("backLink", "/");
        model.addAttribute("requestLink", "/commute/request/workId=");
        model.addAttribute("link", "/commute/request");
        model.addAttribute("linkName", "출퇴근 기록 작성");
        model.addAttribute("isRequest", true);
        model.addAttribute("isCommute", true);

        return "/work/work";
    }

    @GetMapping(value="/commute/request")
    public String requestCommutePage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        model.addAttribute("headerTitle", "출퇴근 기록");
        model.addAttribute("draftStaff", staff);
        model.addAttribute("backLink", "/commute");
        model.addAttribute("isNew", true);
        return "/work/human_resource/commute";
    }

    @PostMapping(value="/commute/request")
    public String requestCommute(CommuteDto commuteDto, Model model, RedirectAttributes attributes){
        if(isClockValid(commuteDto, "clockIn")){
            Commute commute = new Commute();
            Work work = new Work();

            Staff staff = staffRepository.findByStaffName(commuteDto.getStaff());
            WorkType workType = workTypeRepository.findByWorkName("근태기록");

            LocalDate requestDate = LocalDate.now();
            requestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            work.setWorkType(workType);
            work.setDraftStaff(staff);
            work.setDraftState(WorkState.PENDING);
            workRepository.save(work);

            commute.setStaff(staff);
            commute.setCommuteDate(requestDate);
            commute.setClockIn(commuteDto.getClockIn());
            commute.setStatement(commuteDto.getCommuteStatement());
            commute.setWorkType(workType);
            commuteRepository.save(commute);

            attributes.addFlashAttribute("alertData", "출퇴근 기록이 완료되었습니다.");
            return "redirect:/commute";
        }
        else{
            attributes.addFlashAttribute("alertData", "출근 시간을 입력하세요.");
            return "redirect:/commute/request";
        }
    }
    private boolean isClockValid(CommuteDto commuteDto, String name){
        if(name.equals("clockIn")){
            return (commuteDto.getClockIn() != null);
        }
        else if(name.equals("clockOut")){
            return (commuteDto.getClockOut() != null);
        }
        else{
            return false;
        }
    }

    @GetMapping(value="/commute/request/workId={workId}")
    public String detailedRequestCommute(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);

        model.addAttribute("headerTitle", "출퇴근 기록-");
        model.addAttribute("work", work);
        model.addAttribute("commute", commute);

        model.addAttribute("backLink", "/commute");
        model.addAttribute("link", "/commute/request/update/workId="+workId);
        if(!work.getDraftState().equals(WorkState.PENDING)){
            model.addAttribute("isUpdatable", true);
        }

        return "/work//human_resource/commute";
    }

    @GetMapping(value="/commute/request/update/workId={workId}")
    public String updateRequestCommutePage(@PathVariable("workId") Long workId, Model model){
        Work work = workRepository.findByWorkId(workId);

        model.addAttribute("headerTitle", "출퇴근 기록");
        model.addAttribute("commute", commute);

        model.addAttribute("work", work);
        model.addAttribute("backLink", "/commute/request/workId=" + workId);
        model.addAttribute("link", "/commute/request/update/workId=" + workId);
        model.addAttribute("isUpdate", true);

        return "/work//human_resource/commute";
    }

    @PostMapping(value="/commute/request/update/workId={workId}")
    public String updateRequestCommute(@PathVariable("workId") Long workId, CommuteDto commuteDto, RedirectAttributes attributes){
        Work work = workRepository.findByWorkId(workId);
        Commute commute = commuteRepository.findByWork(work);

        commute.setClockIn(commuteDto.getClockIn());
        if(isClockValid(commuteDto, "clockOut")) {
            commute.setClockOut(commuteDto.getClockOut());
        }
        commute.setStatement(commuteDto.getCommuteStatement());
        commuteRepository.save(commute);

        attributes.addFlashAttribute("alertData", "수정이 완료되었습니다.");

        return "redirect:/commute/request/workId=" + workId;
    }
    */
}
