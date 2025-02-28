package com.company.www.repository.staff;

import com.company.www.entity.belong.*;
import com.company.www.entity.staff.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Staff findByStaffId(Long id);
    Staff findByUserId(String userId);
    Staff findByStaffName(String staffName);
    Staff findByRoleAndPosition(Role role, Position position);

    Page<Staff> findAll(Pageable pageable);
    List<Staff> findAll();

    List<Staff> findAllByDepartment(Department department);
    List<Staff> findAllBySection(Section section);

    List<Staff> findAllByRole(Role role);
    List<Staff> findAllByPosition(Position position);

    Staff save(Staff staff);

}