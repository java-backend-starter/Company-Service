package com.company.www.repository.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.belong.Section;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Budget save(Budget budget);
    Budget findByWork(Work work);
    Budget findByDepartmentAndSection(Department department, Section section);
}
