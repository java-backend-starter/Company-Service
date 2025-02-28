package com.company.www.repository.work.account;

import com.company.www.entity.work.account.EquityChange;
import com.company.www.entity.work.account.EquityChangeStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquityChangeRepository extends JpaRepository<EquityChange, Long> {
    EquityChange save(EquityChange equityChange);
    void delete(EquityChange equityChange);
    EquityChange findByEquityChangeId(Long equityChangeId);
    List<EquityChange> findAllByEquityChangeStatement(EquityChangeStatement equityChangeStatement);
}
