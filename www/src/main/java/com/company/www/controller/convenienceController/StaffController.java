package com.company.www.controller.convenienceController;


import com.company.www.constant.work.ApprovalState;
import com.company.www.constant.work.WorkState;
import com.company.www.converter.Converter;
import com.company.www.dto.staff.StaffDto;
import com.company.www.dto.work.human_resource.CommuteDto;
import com.company.www.dto.work.human_resource.HolidayDto;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Commute;
import com.company.www.entity.work.human_reasource.Holiday;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.work.WorkType;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.*;
import com.company.www.repository.work.human_resource.CommuteRepository;
import com.company.www.repository.work.human_resource.HolidayRepository;
import com.company.www.service.staff.StaffInfoService;
import com.company.www.service.staff.StaffService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Controller
public class StaffController {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    StaffService staffService;
    @Autowired
    StaffInfoService staffInfoService;

    @Autowired
    WorkRepository workRepository;
    @Autowired
    WorkTypeRepository workTypeRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    CommuteRepository commuteRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping(value="/login")
    public String loginPage(Model model, Principal principal){
        return "/main/login";
    }

    @GetMapping(value="/login/error")
    public String loginError(Model model){
        model.addAttribute("alertData", "아이디 또는 비밀번호를 확인하세요");
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인하세요");
        return "/main/index";
    }
    @GetMapping(value="/signup")
    public String signupPage(Model model){
        LocalDate time = LocalDate.now(ZoneId.of("Asia/Seoul"));
        model.addAttribute("time", time.toString());
        model.addAttribute("staffDto", new StaffDto());
        return "/main/signup";
    }

    @PostMapping(value="/signup")
    public String newMember(@ModelAttribute @Valid StaffDto staffDto, BindingResult bindingResult,
                            Model model, RedirectAttributes attributes){
        if(bindingResult.hasErrors()){
            attributes.addFlashAttribute("alertData", "회원가입에 실패하였습니다. 회원정보를 확인하세요.");
            return "main/signup";
        }

        try{
            Staff staff = staffService.createStaff(staffDto, passwordEncoder);
            String name = staff.getStaffName();
            attributes.addFlashAttribute("alertData", name + "님의 회원가입이 완료되었습니다.");
        } catch(IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "main/signup";
        }

        return "redirect:/";
    }

    @GetMapping(value="/staff_info")
    public String updateUserInfoPage(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        model.addAttribute("staff", staff);
        return "/main/staff_info";
    }

    @PostMapping(value="/staff_info")
    public String updateUserInfo(StaffDto staffDto, Principal principal, RedirectAttributes attributes){
        if(staffDto.getPassword().equals(staffDto.getPassword_confirm())) {
            Long staffId = staffRepository.findByUserId(principal.getName()).getStaffId();
            staffService.updateStaff(staffId, staffDto, false, passwordEncoder);
            attributes.addFlashAttribute("alertData", "정보 수정이 완료되었습니다.");
            return "redirect:/";
        }
        else{
            attributes.addFlashAttribute("alertData", "비밀번호가 일치하지 않습니다.");
            return "redirect:/staff_info";
        }
    }

}