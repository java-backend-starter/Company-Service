package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.ComprehensiveIncomeStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ComprehensiveIncomeStatementRepository extends JpaRepository<ComprehensiveIncomeStatement, Long> {
    ComprehensiveIncomeStatement save(ComprehensiveIncomeStatement comprehensiveIncomeStatement);
    ComprehensiveIncomeStatement findByWork(Work work);
    ComprehensiveIncomeStatement findByStartDateAndEndDate(LocalDate start, LocalDate end);
}
