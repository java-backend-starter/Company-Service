package com.company.www.repository.work.account;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.account.StandardCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface StandardCostRepository extends JpaRepository<StandardCost, Long> {
    StandardCost save(StandardCost standardCost);
    StandardCost findByWork(Work work);
    StandardCost findByTypeAndItemAndGradeAndDate(String type, String item, String grade, LocalDate date);
}
