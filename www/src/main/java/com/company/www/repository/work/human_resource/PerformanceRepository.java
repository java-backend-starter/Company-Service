package com.company.www.repository.work.human_resource;

import com.company.www.entity.belong.Department;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.human_reasource.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    Performance save(Performance performance);
    Performance findByWork(Work work);
    List<Performance> findAllByDepartment(Department department);
}
