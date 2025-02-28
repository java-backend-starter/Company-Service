package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.ActualCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActualCostRepository extends JpaRepository<ActualCost, Long> {
    ActualCost save(ActualCost actualCost);
    ActualCost findByWork(Work work);
}
