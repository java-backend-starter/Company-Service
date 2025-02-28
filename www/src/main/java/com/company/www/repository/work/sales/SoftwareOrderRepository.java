package com.company.www.repository.work.sales;

import com.company.www.entity.work.Work;
import com.company.www.entity.work.sales.SoftwareOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SoftwareOrderRepository extends JpaRepository<SoftwareOrder, Long> {
    SoftwareOrder save(SoftwareOrder softwareOrder);
    SoftwareOrder findByWork(Work work);
    SoftwareOrder findByOrderCodeAndOrderDate(String orderCode, LocalDate orderDate);
}
