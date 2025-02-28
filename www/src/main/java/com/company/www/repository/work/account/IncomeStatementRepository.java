package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.IncomeStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface IncomeStatementRepository extends JpaRepository<IncomeStatement, Long> {
    IncomeStatement save(IncomeStatement incomeStatement);
    IncomeStatement findByWork(Work work);
    IncomeStatement findByStartDateAndEndDate(LocalDate start, LocalDate end);
}
