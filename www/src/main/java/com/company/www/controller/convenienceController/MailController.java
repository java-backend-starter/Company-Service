package com.company.www.controller.convenienceController;

import com.company.www.constant.staff.MailType;
import com.company.www.dto.convenience.MailDto;
import com.company.www.entity.convenience.Mail;
import com.company.www.entity.staff.Staff;
import com.company.www.repository.convenience.MailRepository;
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
public class MailController {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    MailRepository mailRepository;
    @Autowired
    StaffInfoService staffInfoService;

    @GetMapping(value="/mail")
    public String mailIndex(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        Staff staff = staffRepository.findByUserId(principal.getName());

        Page<Mail> paging = mailRepository.findAllBySendStaffOrReceiveStaff(PageRequest.of(page, 10), staff, staff);
        model.addAttribute("paging", paging);
        model.addAttribute("headerTitle", "메일");
        model.addAttribute("backLink", "/");
        model.addAttribute("writeLink", "/mail/write");

        return "/mail/mail";
    }

    @GetMapping(value="/mail/write")
    public String mailWrite(Model model){
        model.addAttribute("headerTitle", "메일 작성");

        return "/mail/mail_write";
    }

    @PostMapping(value="/mail/write/create")
    public String createMail(MailDto mailDto, Principal principal, RedirectAttributes attributes){
        Staff sendStaff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Mail sendMail = new Mail();
        sendMail.setTitle(mailDto.getTitle());
        sendMail.setContent(mailDto.getContent());
        sendMail.setSendDeleted(false);
        sendMail.setReceiveDeleted(false);
        sendMail.setSend(date);
        sendMail.setSendStaff(sendStaff);
        if(mailDto.isForMe()){
            sendMail.setReceiveStaff(sendStaff);
            sendMail.setMailType(MailType.FOR_ME);
            mailRepository.save(sendMail);
            attributes.addFlashAttribute("alertData", "나에게 보내기 메일을 작성하였습니다.");
        }
        else {
            Staff receiveStaff = staffRepository.findByUserId(mailDto.getReceiveStaff());
            sendMail.setMailType(MailType.SEND);
            sendMail.setReceiveStaff(receiveStaff);
            mailRepository.save(sendMail);
            attributes.addFlashAttribute("alertData", "메일 작성이 완료되었습니다.");
        }
        return "redirect:/mail";
    }

    @GetMapping(value="/mail/mailId={mailId}")
    public String detailedMail(@PathVariable("mailId") Long mailId, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        Mail mail = mailRepository.findByMailId(mailId);

        model.addAttribute("headerTitle", "메일 열람");
        model.addAttribute("mail", mail);
        return "/mail/mail_content";
    }

    @GetMapping(value="/mail/all")
    public String allMail(){

        return "redirect:/mail";
    }

    @GetMapping(value="/mail/send")
    public String mailSend(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        Staff sandStaff = staffRepository.findByUserId(principal.getName());

        Page<Mail> paging = mailRepository.findAllBySendStaffAndMailType(PageRequest.of(page, 10), sandStaff, MailType.SEND);
        model.addAttribute("paging", paging);
        model.addAttribute("headerTitle", "메일");
        model.addAttribute("writeLink", "/mail/write");

        return "/mail/mail";
    }

    @GetMapping(value="/mail/receive")
    public String mailReceive(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        Staff receiveStaff = staffRepository.findByUserId(principal.getName());

        Page<Mail> paging = mailRepository.findAllByReceiveStaffAndMailType(PageRequest.of(page, 10), receiveStaff, MailType.RECEIVE);
        model.addAttribute("paging", paging);
        model.addAttribute("headerTitle", "메일");
        model.addAttribute("writeLink", "/mail/write");

        return "/mail/mail";
    }

    @GetMapping(value="/mail/for_me")
    public String mailForMe(@RequestParam(value="page", defaultValue="0") int page, Model model, Principal principal){
        staffInfoService.getInfo(model, principal);
        Staff staff = staffRepository.findByUserId(principal.getName());
        Page<Mail> paging = mailRepository.findAllBySendStaffAndMailType(PageRequest.of(page, 10), staff, MailType.FOR_ME);
        model.addAttribute("paging", paging);
        model.addAttribute("headerTitle", "메일");
        model.addAttribute("writeLink", "/mail/write");

        return "/mail/mail";
    }
}