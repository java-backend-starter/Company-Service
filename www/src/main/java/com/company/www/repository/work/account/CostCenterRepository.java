package com.company.www.repository.work.account;

import com.company.www.entity.belong.Department;
import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.CostCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Long> {
    CostCenter save(CostCenter costCenter);
    CostCenter findByWork(Work work);
    CostCenter findByDepartment(Department department);
}
