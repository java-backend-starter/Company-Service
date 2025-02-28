package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.SoftwareBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftwareBillRepository extends JpaRepository<SoftwareBill, Long> {
    SoftwareBill save(SoftwareBill softwareBill);
    SoftwareBill findByWork(Work work);
}
