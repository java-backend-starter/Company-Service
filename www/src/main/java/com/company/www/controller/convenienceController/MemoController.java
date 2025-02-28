package com.company.www.controller.convenienceController;

import com.company.www.constant.staff.MemoStatus;
import com.company.www.constant.staff.MemoType;
import com.company.www.dto.convenience.MemoDto;
import com.company.www.entity.convenience.Memo;
import com.company.www.entity.staff.Staff;
import com.company.www.repository.convenience.MemoRepository;
import com.company.www.repository.staff.StaffRepository;
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

@Controller
public class MemoController {
    @Autowired
    StaffInfoService staffInfoService;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    MemoRepository memoRepository;

    @GetMapping(value="/memo")
    public String memoIndex(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAll(PageRequest.of(page, 10));
        model.addAttribute("headerTitle", "메모");
        model.addAttribute("backLink", "/");

        return "/memo/memo";
    }

    @GetMapping(value="/memo/memoId={memoId}")
    public String memo(@PathVariable("memoId") Long memoId, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        Memo memo = memoRepository.findByMemoId(memoId);

        model.addAttribute("memo", memo);
        return "/memo/memo_content";
    }

    @GetMapping(value="/memo/write")
    public String memoWrite(Model model){
        model.addAttribute("headerTitle", "메모 작성");
        return "/memo/memo_write";
    }

    @PostMapping(value="/memo/write/create")
    public String createMemo(MemoDto memoDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Memo memo = new Memo();
        memo.setTitle(memoDto.getTitle());
        memo.setContent(memoDto.getContent());
        memo.setMemoStatus(getStatus(memoDto.getMemoStatus()));
        memo.setMemoType(getType(memoDto.getMemoType()));
        memo.setDate(date);

        memoRepository.save(memo);
        attributes.addFlashAttribute("alertData", "메모 작성이 완료되었습니다.");
        return "redirect:/memo";
    }
    private MemoType getType(String t){
        String type = t.toLowerCase();
        switch (type){
            case "업무", "work" -> {return MemoType.WORK;}
            case "일정", "schedule" -> {return MemoType.SCHEDULE;}
            case "기타", "and so on", "and_so_on" -> {return MemoType.AND_SO_ON;}
            default -> {return null;}
        }
    }
    private MemoStatus getStatus(String sta){
        String status = sta.toLowerCase();
        switch (status){
            case "예정", "plan" -> {return MemoStatus.PLAN;}
            case "보류", "delay" -> {return MemoStatus.DELAY;}
            case "진행", "proceed" -> {return MemoStatus.PROCEED;}
            case "완료", "complete" -> {return MemoStatus.COMPLETE;}
            default -> {return null;}
        }
    }

    @GetMapping(value="/memo/update/memoId={memoId}")
    public String updateMemoPage(@PathVariable("memoId") Long memoId, Model model){

        return "/memo/memo_write";
    }

    @GetMapping(value="/memo/all")
    public String allMemo(){

        return "redirect:/memo";
    }

    @GetMapping(value="/memo/work")
    public String memoWork(@RequestParam(value="page", defaultValue="0") int page,  Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAllByMemoType(PageRequest.of(page, 10), MemoType.WORK);

        model.addAttribute("headerTitle", "메모-업무");
        model.addAttribute("paging", memo);
        return "/memo/memo";
    }

    @GetMapping(value="/memo/schedule")
    public String memoSchedule(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAllByMemoType(PageRequest.of(page, 10), MemoType.SCHEDULE);
        model.addAttribute("headerTitle", "메모-일정");
        model.addAttribute("paging", memo);
        return "/memo/memo";
    }

    @GetMapping(value="/memo/and_so_on")
    public String memoAndSoOn(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAllByMemoType(PageRequest.of(page, 10), MemoType.AND_SO_ON);
        model.addAttribute("headerTitle", "메모-기타");
        model.addAttribute("paging", memo);
        return "/memo/memo";
    }

    @GetMapping(value="/memo/plan")
    public String memoPlan(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAllByMemoStatus(PageRequest.of(page, 10), MemoStatus.PLAN);
        model.addAttribute("headerTitle", "메모-예정");
        model.addAttribute("paging", memo);
        return "/memo/memo";
    }

    @GetMapping(value="/memo/delay")
    public String memoDelay(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAllByMemoStatus(PageRequest.of(page, 10), MemoStatus.DELAY);
        model.addAttribute("headerTitle", "메모-보류");
        model.addAttribute("paging", memo);
        return "/memo/memo";
    }

    @GetMapping(value="/memo/proceed")
    public String memoProceed(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAllByMemoStatus(PageRequest.of(page, 10), MemoStatus.PROCEED);
        model.addAttribute("headerTitle", "메모-진행");
        model.addAttribute("paging", memo);
        return "/memo/memo";
    }

    @GetMapping(value="/memo/complete")
    public String memoComplete(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        Page<Memo> memo = memoRepository.findAllByMemoStatus(PageRequest.of(page, 10), MemoStatus.COMPLETE);
        model.addAttribute("headerTitle", "메모-완료");
        model.addAttribute("paging", memo);
        return "/memo/memo";
    }
}