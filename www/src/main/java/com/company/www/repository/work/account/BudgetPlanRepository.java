package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.Budget;
import com.company.www.entity.work.account.BudgetPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BudgetPlanRepository extends JpaRepository<BudgetPlan, Long> {
    BudgetPlan save(Budget budget);
    BudgetPlan findByWork(Work work);
}
