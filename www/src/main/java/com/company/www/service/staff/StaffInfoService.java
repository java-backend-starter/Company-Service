package com.company.www.service.staff;

import com.company.www.entity.belong.Department;
import com.company.www.entity.staff.Staff;
import com.company.www.repository.belong.DepartmentRepository;
import com.company.www.repository.staff.StaffRepository;
import com.company.www.repository.work.WorkTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Service
public class StaffInfoService {
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    WorkTypeRepository workTypeRepository;

    public void getInfo(Model model, Principal principal){
        Staff staff = staffRepository.findByUserId(principal.getName());
        List<String> departmentNames = new ArrayList<>();
        if(staff.getDepartment().getDepartmentName().equals("경영진")){
            List<Department> departments = departmentRepository.findAll();
            for(Department department : departments){
                departmentNames.add(department.getDepartmentName());
            }
        }
        else {
            departmentNames.add(staff.getDepartment().getDepartmentName());
        }

        model.addAttribute("name", staff.getStaffName());
        model.addAttribute("position", staff.getPosition().getPositionName());
        model.addAttribute("role", staff.getRole().getRoleName());
        model.addAttribute("workLink", "/" + staff.getDepartment().getDepartmentEnglishName()+"/work");
        model.addAttribute(departmentNames);
    }

}