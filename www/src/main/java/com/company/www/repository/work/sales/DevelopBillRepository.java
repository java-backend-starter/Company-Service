package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.DevelopBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevelopBillRepository extends JpaRepository<DevelopBill, Long> {
    DevelopBill save(DevelopBill developBill);
    DevelopBill findByWork(Work work);
}
