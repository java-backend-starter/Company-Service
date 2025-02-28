package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.BalanceSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceSheetRepository extends JpaRepository<BalanceSheet, Long> {
    BalanceSheet save(BalanceSheet balanceSheet);
    BalanceSheet findByWork(Work work);
}
