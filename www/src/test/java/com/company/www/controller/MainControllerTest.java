package com.company.www.controller;

import com.company.www.constant.staff.Gender;
import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.staff.Staff;
import com.company.www.repository.belong.*;
import com.company.www.repository.staff.StaffRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
class MainControllerTest {

    @Autowired
    StaffRepository staffRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    Staff createStaff(String name, String id, String pw, String email, String p, String r, String d, String s){
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
        staff.setEmployDate(start);
        // staff.setResignDate(null);
        // staff.setCheckResign("미대상");

        staff.setRole(roleRepository.findByRoleName(r));
        staff.setPosition(positionRepository.findByPositionName(p));
        staff.setDepartment(departmentRepository.findByDepartmentName(d));
        staff.setSection(sectionRepository.findBySectionName(s));

        if(!d.equals("미정")){
            Department department = departmentRepository.findByDepartmentName(d);
            List<Staff> staffs = department.getStaff();
            staffs.add(staff);
            department.setStaff(staffs);
            departmentRepository.save(department);
        }
        if(!s.equals("미정")){
            Section section = sectionRepository.findBySectionName(s);
            List<Staff> staffs = section.getStaff();
            staffs.add(staff);
            section.setStaff(staffs);
            sectionRepository.save(section);
        }

        return staffRepository.save(staff);
    }

    @DisplayName("회원가입 테스트")
    @Test
    @Transactional
    public void makeStaff(){
        Staff staff = createStaff("가나다", "asdfghjk", "asciassss", "ascia@naver.com", "미정", "미정", "미정", "미정");
        System.out.println(staff.getUserId());
        System.out.println(staff.getPassword());
    }
}