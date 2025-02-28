package com.company.www.repository.work.account;

import com.company.www.entity.work.account.CashFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CashFlowRepository extends JpaRepository<CashFlow, Long> {
    CashFlow save(CashFlow cashFlow);
    CashFlow findByCashFlowId(Long cashFlowId);
    void delete(CashFlow cashFlow);
}
