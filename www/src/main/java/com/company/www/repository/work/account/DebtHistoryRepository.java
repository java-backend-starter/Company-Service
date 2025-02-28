package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.DebtHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtHistoryRepository extends JpaRepository<DebtHistory, Long> {
    DebtHistory save(DebtHistory debtHistory);
    DebtHistory findByWork(Work work);
}
