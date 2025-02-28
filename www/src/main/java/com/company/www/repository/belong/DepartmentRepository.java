package com.company.www.repository.belong;

import com.company.www.entity.belong.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department save(Department department);
    void delete(Department department);

    Department findByDepartmentId(Long id);
    Department findByDepartmentName(String departmentName);

    List<Department> findAll();
}
