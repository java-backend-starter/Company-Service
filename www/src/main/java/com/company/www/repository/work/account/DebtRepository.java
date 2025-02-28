package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.Debt;
import com.company.www.entity.work.sales.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    Debt save(Debt debt);
    Debt findByWork(Work work);
    Debt findByPatronAndStartAndItemAndTotal(Patron patron, LocalDate start, String item, long total);
}
