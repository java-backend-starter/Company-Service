package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.EquityChangeStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquityChangeStatementRepository extends JpaRepository<EquityChangeStatement, Long> {
    EquityChangeStatement save(EquityChangeStatement equityChangeStatement);
    EquityChangeStatement findByWork(Work work);
}
