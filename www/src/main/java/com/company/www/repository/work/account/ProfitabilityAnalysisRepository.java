package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.ProfitabilityAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfitabilityAnalysisRepository extends JpaRepository<ProfitabilityAnalysis, Long> {
    ProfitabilityAnalysis save(ProfitabilityAnalysis profitabilityAnalysis);
    ProfitabilityAnalysis findByWork(Work work);
}
