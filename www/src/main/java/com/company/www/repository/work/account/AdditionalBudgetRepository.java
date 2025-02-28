package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.AdditionalBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalBudgetRepository extends JpaRepository<AdditionalBudget, Long> {
    AdditionalBudget save(AdditionalBudget additionalBudget);
    AdditionalBudget findByWork(Work work);
}
