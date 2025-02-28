package com.company.www.controller;

import com.company.www.constant.post.PostType;
import com.company.www.constant.staff.Gender;
import com.company.www.dto.work.account.TestDataDto;
import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Role;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.entity.post.Post;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.WorkType;
import com.company.www.entity.work.account.TestData;
import com.company.www.entity.work.human_reasource.Holiday;
import com.company.www.repository.belong.*;
import com.company.www.repository.work.WorkRepository;
import com.company.www.repository.work.account.TestDataRepository;
import com.company.www.repository.work.human_resource.HolidayRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.post.PostRepository;
import com.company.www.repository.work.WorkTypeRepository;
import com.company.www.service.staff.StaffInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    PositionRepository positionRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StaffInfoService staffInfoService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    HolidayRepository holidayRepository;
    @Autowired
    WorkTypeRepository workTypeRepository;
    @Autowired
    WorkRepository workRepository;
    @Autowired
    TestDataRepository testDataRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping(value="/test")
    public String testIndex(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        List<String> statuses = List.of("자산취득", "자산수선", "자산매각", "자산페기", "자산이동");
        List<String> depreciationMethods = List.of("정액법", "정률법");

        model.addAttribute("requestStaff", staff);
        model.addAttribute("statuses", statuses);
        model.addAttribute("depreciationMethods", depreciationMethods);
        model.addAttribute("isNew", true);
        return "/work/account/fixed_asset";
    }

    @GetMapping(value="/test2")
    public String test2Index(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        Map<String, String> val = new LinkedHashMap<>();
        val.put("1", "1");
        val.put("2", "2");
        val.put("3", "3");

        model.addAttribute("requestStaff", staff);
        model.addAttribute("link", "/test2");
        model.addAttribute("isNew", true);
        model.addAttribute("vals", val);
        return "/work/account/test2";
    }
    @PostMapping(value="/test2")
    public String insertTest2(TestDataDto testDataDto, Principal principal, RedirectAttributes attributes){
        Staff staff = staffRepository.findByUserId(principal.getName());
        LocalDate date = LocalDate.now();

        Work work = new Work();
        work.setDraftStaff(staff);
        workRepository.save(work);


        TestData testData = new TestData();
        testData.setData(testDataDto.getData());
        testDataRepository.save(testData);

        attributes.addFlashAttribute("alertData", "입력 성공");
        return "redirect:/";
    }

    @GetMapping(value="/test2/{id}")
    public String test2Data(@PathVariable("id") Long id, Model model){
        TestData testData = testDataRepository.findByTestDataId(id);
        model.addAttribute("isUpdatable", true);
        model.addAttribute("link", "/test2/update/"+id);
        model.addAttribute("data", testData.getData());
        return "/work/account/test2";
    }

    @GetMapping(value="/test2/update/{id}")
    public String updateTest2DataPage(@PathVariable("id") Long id, Model model){
        TestData testData = testDataRepository.findByTestDataId(id);

        Map<String, String> val = new LinkedHashMap<>();
        val.put("1", "1");
        val.put("2", "2");
        val.put("3", "3");

        model.addAttribute("isUpdate", true);
        model.addAttribute("link", "/test2/update/"+id);
        model.addAttribute("data", testData.getData());
        model.addAttribute("vals", val);
        return "/work/account/test2";
    }
    @PostMapping(value="/test2/update/{id}")
    public String updateTest2Data(@PathVariable("id") Long id, TestDataDto testDataDto, RedirectAttributes attributes){
        TestData testData = testDataRepository.findByTestDataId(id);
        testData.setData(testDataDto.getData());
        testDataRepository.save(testData);
        attributes.addFlashAttribute("alertData", "수정완료");
        return "redirect:/test2/"+id;
    }

    @GetMapping(value="/test3")
    public String test3(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("requestStaff", staff);
        model.addAttribute("isNew", true);
        String [] tags = new String [] {
                "<td class='width-12'><button type='button' class='inner-width form-control delete-menu' onclick='deleteMenuEvent(this)'>행 삭제</button></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='type' th:value='${equityChange?.equityChange?.type}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='capital' th:value='${equityChange?.equityChange?.capital}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='surplus' th:value='${equityChange?.equityChange?.surplus}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='adjustment' th:value='${equityChange?.equityChange?.adjustment}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='unappropriated' th:value='${equityChange?.equityChange?.unappropriated}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='comprehensive' th:value='${equityChange?.equityChange?.comprehensive}'></td>",
                "<td class='width-12'><input type='text' class='inner-width form-control' name='total' th:value='${equityChange?.equityChange?.total}'></td>"
        };
        model.addAttribute("tags", tags);
        return "/work/account/equity_change_statement";
    }


    @GetMapping(value="/")
    public String index(Model model, Principal principal){

        if(staffRepository.findAll().isEmpty()){
            createStaff("홍길동", "ascia3", "asdf1234", "ascia33@naver.com", "사장", "경영진", "경영진", "경영진");
            createStaff("허준", "ascia4", "asdf1234", "ascia44@naver.com", "부사장", "경영진", "경영진", "경영진");
            createStaff("김인사", "ascia5", "asdf1234", "ascia55@naver.com", "부장", "인사", "인사부", "미정");
            createStaff("이재무", "ascia6", "asdf1234", "ascia66@naver.com", "부장", "재무회계", "재무회계부", "미정");
        }

        if(principal != null){
            Staff staff = staffRepository.findByUserId(principal.getName());
            staffInfoService.getInfo(model, principal);
            List<Holiday> holiday = null;
            List<Holiday> family = null;
        }
        Role role = roleRepository.findByRoleName("미정");
        List<Post> notice = postRepository.findAllByPostTypeAndRoleOrderByPostIdDesc(PostType.NOTICE, role);
        List<Post> free = postRepository.findAllByPostTypeAndRoleOrderByPostIdDesc(PostType.NORMAL, role);

        if(principal == null){
            String message = "로그인 후 확인할 수 있습니다.";
            model.addAttribute("notice_title", message);
            model.addAttribute("free_title", message);
            model.addAttribute("holiday_title", message);
            model.addAttribute("family_title", message);

            return "/main/index";
        }
        else if(notice.isEmpty() && free.isEmpty()){
            String message = "데이터가 없습니다.";
            model.addAttribute("notice_title", message);
            model.addAttribute("free_title", message);
            model.addAttribute("holiday_title", message);
            model.addAttribute("family_title", message);
        }
        else{
            String message = "데이터가 없습니다.";
            if(notice.isEmpty()) {
                model.addAttribute("notice_title", message);
            }
            else{
                model.addAttribute("notice_title", notice.get(0).getTitle());
            }
            if(free.isEmpty()) {
                model.addAttribute("free_title", message);
            }
            else {
                model.addAttribute("free_title", free.get(0).getTitle());
            }
            /*
            if(holiday.isEmpty()){
                model.addAttribute("holiday_title", message);
            }
            else{
                model.addAttribute("holiday_title", holiday.get(0).getTitle());
            }
            if(family.isEmpty()) {
                model.addAttribute("family_title", message);
            }
            else{
                model.addAttribute("family_title", family.get(0).getTitle());
            }
             */
        }

        return "/main/index";
    }

    @GetMapping(value="/directors/work")
    public String allWork(Model model, Principal principal){
        staffInfoService.getInfo(model, principal);

        List<WorkType> workTypes = workTypeRepository.findAllByWorkNameEndingWith("업무");
        model.addAttribute("headerTitle", "경영진 업무");
        model.addAttribute("workType", workTypes);
        model.addAttribute("isWork", true);

        return "/work/work";
    }

    synchronized void createStaff(String name, String id, String pw, String email, String p, String r, String d, String s){
        Staff staff = new Staff();

        LocalDate birthday = LocalDate.of(1997, 1, 1);
        birthday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate start = LocalDate.of(2020, 1, 1);
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        staff.setUserId(id);
        staff.setPassword(passwordEncoder.encode(pw));

        staff.setStaffName(name);
        staff.setGender(Gender.MALE);
        staff.setBirthDate(birthday);
        staff.setEmail(email);
        staff.setAddress("주소");
        staff.setContact("연락처");
        staff.setEmployDate(start);
        staff.setResign(null);

        staff.setRole(roleRepository.findByRoleName(r));
        staff.setPosition(positionRepository.findByPositionName(p));
        staff.setDepartment(departmentRepository.findByDepartmentName(d));
        staff.setSection(sectionRepository.findBySectionName(s));

        Staff created = staffRepository.save(staff);
        if(!d.equals("미정")){
            Department department = departmentRepository.findByDepartmentName(d);
            List<Staff> dStaff = department.getStaff();
            dStaff.add(created);
            department.setStaff(dStaff);
            departmentRepository.save(department);
        }
        if(!s.equals("미정")){
            Section section = sectionRepository.findBySectionName(s);
            List<Staff> sStaff = section.getStaff();
            sStaff.add(created);
            section.setStaff(sStaff);
            sectionRepository.save(section);
        }
        staffRepository.save(staff);
    }
}