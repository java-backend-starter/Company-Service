package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.CashFlowStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashFlowStatementRepository extends JpaRepository<CashFlowStatement, Long> {
    CashFlowStatement save(CashFlowStatement cashFlowStatement);
    CashFlowStatement findByWork(Work work);
}
