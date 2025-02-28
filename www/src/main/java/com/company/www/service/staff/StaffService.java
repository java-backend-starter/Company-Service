package com.company.www.service.staff;


import com.company.www.converter.Converter;
import com.company.www.dto.staff.StaffDto;
import com.company.www.entity.belong.*;
import com.company.www.entity.staff.Staff;
import com.company.www.repository.belong.*;
import com.company.www.repository.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class StaffService implements UserDetailsService {

    @Autowired
    PositionRepository positionRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    StaffRepository staffRepository;

    public void validateDuplicateMember(Staff staff){
        Staff isExisted = staffRepository.findByUserId(staff.getUserId());
        if(isExisted != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }


    public Staff createStaff(StaffDto staffDto, PasswordEncoder passwordEncoder){
        Staff staff = new Staff();

        staff.setUserId(staffDto.getUserId());
        staff.setPassword(passwordEncoder.encode(staffDto.getPassword()));

        LocalDate start = LocalDate.now();
        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        staff.setEmployDate(start);
        staff.setResign(null);

        staff.setStaffName(staffDto.getStaffName());
        staff.setGender(Converter.convetToGender(staffDto.getGender()));
        staff.setBirthDate(staffDto.getBirthDate());

        staff.setEmail(staffDto.getEmail());
        staff.setAddress(staffDto.getAddress());
        staff.setContact(staffDto.getContact());

        staff.setPosition(positionRepository.findByPositionName("미정"));
        staff.setRole(roleRepository.findByRoleName("미정"));
        staff.setDepartment(departmentRepository.findByDepartmentName("미정"));
        staff.setSection(sectionRepository.findBySectionName("미정"));

        return staffRepository.save(staff);
    }

    public void updateStaff(Long id, StaffDto staffDto, Boolean isHumanResource, PasswordEncoder passwordEncoder){
        Staff staff = staffRepository.findByStaffId(id);
        if(isHumanResource){
            Role role = roleRepository.findByRoleName(staffDto.getRole());
            Position position = positionRepository.findByPositionName(staffDto.getPosition());
            Department department = departmentRepository.findByDepartmentName(staffDto.getDepartment());
            Section section = sectionRepository.findBySectionName(staffDto.getSection());

            staff.setStaffName(staffDto.getStaffName());
            staff.setGender(Converter.convetToGender(staffDto.getGender()));
            staff.setBirthDate(staffDto.getBirthDate());

            staff.setEmail(staffDto.getEmail());
            staff.setAddress(staffDto.getAddress());
            staff.setContact(staffDto.getContact());

            staff.setPosition(position);
            staff.setRole(role);

            staff.setDepartment(department);
            staff.setSection(section);

            staff.setEmployDate(staffDto.getEmployDate());

            staffRepository.save(staff);
        }
        else{
            staff.setPassword(passwordEncoder.encode(staffDto.getPassword()));
            staff.setEmail(staffDto.getEmail());
            staff.setAddress(staffDto.getAddress());
            staff.setContact(staffDto.getContact());
            staffRepository.save(staff);
        }
    }

    public void getInfo(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());

        model.addAttribute("name", staff.getStaffName());
        model.addAttribute("position", staff.getPosition().getPositionName());
        model.addAttribute("role", staff.getRole().getRoleName());
        model.addAttribute("workLink", "/" + staff.getDepartment().getDepartmentEnglishName()+"/work");
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Staff staff = staffRepository.findByUserId(userId);

        if(staff == null){
            throw new UsernameNotFoundException(userId);
        }

        return User.builder()
                .username(staff.getUserId())
                .password(staff.getPassword())
                .roles(staff.getRole().getRoleName())
                .build();
    }
}