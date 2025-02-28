package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.DevelopOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DevelopOrderRepository extends JpaRepository<DevelopOrder, Long> {
    DevelopOrder save(DevelopOrder developOrder);
    DevelopOrder findByWork(Work work);
    DevelopOrder findByOrderCodeAndOrderDate(String orderCode, LocalDate orderDate);
}
